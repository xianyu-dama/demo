package com.xianyu.component.retryjob.context.aware;

import com.xianyu.component.retryjob.context.RetryJobContext;
import com.xianyu.component.retryjob.context.RetryJobResult;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class MergedRetryJobContextInterceptor implements RetryJobContextInterceptor {

    private final List<RetryJobContextInterceptor> interceptors;

    @Nullable
    @Override
    public RetryJobResult beforeExecute(RetryJobContext context) {
        List<RetryJobContextInterceptor> sortedInterceptors = sortedInterceptors();
        for (RetryJobContextInterceptor interceptor : sortedInterceptors) {
            RetryJobResult result = interceptor.beforeExecute(context);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @NotNull
    private List<RetryJobContextInterceptor> sortedInterceptors() {
        return interceptors.stream()
                .sorted(Comparator.comparing(RetryJobContextInterceptor::order))
                .collect(Collectors.toList());
    }

    @Override
    public void afterExecute(RetryJobContext context) {
        for (RetryJobContextInterceptor interceptor : interceptors) {
            interceptor.afterExecute(context);
        }
    }

    @Override
    public Order order() {
        return FIRST;
    }
}
