package com.xianyu.component.ddd.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Aspect
@Component
public class TransactionCheckAspect {

    @Before("execution(* com.xianyu.component.ddd.repository.GetWithLockRepository+.getWithLockOrThrow(..))"
            + " || @annotation(com.xianyu.component.ddd.aop.annotation.TransactionCheck)")
    public void checkTransaction(JoinPoint joinPoint) {
        boolean active = TransactionSynchronizationManager.isActualTransactionActive();
        Assert.isTrue(active, "方法 " + joinPoint.getSignature() + " 未开启事务");
    }

}