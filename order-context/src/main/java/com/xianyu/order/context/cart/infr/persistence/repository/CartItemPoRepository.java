package com.xianyu.order.context.cart.infr.persistence.repository;

import com.xianyu.order.context.Tables;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPo;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPoFetcher;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPoTable;
import org.babyfish.jimmer.spring.repository.JRepository;

public interface CartItemPoRepository extends JRepository<CartItemPo, Long> {

    CartItemPoTable CART_ITEM_PO_TABLE = Tables.CART_ITEM_PO_TABLE;

    CartItemPoFetcher CART_ITEM_FIELDS = CartItemPoFetcher.$.allScalarFields();

}