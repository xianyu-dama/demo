package com.xianyu.component.ddd.event.handler;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

/**
 * <br/>
 * Created on : 2023-09-14 21:37
 * @author xian_yu_da_ma
 */
@Slf4j
public abstract class EventHandler<T> {

    protected EventHandler(EventBus eventBus) {
        log.info("regist eventbus {}", getClass());
        eventBus.register(this);
    }

    public abstract void handle(T t);

}
