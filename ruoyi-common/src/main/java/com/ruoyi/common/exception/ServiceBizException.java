package com.ruoyi.common.exception;


import lombok.Getter;
import lombok.Setter;

/**
 * @author: LizYang
 * @date: Created in 2022-07-01 10:18
 * @description: 统一封装的业务异常
 */
@Getter
@Setter
public class ServiceBizException extends RuntimeException {
    private Integer code;
    private String message;

    private ServiceBizException() {
    }

    public ServiceBizException(Exception e) {
        super(e);
    }

    public ServiceBizException(String msg) {
        super(msg);
        this.message = msg;
    }

    public ServiceBizException(String msg, Exception e) {
        super(msg, e);
        this.message = msg;
    }

    /**
     * 自定义业务code 异常
     *
     * @param code code
     * @param msg  信息
     */
    public ServiceBizException(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

}
