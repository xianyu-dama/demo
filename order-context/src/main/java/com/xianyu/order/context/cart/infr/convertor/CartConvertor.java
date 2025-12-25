package com.xianyu.order.context.cart.infr.convertor;

import com.xianyu.order.context.cart.domain.Cart;
import com.xianyu.order.context.cart.domain.CartItem;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPo;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPoDraft;
import com.xianyu.order.context.cart.infr.persistence.po.CartPo;
import com.xianyu.order.context.cart.infr.persistence.po.CartPoDraft;
import com.xianyu.order.context.reference.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * <br/>
 * Created on : 2025-11-13 22:38
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartConvertor {

    private final UserRepository userRepository;

    private static @NotNull List<CartItemPo> toItems(@NotNull Cart cart) {

        return cart.getItems().values().stream()
                .map(item ->
                        CartItemPoDraft.$.produce(itemDraft -> {
                            itemDraft.setItemId(item.getItemId());
                            itemDraft.setQuantity(item.getQuantity());
                        })
                ).toList();
    }

    public CartPo toCartPo(@NonNull Cart cart) {
        return CartPoDraft.$.produce(cartDraft -> {
            cartDraft.setUserId(cart.getUserId());
            cartDraft.setVersion(cart.getVersion());
            cartDraft.setItems(toItems(cart));
        });
    }

    public Cart toCart(@NonNull CartPo cartPo) {
        Cart cart = Cart.builder()
                .userId(cartPo.userId())
                .user(userRepository.getReference(cartPo.userId()))
                .version(cartPo.version())
                .build();
        cartPo.items()
                .forEach(itemPo -> {
                    CartItem item = CartItem.of(itemPo.itemId(), itemPo.quantity());
                    cart.getItems().put(item.getItemId(), item);
                });
        return cart;
    }

}
