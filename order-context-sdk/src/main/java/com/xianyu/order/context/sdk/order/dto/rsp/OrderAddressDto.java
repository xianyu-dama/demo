package com.xianyu.order.context.sdk.order.dto.rsp;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * <br/>
 * Created on : 2025-11-18 16:49
 *
 * @author xian_yu_da_ma
 */
@Jacksonized
@Builder(toBuilder = true)
public record OrderAddressDto(FullNameDto fullName, FullAddressLineDto fullAddressLine, String email,
                              String phoneNumber, String country) {
}
