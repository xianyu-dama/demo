package com.xianyu.order.context.order.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * 生单指令<br/>
 * Created on : 2023-09-06 13:00
 * @author xian_yu_da_ma
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class PlaceOrderDetailDto {

    @NotNull(message = "productId cannot be empty")
    private final Integer productId;


    @Positive(message = "price must be greater than 0")
    private final BigDecimal price;

    @NonNull
    @Positive(message = "quantity must be greater than 0")
    private final Integer quantity;

}
