package com.ruoyi.mini.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.ehyaya.core.exception.ServiceBizException;
import com.ehyaya.message.sms.SendSmsUtil;
import com.ehyaya.message.sms.dto.SendSmsInfoDTO;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.mini.dto.SmsCodeLoginDTO;
import com.ruoyi.mini.service.AuthService;
import com.ruoyi.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.common.core.domain.AjaxResult.error;

/**
 * @author Lizyang
 * @date Created in 2023/11/9 20:33
 * @description 类描述
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SendSmsUtil sendSmsUtil;
    private final String signName = "xxxxxxx";
    private final String templateCode = "xxxxxxx";
    private final String accessKeyId = "xxxxxxxxx";
    private final String accessKeySecret = "xxxxxxxxx";

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserDetailsService userDetailsService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private TokenService tokenService;

    /**
     * 发送注册登陆验证码
     *
     * @param mobile 手机号
     */
    @Override
    public void sendMobileLoginCode(String mobile) {
        // 验证码
        long code = Math.round((Math.random() * 9 + 1) * 1000);
        log.info("sendMobileLoginCode|发送注册登陆验证码={},{}", mobile, code);
        SendSmsInfoDTO sendSmsInfo = new SendSmsInfoDTO();
        // 短信六位数字
        sendSmsInfo.setContent(String.valueOf(code));
        // 短信主题
        sendSmsInfo.setSubject("动态验证码");
        // 短信收件人
        sendSmsInfo.setMobile(mobile);
        sendSmsUtil.sendCodeSms(sendSmsInfo);

        //发送短信前缀
        String sendSmsPrefixKey = "USER_SMS_LOGIN_";
        String key = sendSmsPrefixKey + mobile;
        redisCache.setCacheObject(key, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public AjaxResult loginSmsByCode(SmsCodeLoginDTO mobileCodeLoginDTO) {
        String sendSmsPrefixKey = "USER_SMS_LOGIN_";
        String mobile = mobileCodeLoginDTO.getMobile();
        String key = sendSmsPrefixKey + mobileCodeLoginDTO.getMobile();
        Object codeObject = redisCache.getCacheObject(key);
        if (ObjectUtil.isEmpty(codeObject)) {
            // 验证码已过期
            throw new ServiceBizException("验证码已过期");
        }

        String code = codeObject.toString();
        if (!code.equals(mobileCodeLoginDTO.getCode())) {
            // 验证码不正确
            throw new ServiceBizException("验证码不正确");
        }

        // 查询user表是否存在
        SysUser userInfo = userService.selectUserByUserName(mobile);
        if (StringUtils.isNull(userInfo)) {
            // 注册用户
            SysUser addUser = new SysUser();
            addUser.setNickName(mobile);
            addUser.setPassword("123456");
            addUser.setPhonenumber(mobile);
            addUser.setDeptId(201L);
            addUser.setRoleIds(new Long[]{100L});
            // 未知
            addUser.setSex("2");
            addUser.setStatus("0");
            addUser.setUserName(mobile);
            if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(addUser))) {
                return error("新增用户'" + addUser.getUserName() + "'失败，登录账号已存在");
            } else if (StringUtils.isNotEmpty(addUser.getPhonenumber())
                    && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(addUser))) {
                return error("新增用户'" + addUser.getUserName() + "'失败，手机号码已存在");
            } else if (StringUtils.isNotEmpty(addUser.getEmail())
                    && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(addUser))) {
                return error("新增用户'" + addUser.getUserName() + "'失败，邮箱账号已存在");
            }
            addUser.setCreateBy("admin");
            addUser.setPassword(SecurityUtils.encryptPassword(addUser.getPassword()));
            userService.insertUser(addUser);
        }

        //UserDetails userInfo = userDetailsService.loadUserByUsername(mobile);
        // 用户验证
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(mobile, "123456");
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(mobile, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(mobile, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(mobile, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        String token = tokenService.createToken(loginUser);

        // 生成令牌
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
