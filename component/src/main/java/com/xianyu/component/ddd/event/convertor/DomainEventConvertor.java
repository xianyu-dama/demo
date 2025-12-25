package com.xianyu.component.ddd.event.convertor;

import com.xianyu.component.ddd.event.DomainEvent;
import com.xianyu.component.ddd.event.persistence.DomainEventPo;
import com.xianyu.component.ddd.event.persistence.Immutables;
import com.xianyu.component.utils.json.JsonUtils;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2023-09-02 12:57
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public final class DomainEventConvertor {

    public static DomainEventPo toDomainEventPo(String topic, DomainEvent event) {
        return Immutables.createDomainEventPo(domainEventPo -> {
            domainEventPo.setTopic(topic);
            domainEventPo.setTag(event.getTag());
            domainEventPo.setBizId(event.getBizId());
            domainEventPo.setKey(event.getKey());
            domainEventPo.setContent(JsonUtils.toJSONString(event));
            domainEventPo.setMsgId(event.getMsgId());
        });
    }

    public static List<DomainEventPo> toDomainEventPos(String topic, Collection<DomainEvent> events) {
        return events.stream().map(event -> toDomainEventPo(topic, event)).toList();
    }

}
