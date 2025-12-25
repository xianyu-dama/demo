package com.xianyu.component.ddd.aop;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import com.xianyu.component.utils.json.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UpdateAspect {

    private final AggregationEventAspectSupport eventAspectSupport;


    @Around("execution(* com.xianyu..*.UpdateRepository+.update(..))")
    public Object exexute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object[] paramValues = proceedingJoinPoint.getArgs();
        if (!(paramValues[0] instanceof BaseAggregation<?, ?> aggregation)) {
            return proceedingJoinPoint.proceed(paramValues);
        }

        try {
            aggregation.checkForVersion();
            return eventAspectSupport.proceedAndSendEvents(proceedingJoinPoint, aggregation, paramValues);
        } catch (Throwable e) {
            log.error("聚合根更新异常 {} ", JsonUtils.toJSONString(aggregation), e);
            throw e;
        } finally {
            aggregation.removeSnapshot();
        }
    }

    

}
