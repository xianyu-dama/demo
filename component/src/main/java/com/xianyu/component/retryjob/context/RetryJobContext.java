package com.xianyu.component.retryjob.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.annotation.RetryJobLimiter;
import com.xianyu.component.retryjob.context.handler.DefaultRetryJobHandler;
import com.xianyu.component.retryjob.context.handler.RetryJobHandler;
import com.xianyu.component.retryjob.context.handler.RetryJobHandlers;
import com.xianyu.component.retryjob.context.handler.SimpleRetryJobHandler;
import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.utils.ExpressionUtils;
import com.xianyu.component.utils.json.JsonUtils;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryJobContext {

    /**
     * 重试栈
     */
    private static final ThreadLocal<List<RetryJobContext>> RETRY_JOB_STACK = new TransmittableThreadLocal<>();

    @NonNull
    private RetryJob retryJobAnnotation;
    /**
     * 任务参数
     */
    @NonNull
    private RetryJobDo retryJob;
    /**
     * 任务处理器
     */
    @NonNull
    private RetryJobHandler retryJobHandler;

    /**
     * 运行时类型
     */
    @NonNull
    private RunTimeType runTimeType;


    public static RetryJobContext ofFirst(RetryJob retryJob, ProceedingJoinPoint invocation, Supplier<String> defaultTaskLockIdSupplier) {
        RetryJobDo retryJobDo = buildRetryJobDo(retryJob, invocation, defaultTaskLockIdSupplier);
        return builder()
                .retryJob(retryJobDo)
                .retryJobAnnotation(retryJob)
                .retryJobHandler(RetryJobHandlers.wrap((SimpleRetryJobHandler) x -> invocation.proceed()))
                .runTimeType(RunTimeType.FIRST)
                .build();
    }

    public static RetryJobContext ofRetry(RetryJobDo retryJob, DefaultRetryJobHandler handler) {
        return builder()
                .retryJob(retryJob)
                .retryJobAnnotation(handler.getRetryJobAnnotation())
                .retryJobHandler(handler)
                .runTimeType(RunTimeType.RETRY)
                .build();
    }

    private static RetryJobDo buildRetryJobDo(RetryJob retryJob, ProceedingJoinPoint invocation, Supplier<String> taskLockIdSupplier) {
        MethodSignature signature = (MethodSignature) invocation.getSignature();
        Method method = signature.getMethod();
        Object[] args = invocation.getArgs();
        LocalDateTime delayTimes = getDelayTimes(retryJob, method, args);
        String keyExpr = retryJob.key();
        if (ExpressionUtils.isEl(keyExpr)) {
            keyExpr = String.valueOf(ExpressionUtils.parseExpression(keyExpr, method, args));
        }
        if (StringUtils.isBlank(keyExpr)) {
            keyExpr = taskLockIdSupplier.get();
        }
        Assert.notEmpty(args, "args不能为空");

        return RetryJobDo.builder()
                .key(String.valueOf(keyExpr))
                .content(JsonUtils.toJSONString(args[0]))
                .currentRetryTimes(0)
                .maxRetryTimes(retryJob.maxRetryTimes())
                .type(retryJob.value())
                .status(RetryJobStatusEnum.PROCESSING)
                .nextExecuteTime(delayTimes)
                .build();
    }

    private static LocalDateTime getDelayTimes(RetryJob retryJob, Method method, Object[] args) {
        String delayTimes = retryJob.delayTime();
        if (StringUtils.isNumeric(delayTimes)) {
            return LocalDateTime.now().plus(Long.parseLong(delayTimes), retryJob.delayTimeUnit());
        }
        return parseDelayTimeBySystemProperty(retryJob, delayTimes, method, args);
    }

    /**
     * delayTimes 可以是：
     * 1. "${test.delay:10}"            -> 占位符
     * 2. "#dto.times"                  -> 方法参数值
     * 3. "${test.delay:10} + #dto.times" -> 混合
     */
    private static LocalDateTime parseDelayTimeBySystemProperty(RetryJob retryJob, String delayTimes,
                                                         Method method, Object[] args) {
        var valueObj = ExpressionUtils.parseExpression(delayTimes, method, args);

        if (valueObj instanceof LocalDateTime lt) {
            return lt;
        }

        long delayTimeNum = Long.parseLong(String.valueOf(valueObj));

        return LocalDateTime.now().plus(delayTimeNum, retryJob.delayTimeUnit());
    }

    public static void start(RetryJobContext retryJobContext) {
        getContextStack().add(retryJobContext);
    }

    public static void end() {
        List<RetryJobContext> contextList = getContextStack();
        if (Objects.isNull(contextList) || contextList.isEmpty()) {
            return;
        }
        contextList.remove(contextList.size() - 1);
    }

    private static List<RetryJobContext> getContextStack() {
        return Optional.ofNullable(RETRY_JOB_STACK.get())
                .orElseGet(() -> {
                    RETRY_JOB_STACK.set(new ArrayList<>());
                    return RETRY_JOB_STACK.get();
                });
    }

    public static Optional<RetryJobContext> current() {
        return getContextStack().stream().findFirst();
    }


    public static Optional<RetryJobContext> first() {
        return getContextStack().stream().findFirst();
    }

    public static boolean isOnRetry(RetryJob retryJobType) {
        if (getContextStack().size() != 1) {
            return false;
        }
        return first()
                .filter(RetryJobContext::isOnRetry)
                .map(RetryJobContext::getRetryJob)
                .map(RetryJobDo::getType)
                .map(type -> Objects.equals(type, retryJobType.value()))
                .orElse(false);
    }

    public void addRetryTimesIfOnRetry() {
        if (isOnRetry()) {
            getRetryJob().addTimes();
        }
    }

    public RetryJobLimiter getRetryJobLimiter() {
        return getRetryJobAnnotation().limiter();
    }

    public boolean isOnRetry() {
        return getRunTimeType() == RunTimeType.RETRY;
    }


    enum RunTimeType {
        /**
         * 非重试
         */
        FIRST,
        /**
         * 重试
         */
        RETRY,

    }

}
