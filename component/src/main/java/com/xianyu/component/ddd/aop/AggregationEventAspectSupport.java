package com.xianyu.component.ddd.aop;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import com.xianyu.component.ddd.event.DomainEventSupport;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jetbrains.annotations.Nullable;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class AggregationEventAspectSupport {

    private final DomainEventSupport domainEventSupport;
    private final TransactionTemplate transactionTemplate;

    public @Nullable Object proceedAndSendEvents(ProceedingJoinPoint pjp,
                                                 BaseAggregation<?, ?> aggregation,
                                                 Object[] args) throws Throwable {
        boolean active = TransactionSynchronizationManager.isActualTransactionActive();
        if (active) {
            Object result = pjp.proceed(args);
            domainEventSupport.sendEventAfterPersist(aggregation.getEvents());
            return result;
        }
        return transactionTemplate.execute(status -> {
            try {
                Object result = pjp.proceed(args);
                domainEventSupport.sendEventAfterPersist(aggregation.getEvents());
                return result;
            } catch (Throwable t) {
                status.setRollbackOnly();
                throw new RuntimeException(t);
            }
        });
    }
}
