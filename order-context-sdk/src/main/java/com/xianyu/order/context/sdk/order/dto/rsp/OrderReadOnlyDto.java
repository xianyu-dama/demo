package com.xianyu.order.context.sdk.order.dto.rsp;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.jspecify.annotations.NonNull;

/**
 * <br/>
 * Created on : 2025-11-18 16:44
 *
 * @param cancelFlag 由聚合根赋值，具体判断逻辑在聚合根中实现
 * @author xian_yu_da_ma
 */
@Jacksonized
@Builder(toBuilder = true)
public record OrderReadOnlyDto(
        Long orderId,
        String orderStatus,
        String currency,
        BigDecimal exchangeRate,
        BigDecimal shouldPay,
        BigDecimal actualPay,
        OrderAddressDto orderAddress,
        List<OrderDetailDto> orderDetails,
        ExtensionDto extension,
        Long userId,
        @NonNull Boolean cancelFlag,
        @NonNull Boolean shippedFlag,
        @NonNull Boolean canCancelFlag
) {

    public boolean isShipped() {
        return shippedFlag;
    }

    public boolean canCancel() {
        return canCancelFlag;
    }

    public boolean isCanceled() {
        return cancelFlag;
    }
}
