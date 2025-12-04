package com.xianyu.order.context.order.infr.persistence.repository;

import com.xianyu.order.context.Tables;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPoFetcher;
import com.xianyu.order.context.order.infr.persistence.po.OrderPoTable;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.sql.ast.Selection;

/**
 * <br/>
 * Created on : 2025-11-06 09:43
 * @author xian_yu_da_ma
 */
public interface OrderPoRepository extends JRepository<OrderPo, Long> {

    OrderPoTable ORDER_PO_TABLE = Tables.ORDER_PO_TABLE;

    OrderPoFetcher FETCHER = OrderPoFetcher.$.allScalarFields()
            .orderItems(OrderItemPoRepository.ORDER_ITEM_FIELDS);

    Selection<OrderPo> ORDER_AGGREGATION_SELECTION = ORDER_PO_TABLE.fetch(FETCHER);
}
