package com.xianyu.order.context.sdk.order.dto.rsp;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * <br/>
 * Created on : 2025-11-18 16:51
 *
 * @author xian_yu_da_ma
 */
@Jacksonized
@Builder(toBuilder = true)
public record ExtensionDto(BigDecimal shipInsuranceFee, Boolean isFirstOrder, String lockId) {
}
