package com.xianyu.order.context.cart.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * <br/>
 * Created on : 2025-11-13 19:36
 *
 * @author xian_yu_da_ma
 */
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
@Getter
public class CartItem implements Serializable {

    private String itemId;
    private int quantity;

    void addQuantity(int quantity) {
        this.quantity += quantity;
    }

}
