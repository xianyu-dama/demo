package com.xianyu.order.context.order.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

/**
 * 生单指令<br/>
 * Created on : 2023-09-06 13:00
 *
 * @author xian_yu_da_ma
 */
public record PlaceOrderDto(String currency, BigDecimal exchangeRate, BigDecimal shouldPay,
                            BigDecimal actualPay,
                            @Email(message = "邮箱格式不正确")
                            String email, String phoneNumber, String firstName,
                            String lastName, String addressLine1, String addressLine2, String country,
                            BigDecimal shipInsuranceFee,
                            Boolean isFirstOrder,
                            @NotEmpty
                            List<PlaceOrderDetailDto> details,
                            @NotNull Long userId) {

    public boolean isOnlyOnePerSku(int maxSkuCount) {
        return details().stream().map(PlaceOrderDetailDto::getQuantity).allMatch(Predicate.isEqual(maxSkuCount));
    }
}
