package com.xianyu.order.context.sdk.order.dto.rsp;

import java.math.BigDecimal;
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
public record OrderDetailDto(Long id, Integer productId, String orderStatus, BigDecimal price, Boolean locked,
                             int quantity) {
}
