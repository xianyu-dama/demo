package com.xianyu.component.retryjob.service;

import com.xianyu.component.exception.BizException;
import com.xianyu.component.retryjob.RetryJobAlertInfo;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.config.RetryConfig;
import com.xianyu.component.retryjob.context.RetryJobContext;
import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.context.aware.MergedRetryJobContextInterceptor;
import com.xianyu.component.retryjob.context.aware.RetryJobContextInterceptor;
import com.xianyu.component.retryjob.context.handler.RetryJobHandler;
import com.xianyu.component.retryjob.context.handler.RetryJobHandlers;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import com.xianyu.component.utils.json.JsonUtils;
import com.xianyu.component.utils.validation.ValidatorUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class DefaultRetryService implements RetryService, InitializingBean {

    public static final String REACH_MAX_RETRY_TIMES_REASON = "达到最大重试次数，终止重试";

    public static final String THROW_NOT_RETRY_EXCEPTION = "抛出无需重试的异常，终止重试";

    private final RetryConfig retryConfig;

    private final List<RetryJobContextInterceptor> interceptors;

    private final RedissonClient redissonClient;

    private final RetryJobMapper retryJobMapper;

    private static Throwable getThrowable(Throwable e, boolean isOnRetry) {
        Throwable throwable = isOnRetry ? Optional.ofNullable(e.getCause()).orElse(e) : e;
        if (throwable != null && Objects.equals(throwable.getClass(), RuntimeException.class) && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    @Override
    public void process(RetryJobContext context) throws Throwable {
        ValidatorUtils.validate(context);

        RetryJobDo retryJobDo = context.getRetryJob();
        RLock lock = redissonClient.getLock("retry-job:" + retryJobDo.getType() + ":" + retryJobDo.getKey());
        boolean locked = lock.tryLock(0, 10, TimeUnit.SECONDS);
        if (!locked) {
            log.warn("{} [分布式锁未获取]", logPrefix(retryJobDo));
            return;
        }
        try {
            RetryJobHandler retryJobHandler = context.getRetryJobHandler();
            RetryJobContextInterceptor interceptor = getRetryJonInterceptor();

            RetryJobResult retryJobResult = beforeExecute(context, interceptor);
            if (retryJobResult == null) {
                retryJobResult = execute(context, retryJobHandler);
            }
            afterExecute(context, retryJobResult, interceptor);
        } finally {
            try {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (Exception e) {
                log.error("{} [解锁失败]", logPrefix(retryJobDo), e);
            }
        }
    }

    private RetryJobResult execute(RetryJobContext context, RetryJobHandler retryJobHandler) throws Throwable {
        context.addRetryTimesIfOnRetry();
        return execute(retryJobHandler, context.getRetryJob());
    }

    @NotNull
    private RetryJobContextInterceptor getRetryJonInterceptor() {
        return new MergedRetryJobContextInterceptor(interceptors);
    }


    private RetryJobResult beforeExecute(RetryJobContext context, RetryJobContextInterceptor interceptor) {
        return interceptor.beforeExecute(context);
    }

    private void afterExecute(RetryJobContext context, RetryJobResult jobResult, RetryJobContextInterceptor interceptor) throws Throwable {
        RetryJobDo retryJobDo = context.getRetryJob();
        switch (jobResult.getResultType()) {
            case SUCCESS:
                successFinish(retryJobDo);
                break;
            case FAIL:
                handleFail(context, getThrowable(jobResult.getThrowable(), context.isOnRetry()));
                break;
            case DELAY:
                handleDelay(context, jobResult);
                break;
            default:
                throw new BizException(String.format("未知的执行结果:%s", JsonUtils.toJSONString(retryJobDo)));
        }
        interceptor.afterExecute(context);
    }

    private RetryJobResult execute(RetryJobHandler retryJobHandler, RetryJobDo retryJobDo) throws Throwable {
        return RetryJobHandlers.wrap(retryJobHandler).invoke(retryJobDo);
    }

    private void handleDelay(RetryJobContext context, RetryJobResult jobResult) {
        RetryJobDo retryJob = context.getRetryJob();
        RetryJobDo updateRetryJob = retryJob.delay(jobResult.getNextExecuteTime());
        retryJobMapper.update(updateRetryJob);
        log.error("{} [延迟执行][待重试]下次执行时间[{}]", logPrefix(updateRetryJob), updateRetryJob.getNextExecuteTime());
    }

    private void handleFail(RetryJobContext context, Throwable e) throws Throwable {
        RetryJobDo retryJobDo = context.getRetryJob();
        // 到达最大重试次数
        if (retryJobDo.reachMaxRetryTimes()) {
            // 失败终止
            failFinish(context,retryJobDo, REACH_MAX_RETRY_TIMES_REASON, e);
            return;
        }
        // 如果需要重试,则标记失败
        if (isNeedRetry(context, e)) {
            // 标记失败等待重试
            failWaitRetry(retryJobDo, e);
        } else {
            // 失败终止
            failFinish(context, retryJobDo, THROW_NOT_RETRY_EXCEPTION, e);
        }
    }

    private boolean isNeedRetry(RetryJobContext retryJobContext, Throwable e) {
        RetryJob retryJob = retryJobContext.getRetryJobAnnotation();
        Class<? extends Throwable> eClass = e.getClass();
        return Arrays.stream(retryJob.retryWhenThrow())
                .anyMatch(exception -> exception.isAssignableFrom(eClass))
                && Arrays.stream(retryJob.notTryWhenThrow()).noneMatch(exception -> exception.isAssignableFrom(eClass));
    }

    /**
     * 失败待重试
     */
    private void failWaitRetry(RetryJobDo retryJobDo, Throwable e) {
        log.error("{} [失败][待重试]", logPrefix(retryJobDo), e);

        retryJobDo.failedWaitRetry(retryConfig.getIntervalMinutes(), e.getMessage());

        retryJobMapper.update(retryJobDo);
    }

    /**
     * 成功终止
     */
    private void successFinish(RetryJobDo retryJobDo) {
        log.info("{} [成功][终止]", logPrefix(retryJobDo));
        retryJobDo.success();
        retryJobMapper.update(retryJobDo);
    }

    /**
     * 失败终止
     */
    private void failFinish(RetryJobContext context, RetryJobDo retryJobDo, String failReason, Throwable e) throws Throwable {
        log.error("{} [失败][终止]", logPrefix(retryJobDo), e);
        String errorMsg = String.format("%s [失败][终止],失败原因 = %s", logPrefix(retryJobDo), e.getMessage());


        retryJobDo.failedFinish(failReason + ",错误信息:" + e.getMessage());
        // 报警
        alert(RetryJobAlertInfo.builder()
                .title("重试任务执行失败")
                .message(errorMsg)
                .retryJobId(retryJobDo.getId())
                .taskLockId(retryJobDo.getKey())
                .type(retryJobDo.getType())
                .build());
        retryJobMapper.update(retryJobDo);
        if (!context.isOnRetry()){
            throw e;
        }
    }

    private void alert(RetryJobAlertInfo alert) {
        log.warn("报警 {}", JsonUtils.toJSONString(alert));
    }


    /**
     * 日志前缀
     */
    protected String logPrefix(RetryJobDo retryJobDo) {
        return String.format("[retry-job][%s][key = %s]", retryJobDo.getType(), retryJobDo.getKey());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assertNotContainsDuplicateOrderInterceptors();
    }

    /**
     * 不能有重复顺序的拦截器
     */
    private void assertNotContainsDuplicateOrderInterceptors() {
        var order2Interceptors = interceptors.stream()
                .collect(Collectors.groupingBy(RetryJobContextInterceptor::order));
        var duplicateOrderInterceptors = order2Interceptors.values()
                .stream()
                .filter(retryJobContextInterceptors -> retryJobContextInterceptors.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(duplicateOrderInterceptors)) {
            throw new BizException(String.format("重试拦截器排序重复:%s", duplicateOrderInterceptors.stream().filter(Objects::nonNull).toList()));
        }
    }
}
