package com.xianyu.order.context.order.app.dto;

import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import jakarta.validation.constraints.NotBlank;

/**
 * 更新订单地址<br/>
 * Created on : 2023-09-16 10:29
 * @author xian_yu_da_ma
 */
public record UpdateAddressDto(long orderId,
                               @NotBlank
                                    String email,
                               @NotBlank
                                    String phoneNumber, String firstName,
                               String lastName, String addressLine1, String addressLine2, String country) {

    public OrderAddress toOrderAddress() {
        return OrderAddress.builder()
            .fullName(FullName.of(firstName(), lastName()))
            .fullAddressLine(FullAddressLine.of(addressLine1(), addressLine2()))
            .country(country())
            .email(email())
            .phoneNumber(phoneNumber())
            .build();
    }

}
