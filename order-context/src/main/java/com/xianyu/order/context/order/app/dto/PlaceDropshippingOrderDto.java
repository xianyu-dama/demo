package com.xianyu.order.context.order.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PlaceDropshippingOrderDto(
    @NotBlank(message = "orderId cannot be blank") String orderId,
    @NotEmpty(message = "order items cannot be empty") List<PlaceDropshippingOrderItemDto> details) {
}