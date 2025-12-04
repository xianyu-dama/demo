package com.xianyu.component.ddd.event.sender;

import com.xianyu.component.ddd.event.DomainEvent;
import com.xianyu.component.utils.json.JsonUtils;
import java.util.function.Consumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * <br/>
 * Created on : 2023-09-01 10:17
 * @author xian_yu_da_ma
 */
@Service
@ConditionalOnProperty(name = "domain.event.sender.type", havingValue = "rocket-mq")
public class DomainEventSenderInRocketMq implements DomainEventSender {


    @Override
    public void send(DomainEvent event, Consumer<DomainEvent> callback) {
        System.out.println("发送领域事件：" + JsonUtils.toJSONString(event));
    }
}
