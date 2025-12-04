package com.xianyu.component.ddd.aop;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import com.xianyu.component.ddd.event.DomainEventSupport;
import com.xianyu.component.helper.TransactionHelper;
import com.xianyu.component.utils.json.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 更新聚合根，事务提交之后，刷新快照
 */
@Slf4j
@Aspect
@Component
public class UpdateAroundAspect {

    @Resource
    protected TransactionHelper transactionHelper;

    @Resource
    protected DomainEventSupport domainEventSupport;

    @Around("execution(* com.xianyu..*.UpdateRepository+.update(..))")
    public Object handleRequestMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] paramValues = proceedingJoinPoint.getArgs();
        if (paramValues[0] instanceof BaseAggregation<?, ?> aggregation) {
            aggregation.checkForVersion();
            try {
                var result = proceedingJoinPoint.proceed(paramValues);
                domainEventSupport.sendEventAfterPersist(aggregation.getEvents());
                return result;
            } catch (Throwable e) {
                log.error("聚合根更新异常 {} ", JsonUtils.toJSONString(aggregation), e);
                throw e;
            } finally {
                aggregation.removeSnapshot();
            }
        } else {
            return proceedingJoinPoint.proceed(paramValues);
        }
    }

}