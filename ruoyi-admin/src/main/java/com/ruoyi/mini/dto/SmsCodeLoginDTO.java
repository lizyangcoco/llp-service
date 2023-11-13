package com.ruoyi.mini.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Lizyang
 * @date Created in 2023/11/9 23:55
 * @description 类描述
 */
@Data
public class SmsCodeLoginDTO {
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String mobile;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 唯一标识
     */
    @NotBlank(message = "不可为空")
    private String uuid;

}
