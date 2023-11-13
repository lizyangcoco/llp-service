package com.ruoyi.mini.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.mini.dto.SmsCodeLoginDTO;

/**
 * @author Lizyang
 * @date Created in 2023/11/9 20:33
 * @description 类描述
 */
public interface AuthService {


    /**
     * 发送注册登陆验证码
     *
     * @param mobile mobile
     */
    void sendMobileLoginCode(String mobile);

    /**
     * 手机号验证码登录注册
     *
     * @param mobileCodeLoginDTO mobileCodeLoginDTO
     */
    AjaxResult loginSmsByCode(SmsCodeLoginDTO mobileCodeLoginDTO);


}
