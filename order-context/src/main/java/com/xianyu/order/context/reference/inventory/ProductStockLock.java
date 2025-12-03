package com.xianyu.order.context.reference.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record ProductStockLock(
        @NonNull
        @NotNull(message = "locked must not be null")
        Boolean locked,

        @NonNull
        @NotNull(message = "lockId must not be null")
        String stockLockId
) {}
