package com.xianyu.component.retryjob.context.handler;

import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.utils.json.JsonUtils;
import java.lang.reflect.Method;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DefaultRetryJobHandler extends BaseRetryJobHandler {
    /**
     * 方法
     */
    private Method method;
    /**
     * bean
     */
    private Object bean;

    /**
     * 注解
     */
    private RetryJob retryJobAnnotation;
    /**
     * 原始返回结果
     */
    private RetryJobResult result;


    @Override
    protected Object doInvoke(RetryJobDo retryJobDo) throws Throwable {
        String content = retryJobDo.getContent();
        Class<?>[] parameterTypes = method.getParameterTypes();
        assert parameterTypes.length == 1;
        Object o = JsonUtils.json2JavaBean(content, parameterTypes[0]);
        return method.invoke(bean, o);
    }

}
