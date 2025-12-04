package com.xianyu.component.retryjob.context.handler;


import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;

public interface RetryJobHandler {

    RetryJobResult invoke(RetryJobDo retryJobDo) throws Throwable;

}
