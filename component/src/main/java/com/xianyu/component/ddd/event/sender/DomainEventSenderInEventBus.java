package com.xianyu.component.ddd.event.sender;

import com.google.common.eventbus.EventBus;
import com.xianyu.component.ddd.event.DomainEvent;
import com.xianyu.component.utils.json.JsonUtils;
import jakarta.annotation.Resource;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * <br/>
 * Created on : 2023-09-14 21:46
 * @author xian_yu_da_ma
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "domain.event.sender.type", havingValue = "event-bus")
public class DomainEventSenderInEventBus implements DomainEventSender {

    @Resource
    private EventBus eventBus;

    @Override
    public void send(DomainEvent event, Consumer<DomainEvent> callback) {
        log.info("发送领域事件 {}", JsonUtils.toJSONString(event));
        eventBus.post(event);
        callback.accept(event);
    }
}
