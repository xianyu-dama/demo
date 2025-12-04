package com.xianyu.component.exception;

import lombok.Getter;

/**
 * <br/>
 * Created on : 2023-09-09 16:33
 * @author xian_yu_da_ma
 */
@Getter
public class BizException extends RuntimeException {

    private final String code = "ERROR";
    private String message;

    public BizException(String message) {
        this.message = message;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
