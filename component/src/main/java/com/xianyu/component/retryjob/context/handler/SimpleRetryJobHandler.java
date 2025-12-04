package com.xianyu.component.retryjob.context.handler;


import com.xianyu.component.retryjob.repository.entity.RetryJobDo;

public interface SimpleRetryJobHandler {

    Object invoke(RetryJobDo retryJobDo) throws Throwable;

}
