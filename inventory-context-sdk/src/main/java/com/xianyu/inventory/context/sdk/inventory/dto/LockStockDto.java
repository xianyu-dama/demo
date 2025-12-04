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

    @NotEmpty(message = "products cannot be empty")
    private List<Product> products;

    @Jacksonized
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    @Builder(toBuilder = true)
    @Getter
    public static class Product {

        @NonNull
        @NotNull(message = "productId cannot be null")
        private Long productId;

        @NonNull
        @NotNull(message = "quantity cannot be null")
        @Positive(message = "quantity must be greater than 0")
        private Long quantity;
    }

}
