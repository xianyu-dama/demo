package com.xianyu.order.context.order.infr.persistence.repository;

import com.xianyu.order.context.Tables;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPoFetcher;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPoTable;
import java.util.List;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.jspecify.annotations.NonNull;

public interface OrderItemPoRepository extends JRepository<OrderItemPo, Long> {

    OrderItemPoTable ORDER_ITEM_PO_TABLE = Tables.ORDER_ITEM_PO_TABLE;
    OrderItemPoFetcher ORDER_ITEM_FIELDS = OrderItemPoFetcher.$.allScalarFields();

    default List<OrderItemPo> listById(@NonNull Long orderId) {
        return sql().createQuery(ORDER_ITEM_PO_TABLE)
                .where(ORDER_ITEM_PO_TABLE.orderId().eq(orderId))
                .select(ORDER_ITEM_PO_TABLE.fetch(ORDER_ITEM_FIELDS))
                .execute();
    }
}
