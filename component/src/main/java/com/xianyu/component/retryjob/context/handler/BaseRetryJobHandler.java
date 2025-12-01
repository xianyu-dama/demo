package com.xianyu.component.retryjob.context.handler;


import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;

public abstract class BaseRetryJobHandler implements RetryJobHandler{

    @Override
    public final RetryJobResult invoke(RetryJobDo retryJobDo) {
        try {
            Object result = doInvoke(retryJobDo);
            if (result instanceof RetryJobResult){
                return ((RetryJobResult) result);
            }
            return RetryJobResult.success();
        }catch (Throwable e){
            return RetryJobResult.fail(e);
        }
    }

    protected abstract Object doInvoke(RetryJobDo retryJobDo) throws Throwable;

}
