package com.xianyu.component.retryjob.context.handler;


import com.xianyu.component.retryjob.repository.entity.RetryJobDo;

public class RetryJobHandlers {

    public static RetryJobHandler wrap(SimpleRetryJobHandler handler){
        return new BaseRetryJobHandler() {
            @Override
            protected Object doInvoke(RetryJobDo retryJobDo) throws Throwable {
                return handler.invoke(retryJobDo);
            }
        };
    }


    public static RetryJobHandler wrap(RetryJobHandler handler){
        return new BaseRetryJobHandler() {
            @Override
            protected Object doInvoke(RetryJobDo retryJobDo) throws Throwable {
                return handler.invoke(retryJobDo);
            }
        };
    }
}
