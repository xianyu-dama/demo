package com.xianyu.order.context.cart.infr.adapter;

import com.xianyu.order.context.cart.domain.Cart;
import com.xianyu.order.context.cart.domain.CartRepository;
import com.xianyu.order.context.cart.infr.convertor.CartConvertor;
import com.xianyu.order.context.cart.infr.persistence.mapper.CartItemPoMapper;
import com.xianyu.order.context.cart.infr.persistence.mapper.CartPoMapper;
import com.xianyu.order.context.reference.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * TODO:xian_yu_da_ma 购物车仓储适配器
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepository {

    private final CartPoMapper cartPoMapper;
    private final CartItemPoMapper cartItemPoMapper;
    private final CartConvertor cartConvertor;
    private final UserRepository userRepository;

    @Override
    public Long add(@NonNull Cart aggregate) {
        return 0L;
    }

    @Override
    public Optional<Cart> get(Long id) {
        return Optional.empty();
    }

    @Override
    public int update(@NonNull Cart aggregate) {
        return 0;
    }
}
