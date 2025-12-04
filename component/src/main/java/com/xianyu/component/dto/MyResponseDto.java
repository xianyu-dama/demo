package com.xianyu.component.dto;

import java.util.Objects;
import lombok.NonNull;

/**
 * <br/>
 * Created on : 2023-09-06 13:02
 *
 * @param code 响应状态码
 * @param msg  响应描述
 * @param data 响应业务数据
 * @author xian_yu_da_ma
 */
public record MyResponseDto<R>(@NonNull String code, String msg, R data) {

    public static final String SUCCESS_CODE = "SUCCESS";

    public static <T> MyResponseDto<T> success(T data) {
        return new MyResponseDto<>(SUCCESS_CODE, "", data);
    }

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return Objects.equals(SUCCESS_CODE, code);
    }

}
