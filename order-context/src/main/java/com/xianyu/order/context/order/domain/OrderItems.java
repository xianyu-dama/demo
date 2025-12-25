package com.xianyu.order.context.order.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.xianyu.component.ddd.aggregation.Identified;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.jspecify.annotations.NonNull;

/**
 * 订单聚合根关联对象-订单明细<br/>
 * 读方法：public
 * 写方法：default
 * Created on : 2022-03-07 19:57
 *
 * @author xian_yu_da_ma
 */
@Slf4j
public class OrderItems implements Iterable<OrderItem>, Serializable, Identified<OrderItem, Long> {

    private final List<OrderItem> list;

    /**
     * @JsonCreator 用于反序列化
     * @param list
     */
    @JsonCreator
    public OrderItems(List<OrderItem> list) {
        this.list = Objects.requireNonNullElseGet(list, ArrayList::new);
        validate();
    }

    void validate() {
        if (list.isEmpty()) {
            return;
        }
        if (isDuplicated()) {
            throw new IllegalArgumentException("订单明细id重复");
        }
    }

    @Override
    public @NonNull Iterator<OrderItem> iterator() {
        return list.iterator();
    }

    public Stream<OrderItem> toStream() {
        return list.stream();
    }

    @Override
    public Collection<OrderItem> getCollection() {
        return list;
    }

    @Override
    public Function<OrderItem, Long> identify() {
        return OrderItem::getId;
    }


    public List<Integer> getProductIds() {
        return StreamEx.of(list).map(OrderItem::getProductId).toList();
    }

    public int count() {
        return list.size();
    }

    void add(OrderItem orderItem) {
        list.add(orderItem);
    }
}
