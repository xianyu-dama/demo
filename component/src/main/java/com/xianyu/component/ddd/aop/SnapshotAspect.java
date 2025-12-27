package com.xianyu.component.ddd.aop;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SnapshotAspect {

    /**
     * 支持方法 or 注解
     *
     * @param pjp
     * @param returnVal
     */
    @AfterReturning(pointcut = "@annotation(com.xianyu.component.ddd.aop.annotation.Snapshot)"
        + "|| execution(* com.xianyu.component.ddd.repository.GetWithLockRepository+.getWithLockOrThrow(..))",
        returning = "returnVal")
    public void handleRequestMethod(JoinPoint pjp, Object returnVal) {
        if (returnVal instanceof BaseAggregation<?, ?> aggregate) {
            aggregate.attachSnapshot();
            return;
        }
        if (returnVal instanceof Optional<?> returnValOpt
            && returnValOpt.isPresent()
            && returnValOpt.get() instanceof BaseAggregation<?, ?> aggregate) {
            aggregate.attachSnapshot();
            return;
        }
    }

}