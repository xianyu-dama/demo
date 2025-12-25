package com.xianyu.component.retryjob.context.aware;

import com.xianyu.component.retryjob.context.RetryJobContext;
import com.xianyu.component.retryjob.context.RetryJobResult;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetryJobContextInterceptor implements RetryJobContextInterceptor {

    @Nullable
    @Override
    public RetryJobResult beforeExecute(RetryJobContext context) {
        return null;
    }

    @Override
    public void afterExecute(RetryJobContext context) {

    }

    @Override
    public Order order() {
        return LAST;
    }
}
