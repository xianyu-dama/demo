package com.xianyu.order.context.cart.infr.convertor;

import com.xianyu.component.ddd.composition.Reference;
import com.xianyu.order.context.cart.domain.Cart;
import com.xianyu.order.context.cart.domain.CartItem;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPo;
import com.xianyu.order.context.cart.infr.persistence.po.CartPo;
import com.xianyu.order.context.reference.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 购物车转换器
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class CartConvertor {

    /**
     * Po转领域对象
     */
    public Cart toCart(CartPo cartPo, List<CartItemPo> cartItemPos, User user) {
        Map<String, CartItem> items = new HashMap<>();
        for (CartItemPo itemPo : cartItemPos) {
            CartItem item = CartItem.of(itemPo.getItemId(), itemPo.getQuantity());
            items.put(itemPo.getItemId(), item);
        }
        
        return Cart.builder()
                .userId(cartPo.getUserId())
                .user(Reference.of(() -> user))
                .items(items)
                .build();
    }

    /**
     * 领域对象转Po
     */
    public CartPo toCartPo(Cart cart) {
        CartPo cartPo = new CartPo();
        cartPo.setUserId(cart.getUserId());
        cartPo.setVersion(0);
        return cartPo;
    }

    /**
     * 领域对象转CartItemPo列表
     */
    public List<CartItemPo> toCartItemPos(Cart cart) {
        return cart.getItems().values().stream()
                .map(item -> toCartItemPo(cart.getUserId(), item))
                .toList();
    }

    /**
     * CartItem转CartItemPo
     */
    private CartItemPo toCartItemPo(Long userId, CartItem cartItem) {
        CartItemPo itemPo = new CartItemPo();
        itemPo.setUserId(userId);
        itemPo.setItemId(cartItem.getItemId());
        itemPo.setQuantity(cartItem.getQuantity());
        itemPo.setDeleteTimestamp(0L);
        return itemPo;
    }

}
