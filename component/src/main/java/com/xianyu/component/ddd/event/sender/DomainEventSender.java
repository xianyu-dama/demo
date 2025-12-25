package com.xianyu.component.ddd.event.sender;

import com.xianyu.component.ddd.event.DomainEvent;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * 领域事件发送<br/>
 * Created on : 2022-03-01 13:34
 *
 * @author xian_yu_da_ma
 */
public interface DomainEventSender {

    /**
     * 1.持久化消息，方便重试
     * 2.发送领域事件
     * @param event
     */
    void send(DomainEvent event, Consumer<DomainEvent> callback);

    default void sendEvents(Queue<DomainEvent> events, Consumer<DomainEvent> callback) {
        while (!events.isEmpty()) {
            send(events.poll(), callback);
        }
    }

}
