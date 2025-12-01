package com.xianyu.order.context.cart.infr.persistence.repository;

import com.xianyu.order.context.Tables;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPoTable;
import com.xianyu.order.context.cart.infr.persistence.po.CartPo;
import com.xianyu.order.context.cart.infr.persistence.po.CartPoFetcher;
import com.xianyu.order.context.cart.infr.persistence.po.CartPoTable;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.sql.ast.Selection;

public interface CartPoRepository extends JRepository<CartPo, Long> {

    CartPoTable CART_PO_TABLE = Tables.CART_PO_TABLE;

    CartItemPoTable CART_ITEM_PO_TABLE = Tables.CART_ITEM_PO_TABLE;

    CartPoFetcher FETCHER = CartPoFetcher.$.allScalarFields()
        .items(CartItemPoRepository.CART_ITEM_FIELDS);

    Selection<CartPo> CART_AGGREGATION_SELECTION = CART_PO_TABLE.fetch(FETCHER);
}