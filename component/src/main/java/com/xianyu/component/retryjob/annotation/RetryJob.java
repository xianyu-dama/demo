package com.xianyu.component.retryjob.annotation;

import com.xianyu.component.exception.BizException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * RetryJob
 * @author 
 * @date 2023/02/23
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryJob {

    /**
     * 任务类型
     */
    String value();

    /**
     * 当抛出此异常时重试
     */
    Class<? extends Throwable>[] retryWhenThrow() default {Throwable.class};

    /**
     * 当抛出此异常不重试
     */
    Class<? extends Throwable>[] notTryWhenThrow() default {BizException.class};

    /**
     * 最大重试次数
     */
    int maxRetryTimes() default 8;

    /**
     * SPEL表达式,相同任务类型的任务唯一键,若重复执行则会在方法执行前就报错
     */
    String key() default "";

    /**
     * 首次调用是否异步
     */
    boolean async() default false;

    /**
     * 延迟执行时间的单位
     * - delayTime 如果是el表达式且LocalDateTime类型的话不会生效
     */
    ChronoUnit delayTimeUnit() default ChronoUnit.SECONDS;

    /**
     * 默认不配置直接执行，延迟执行时间,支持使用配置参数和纯数字
     * 1. 纯数字,例如: 10
     * 2. 配置参数,例如: ${test.delay-times:10}
     * 3. el表达式,例如: #dto.times,必须是LocalDateTime类型,不是会报错
     */
    String delayTime() default "0";

    /**
     * 是否启用幂等
     * 1. false(不幂等)
     *  - 如果已经有相同类型+taskLockId都一致的任务,则报错
     * 2. true(幂等)
     *  - 如果已经有相同类型+taskLockId都一致的任务,则直接返回,不会报错,也不会执行真实的方法
     */
    boolean idempotence() default false;

    /**
     * 限流器,默认不限制
     * 作用：任务执行前会给予限流配置进行判断，如果当前时间到之前的时间范围内，同类型重试任务成功次数超过maxSuccessJobCount
     */
    RetryJobLimiter limiter() default @RetryJobLimiter(maxSuccessJobCount = Integer.MAX_VALUE, durationTime = 0, timeUnit = ChronoUnit.SECONDS);
}

