package com.xianyu.order.context.cart.app.service;

import com.xianyu.order.context.cart.domain.Cart;
import com.xianyu.order.context.cart.domain.CartItem;
import com.xianyu.order.context.cart.domain.CartRepository;
import com.xianyu.order.context.sdk.cart.api.ClearCartApiService;
import com.xianyu.order.context.sdk.order.api.OrderQueryApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <br/>
 * Created on : 2025-06-04 23:42
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class CartAppService implements ClearCartApiService {

    // modulith 报错是对的
    // private final OrderRepository orderRepository;

    private final OrderQueryApiService orderQueryApiService;

    private final CartRepository cartRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(long userId) {
        Cart cart = cartRepository.getExistedCart(userId);
        cart.clear();
        cartRepository.update(cart);
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(long userId, String itemId, int quantity) {
        var cart = cartRepository.getExistedCart(userId);
        cart.addItems(List.of(CartItem.of(itemId, quantity)));
        cartRepository.update(cart);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeItem(long userId, String itemId) {
        var cart = cartRepository.getExistedCart(userId);
        cart.removeItem(itemId);
        cartRepository.update(cart);
    }

}
