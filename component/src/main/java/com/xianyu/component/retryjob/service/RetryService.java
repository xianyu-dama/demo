package com.xianyu.component.retryjob.service;


import com.xianyu.component.retryjob.context.RetryJobContext;

public interface RetryService {

    void process(RetryJobContext retryJob) throws Throwable;
}
