package com.xianyu.order.context.order.domain.factory;

import com.xianyu.order.context.order.domain.OrderBuilder;
import com.xianyu.order.context.order.domain.service.validator.OrderValidator;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单聚合工厂：构造订单聚合
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFactory {

    private final Collection<OrderValidator> orderValidators;

    public OrderBuilder createOrderBuilder() {
        return new OrderBuilder(orderValidators);
    }

}