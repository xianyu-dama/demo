package com.xianyu.component.retryjob.context.aware;

import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.annotation.RetryJobLimiter;
import com.xianyu.component.retryjob.context.RetryJobContext;
import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.utils.DateUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class RetryJobRateLimitInterceptor implements RetryJobContextInterceptor {

    private final RedissonClient redissonClient;

    private static boolean isSameConfig(RRateLimiter rateLimiter, RateLimiterConfig rateLimiterConfig) {
        RateLimiterConfig config = rateLimiter.getConfig();
        return Objects.equals(config.getRate(), rateLimiterConfig.getRate())
                && Objects.equals(config.getRateInterval(), rateLimiterConfig.getRateInterval())
                && Objects.equals(config.getRateType(), rateLimiterConfig.getRateType());
    }

    @Nullable
    private static RetryJobResult allow() {
        return null;
    }

    @NonNull
    private static String limiterName(RetryJob retryJob) {
        return String.format("RETRY_JOB_LIMITER:%s", retryJob.value());
    }

    @Nullable
    @Override
    public RetryJobResult beforeExecute(RetryJobContext context) {
        RetryJob retryJobAnnotation = context.getRetryJobAnnotation();
        RetryJobLimiter limiter = retryJobAnnotation.limiter();
        int maxSuccessCount = limiter.maxSuccessJobCount();
        if (maxSuccessCount <= 0 || maxSuccessCount == Integer.MAX_VALUE) {
            return allow();
        }
        RRateLimiter rateLimiter = getRateLimiter(retryJobAnnotation);
        if (rateLimiter.tryAcquire(1)) {
            log.info("[retry-job][限流器][{}]获取许可成功", context.getRetryJob().getType());
            return allow();
        }
        Duration duration = Duration.of(limiter.durationTime(), limiter.timeUnit());
        LocalDateTime nextExecuteTime = LocalDateTime.now()
                .plusNanos((long) (duration.toNanos() * limiter.delayRate()));

        log.info("[retry-job][限流器][{}]触发限流,延迟到[{}]执行", context.getRetryJob()
                .getType(), DateUtils.toString(nextExecuteTime));
        return RetryJobResult.delay(nextExecuteTime);
    }

    private synchronized RRateLimiter getRateLimiter(RetryJob retryJobAnnotation) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(limiterName(retryJobAnnotation));
        RateLimiterConfig limiterConfig = rateConfig(retryJobAnnotation.limiter());
        if (rateLimiter.isExists() && isSameConfig(rateLimiter, limiterConfig)) {
            return rateLimiter;
        }
        rateLimiter.setRate(limiterConfig.getRateType(), limiterConfig.getRate(), limiterConfig.getRateInterval(), RateIntervalUnit.MILLISECONDS);
        return rateLimiter;
    }

    public RateLimiterConfig rateConfig(RetryJobLimiter limiter) {
        long millis = Duration.of(limiter.durationTime(), limiter.timeUnit()).toMillis();
        return new RateLimiterConfig(RateType.OVERALL, millis, (long) limiter.maxSuccessJobCount());
    }

    @Override
    public void afterExecute(RetryJobContext context) {

    }

    @Override
    public Order order() {
        return LAST.up();
    }
}
