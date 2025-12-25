package com.xianyu.order.context.cart.domain;

import com.xianyu.component.ddd.repository.CommonRepository;
import com.xianyu.component.helper.SpringHelper;
import com.xianyu.order.context.reference.user.UserRepository;

/**
 * <br/>
 * Created on : 2025-11-13 19:42
 *
 * @author xian_yu_da_ma
 */
public interface CartRepository extends CommonRepository<Cart, Long> {

    default Cart getExistedCart(long userId) {
        return get(userId).orElseGet(() -> {
            UserRepository userRepository = SpringHelper.getBean(UserRepository.class);
            add(Cart.of(userRepository.getOrThrow(userId)));
            return getOrThrow(userId);
        });
    }

}
