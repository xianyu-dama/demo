package com.xianyu.order.context.sdk.order.event;

import com.xianyu.component.ddd.event.DomainEvent;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;

/**
 * 订单取消事件<br/>
 * Created on : 2023-09-02 20:15
 * @author xian_yu_da_ma
 */
@Getter
public class OrderCanceledEvent extends DomainEvent {

    private final long orderId;

    private OrderCanceledEvent(
            @NonNull String key,
            @NonNull String bizId, long orderId) {
        super(key, bizId);
        this.orderId = orderId;
    }

    public static OrderCanceledEvent create(long orderId) {
        return new OrderCanceledEvent(Objects.toString(orderId), Objects.toString(orderId), orderId);
    }

    @Override
    public String getTag() {
        return "ORDER_CANCELED";
    }

}
