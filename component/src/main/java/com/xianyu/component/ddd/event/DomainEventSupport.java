package com.xianyu.component.ddd.event;

import com.xianyu.component.ddd.aop.annotation.TransactionCheck;
import com.xianyu.component.ddd.event.common.Topics;
import com.xianyu.component.ddd.event.convertor.DomainEventConvertor;
import com.xianyu.component.ddd.event.persistence.DomainEventPo;
import com.xianyu.component.ddd.event.persistence.service.DomainEventPoService;
import com.xianyu.component.ddd.event.sender.DomainEventSender;
import com.xianyu.component.helper.TransactionHelper;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Queue;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2023-09-02 13:44
 * @author xian_yu_da_ma
 */
@Component
public class DomainEventSupport {

    @Resource
    public DomainEventSender domainEventSender;

    @Resource
    private DomainEventPoService domainEventPoService;

    @Resource
    private TransactionHelper transactionHelper;

    /**
     * 持久化之后，异步发送事件
     * @param events
     */
    @TransactionCheck
    public void sendEventAfterPersist(@NonNull Queue<DomainEvent> events) {
        List<DomainEventPo> domainEventPos = DomainEventConvertor.toDomainEventPos(Topics.ORDER, events);
        boolean success = domainEventPoService.saveBatch(domainEventPos);
        if (success) {
            transactionHelper.runAfterCommit(() -> {
                domainEventSender.sendEvents(events, event -> {
                    domainEventPoService.updateSent(event.getBizId());
                });
            });
        }
    }

}
