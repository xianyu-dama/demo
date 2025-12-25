package com.xianyu.order.context.cart.infr.adapter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xianyu.order.context.cart.domain.Cart;
import com.xianyu.order.context.cart.domain.CartRepository;
import com.xianyu.order.context.cart.infr.convertor.CartConvertor;
import com.xianyu.order.context.cart.infr.persistence.mapper.CartItemPoMapper;
import com.xianyu.order.context.cart.infr.persistence.mapper.CartPoMapper;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPo;
import com.xianyu.order.context.cart.infr.persistence.po.CartPo;
import com.xianyu.order.context.reference.user.User;
import com.xianyu.order.context.reference.user.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 购物车仓储适配器
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
    public Optional<Cart> get(Long userId) {
        CartPo cartPo = cartPoMapper.selectById(userId);
        if (cartPo == null) {
            return Optional.empty();
        }

        LambdaQueryWrapper<CartItemPo> query = Wrappers.lambdaQuery(CartItemPo.class);
        query.eq(CartItemPo::getUserId, userId);
        List<CartItemPo> cartItemPos = cartItemPoMapper.selectList(query);

        User user = userRepository.getOrThrow(userId);
        Cart cart = cartConvertor.toCart(cartPo, cartItemPos, user);
        return Optional.of(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Cart cart) {
        CartPo cartPo = cartConvertor.toCartPo(cart);
        cartPoMapper.insert(cartPo);

        List<CartItemPo> cartItemPos = cartConvertor.toCartItemPos(cart);
        for (CartItemPo itemPo : cartItemPos) {
            cartItemPoMapper.insert(itemPo);
        }

        return cart.getUserId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Cart cart) {
        // 先删除所有CartItem
        LambdaQueryWrapper<CartItemPo> deleteQuery = Wrappers.lambdaQuery(CartItemPo.class);
        deleteQuery.eq(CartItemPo::getUserId, cart.getUserId());
        cartItemPoMapper.delete(deleteQuery);

        // 重新插入CartItem
        List<CartItemPo> cartItemPos = cartConvertor.toCartItemPos(cart);
        for (CartItemPo itemPo : cartItemPos) {
            cartItemPoMapper.insert(itemPo);
        }

        // 更新CartPo（主要是version字段）
        CartPo cartPo = cartConvertor.toCartPo(cart);
        return cartPoMapper.updateById(cartPo);
    }

}
