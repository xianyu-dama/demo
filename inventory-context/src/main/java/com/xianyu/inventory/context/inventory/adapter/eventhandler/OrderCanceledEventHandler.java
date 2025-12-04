package com.xianyu.inventory.context.inventory.adapter.eventhandler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.xianyu.component.ddd.event.handler.EventHandler;
import com.xianyu.component.utils.json.JsonUtils;
import com.xianyu.order.context.sdk.order.event.OrderCanceledEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component(OrderCanceledEventHandler.PACKGAE_BEAN_NAME)
public class OrderCanceledEventHandler extends EventHandler<OrderCanceledEvent> {

    public static final String PACKGAE_BEAN_NAME = "inventory.orderCanceledEventHandler";

    public OrderCanceledEventHandler(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    @Subscribe
    public void handle(OrderCanceledEvent event) {
        log.info("收到订单取消事件:" + JsonUtils.toJSONString(event));
    }

}