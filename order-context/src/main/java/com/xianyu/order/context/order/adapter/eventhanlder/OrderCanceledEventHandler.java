package com.xianyu.order.context.order.adapter.eventhanlder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.xianyu.component.ddd.event.handler.EventHandler;
import com.xianyu.component.utils.json.JsonUtils;
import com.xianyu.order.context.sdk.order.event.OrderCanceledEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2023-09-08 09:06
 * @author xian_yu_da_ma
 */
@Slf4j
@Component
public class OrderCanceledEventHandler extends EventHandler<OrderCanceledEvent> {

    public OrderCanceledEventHandler(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    @Subscribe
    public void handle(OrderCanceledEvent event) {
        log.info("收到订单事件:" + JsonUtils.toJSONString(event));
    }

}