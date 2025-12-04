package com.xianyu.component.retryjob.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * RetryJob
 *
 * @author 
 * @date 2023/02/23
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryJobLimiter {
    /**
     * 时间范围内最大成功的任务数
     * <=0 或者 Integer.MAX_VALUE代表不限制
     */
    int maxSuccessJobCount();

    /**
     * 限流的时间范围值
     */
    long durationTime();

    /**
     * 触发限流后下次延迟多久执行,默认为0.5倍限流时间
     * 延迟时间= 限流范围的durationTime * delayRate
     */
    double delayRate() default 0.5;

    /**
     * 限流的时间范围的单位
     */
    ChronoUnit timeUnit();


}
