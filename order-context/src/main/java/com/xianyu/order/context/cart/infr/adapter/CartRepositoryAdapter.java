package com.xianyu.order.context.cart.infr.adapter;

import com.xianyu.order.context.cart.domain.Cart;
import com.xianyu.order.context.cart.domain.CartRepository;
import com.xianyu.order.context.cart.infr.convertor.CartConvertor;
import com.xianyu.order.context.cart.infr.persistence.repository.CartPoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import static com.xianyu.order.context.cart.infr.persistence.repository.CartPoRepository.FETCHER;

/**
 * <br/>
 * Created on : 2025-11-13 21:48
 *
 * @author xian_yu_da_ma
 */
@Repository
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepository {

    private final CartConvertor cartConvertor;

    private final CartPoRepository cartPoRepository;

    @Override
    public Long add(@NonNull Cart aggregate) {
        var cartPo = cartConvertor.toCartPo(aggregate);
        return cartPoRepository.save(cartPo).userId();
    }

    @Override
    public Optional<Cart> get(Long id) {
        return cartPoRepository.findById(id, FETCHER).map(cartConvertor::toCart);
    }

    @Override
    public int update(@NonNull Cart aggregate) {
        var cartPo = cartConvertor.toCartPo(aggregate);
        cartPoRepository.save(cartPo);
        return 1;
    }

}
