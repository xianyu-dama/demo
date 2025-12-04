package com.xianyu.order.context.order.app.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 前端只会传姓名，只更新姓名即可<br/>
 * Created on : 2023-09-17 21:34
 * @author xian_yu_da_ma
 */
public record UpdateFullNameDto(
        long orderId,
        @NotBlank
        String firstName,
        String lastName) {

}
