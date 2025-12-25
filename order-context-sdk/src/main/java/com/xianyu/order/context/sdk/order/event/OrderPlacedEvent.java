package com.xianyu.order.context.sdk.order.event;

import com.xianyu.component.ddd.event.DomainEvent;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;

/**
 * 生单事件<br/>
 * Created on : 2023-09-12 12:35
 * @author xian_yu_da_ma
 */
@Getter
public class OrderPlacedEvent extends DomainEvent {

    private final long orderId;

    private OrderPlacedEvent(@NonNull String key, @NonNull String bizId, long orderId) {
        super(key, bizId);
        this.orderId = orderId;
    }

    public static OrderPlacedEvent create(long orderId) {
        return new OrderPlacedEvent(Objects.toString(orderId), Objects.toString(orderId), orderId);
    }

    @Override
    public String getTag() {
        return "ORDER_PLACE";
    }
}
