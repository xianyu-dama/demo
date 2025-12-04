package com.xianyu.order.context.sdk.order.dto.rsp;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * <br/>
 * Created on : 2025-11-18 16:50
 *
 * @author xian_yu_da_ma
 */
@Jacksonized
@Builder(toBuilder = true)
public record FullAddressLineDto(String addressLine1, String addressLine2) {
}
