package com.xianyu.order.context.order.domain;

import com.xianyu.order.context.order.domain.service.validator.OrderValidator;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;

/**
 * 订单聚合构建器OrderBuilder，继承lombok的Builder<br/>
 * Created on : 2025-04-10 10:52
 * @author xian_yu_da_ma
 */
public class OrderBuilder extends Order.OrderBuilder<Order, OrderBuilder> {

    private final Collection<OrderValidator> orderValidators;

    private final List<OrderItem> builderOrderItems = new ArrayList<>();

    public OrderBuilder(Collection<OrderValidator> orderValidators) {
        this.orderValidators = orderValidators;
    }

    public OrderBuilder addOrderDetail(@NonNull OrderItem orderItem) {
        builderOrderItems.add(orderItem);
        return this;
    }

    @Override
    public OrderBuilder orderAddress(OrderAddress orderAddress) {
        // 业务校验
        super.orderAddress(orderAddress);
        return this;
    }

    @Override
    public OrderBuilder extension(Extension extension) {
        // 业务校验
        super.extension(extension);
        return this;
    }

    protected OrderBuilder self() {
        return this;
    }

    /**
     * 聚合根构造的过程保证业务一致性
     * @return
     */
    @Override
    public Order build() {

        Order order = new Order(this);
        builderOrderItems.forEach(order::add);

        boolean isValidate = order.isSkuQuantityValidate(Order.PLACE_ORDER_MAX_SKU_QUANTITY);
        if (!isValidate) {
            throw new IllegalArgumentException("订单明细数量超过限制");
        }

        // 对order做业务校验
        orderValidators.forEach(validator -> validator.validate(order));

        return order;
    }
}