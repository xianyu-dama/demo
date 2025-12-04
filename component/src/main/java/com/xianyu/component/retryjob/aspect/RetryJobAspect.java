package com.xianyu.component.retryjob.aspect;

import com.xianyu.component.exception.BizException;
import com.xianyu.component.helper.TransactionHelper;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.context.RetryJobContext;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import com.xianyu.component.retryjob.service.RetryService;
import com.xianyu.component.utils.json.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@Component
@RequiredArgsConstructor
public class RetryJobAspect {

    private final RetryService retryService;

    private final TransactionHelper transactionHelper;

    private final Environment environment;

    private final RetryJobMapper retryJobMapper;

    /**
     *
     * 1. 无重试,第一次调用任务A
     * []
     * 2. 正在重试任务A
     * [A]
     * 3. 正在重试任务A,第一次调用任务B
     * [A]
     */
    @Around(value = "@annotation(annotation)")
    public Object around(ProceedingJoinPoint invocation, RetryJob annotation) throws Throwable {
        if (RetryJobContext.isOnRetry(annotation)) {
            return invocation.proceed();
        }
        RetryJobContext retryJobContext = buildFirstRetryJobContext(invocation, annotation);

        RetryJobContext.start(retryJobContext);
        try {
            RetryJobDo retryJob = retryJobContext.getRetryJob();
            boolean async = annotation.async();
            // 是否立即执行
            boolean execImmediately = retryJob.hasReachedExecuteTime();
            boolean asyncExecImmediately = execImmediately && async;
            boolean syncExecImmediately = execImmediately && !async;
            if (!annotation.idempotence()){
                removeIfExists(retryJob);
            }
            normalRun(retryJob, asyncExecImmediately, retryJobContext);
            // 同步立即执行
            if (syncExecImmediately) {
                retryService.process(retryJobContext);
            }
        } catch (DuplicateKeyException e) {
            if (annotation.idempotence()){
                log.error("[retry-job][{}]重试任务已存在,并设置了启用幂等,不执行真实方法,直接返回,{}",
                        annotation.value(),
                        JsonUtils.toJSONString(retryJobContext));
                return null;
            }
            log.error("[retry-job][{}]重试任务保存失败,{}", annotation.value(), JsonUtils.toJSONString(retryJobContext));
            throw new BizException("当前操作正在后台执行中,请勿重复操作");
        }finally {
            RetryJobContext.end();
        }
        return null;
    }

    private void removeIfExists(RetryJobDo retryJob) {
        retryJobMapper.get(retryJob.getType(), retryJob.getKey())
                .map(RetryJobDo::getId)
                .ifPresent(retryJobMapper::delete);
    }

    private void normalRun(RetryJobDo retryJob, boolean asyncExecImmediately, RetryJobContext retryJobContext) {
        transactionHelper.runWithRequired(()->{
            retryJobMapper.insert(retryJob);
            // 异步立即执行
            if (!asyncExecImmediately) {
                return;
            }
            // 事务提交后异步执行一次任务
            transactionHelper.asyncRunAfterCommit(() -> {
                try {
                    retryService.process(retryJobContext);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    private RetryJobContext buildFirstRetryJobContext(ProceedingJoinPoint invocation, RetryJob annotation) {
        return RetryJobContext.ofFirst(annotation, invocation, () ->
                String.valueOf(System.currentTimeMillis()));
    }

}
