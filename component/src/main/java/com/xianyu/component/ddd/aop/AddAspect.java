package com.xianyu.component.ddd.aop;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AddAspect {

    private final AggregationEventAspectSupport eventAspectSupport;

    @Around("execution(* com.xianyu..*.AddRepository+.add(..))")
    public @Nullable Object exexute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] paramValues = proceedingJoinPoint.getArgs();
        if (!(paramValues.length > 0 && paramValues[0] instanceof BaseAggregation<?, ?> aggregation)) {
            return proceedingJoinPoint.proceed(paramValues);
        }
        return eventAspectSupport.proceedAndSendEvents(proceedingJoinPoint, aggregation, paramValues);
    }

}
