package com.xianyu.order.context.reference.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * <br/>
 * Created on : 2023-12-28 22:57
 * @author xian_yu_da_ma
 */
@Builder
public record SkuStockLock(
        @NonNull
        @NotNull(message = "locked must not be null")
        Boolean locked,

        @NonNull
        @NotNull(message = "lockId must not be null")
        String stockLockId
) {

}
