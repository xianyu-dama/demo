package com.xianyu.order.context.order.app.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class PlaceDropshippingOrderDetailDto extends PlaceOrderDetailDto {

    @NotBlank(message = "unitPrice cannot be empty")
    String unitPrice;

    /**
     * @param productId
     * @param quantity
     * @param count
     * @param unitPrice
     */
    public PlaceDropshippingOrderDetailDto(Integer productId, Integer quantity, Integer count, String unitPrice) {
        super(productId, new BigDecimal(quantity), count);
        this.unitPrice = unitPrice;
    }

}
