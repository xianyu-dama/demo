package com.xianyu.inventory.context.sdk.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.jspecify.annotations.NonNull;

/**
 * <br/>
 * Created on : 2025-11-18 09:20
 *
 * @author xian_yu_da_ma
 */
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
@Getter
public class LockStockDto {

    /**
     * 业务id 等价于 idempotentId
     */
    @NonNull
    @NotNull(message = "bizId cannot be null")
    private String bizId;

    @NotEmpty(message = "skus cannot be empty")
    private List<Sku> skus;

    @Jacksonized
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    @Builder(toBuilder = true)
    @Getter
    public static class Sku {

        @NonNull
        @NotNull(message = "skuId cannot be null")
        private Long skuId;

        @NonNull
        @NotNull(message = "quantity cannot be null")
        @Positive(message = "quantity must be greater than 0")
        private Long quantity;
    }

}
