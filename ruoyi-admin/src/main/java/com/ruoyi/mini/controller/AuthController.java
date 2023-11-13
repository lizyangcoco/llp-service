package com.ruoyi.mini.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mini.dto.SmsCodeLoginDTO;
import com.ruoyi.mini.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lizyang
 * @date Created in 2023/11/9 20:30
 * @description 类描述
 */
@RestController
@RequestMapping("/auth")
@Api(tags = "小程序鉴权接口")
public class AuthController {
    @Autowired
    private AuthService authService;


    /**
     * 发送注册登陆验证码
     */
    @ApiOperation(value = "发送注册登陆验证码")
    @GetMapping("/sms/send")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "mobile 手机号", required = true, dataTypeClass = String.class)
    })
    public R<?> sendMobileLoginCode(@RequestParam String mobile) {
        authService.sendMobileLoginCode(mobile);
        return R.ok();
    }

    /**
     * 手机号验证码登录注册
     *
     * @param mobileCodeLoginDTO appCodeLoginDTO
     * @return String
     */
    @ApiOperation(value = "手机号验证码登录注册")
    @PostMapping("/sms/login")
    public AjaxResult loginByCode(@RequestBody @Validated SmsCodeLoginDTO mobileCodeLoginDTO) {
        return authService.loginSmsByCode(mobileCodeLoginDTO);
    }
}
