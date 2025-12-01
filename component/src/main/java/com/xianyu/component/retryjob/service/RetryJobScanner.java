package com.xianyu.component.retryjob.service;

import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.context.handler.DefaultRetryJobHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class RetryJobScanner implements ApplicationContextAware, SmartInitializingSingleton {

    private final Map<String, DefaultRetryJobHandler> retryJobHandlerMap = new HashMap<>();

    private ApplicationContext applicationContext;

    @Nullable
    private static Map<Method, RetryJob> getMethod2RetryableMap(String beanDefinitionName, Object bean) {
        Map<Method, RetryJob> annotatedMethods = null;
        try {
            annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<RetryJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, RetryJob.class));
        } catch (Throwable ex) {
            log.error("retry-job 方法解析异常 bean[" + beanDefinitionName + "].", ex);
        }
        if (annotatedMethods == null || annotatedMethods.isEmpty()) {
            return null;
        }
        return annotatedMethods;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取方法参数
     */
    public Optional<DefaultRetryJobHandler> getJobHandler(String retryJobTypeEnum) {
        return Optional.ofNullable(retryJobHandlerMap.get(retryJobTypeEnum));
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Map<Method, RetryJob> method2RetryJobMap = getMethod2RetryableMap(beanDefinitionName, bean);
            if (method2RetryJobMap == null) {
                continue;
            }
            method2RetryJobMap.forEach((method, retryJob) -> {
                checkMethod(method, retryJob);
                retryJobHandlerMap.put(retryJob.value(), DefaultRetryJobHandler.builder()
                        .bean(bean)
                        .method(method)
                        .retryJobAnnotation(retryJob)
                        .build());
            });
        }
    }

    private void checkMethod(Method method, RetryJob retryJob) {
        String retryJobType = retryJob.value();
        String prefix = String.format("[retry-job][%s][%s]", method.getDeclaringClass().getName() + '#' + method.getName(), retryJobType);
        if (Objects.isNull(retryJobType)) {
            throw new IllegalArgumentException(String.format("%s重试任务类型不能为空", prefix));
        }
        if (retryJobHandlerMap.containsKey(retryJobType)) {
            throw new IllegalArgumentException(String.format("%s重试任务不能重复[%s]", prefix, retryJobType));
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 1) {
            throw new IllegalArgumentException(String.format("%s方法参数不能大于1", prefix));
        }
        Class<?> returnType = method.getReturnType();
        if (!Objects.equals(Void.TYPE, returnType)&&!RetryJobResult.class.isAssignableFrom(returnType)){
            throw new IllegalArgumentException(String.format("%s方法返回值必须为void 或者 RetryJobResult类", prefix));
        }
        if (retryJob.idempotence()) {
            Assert.isTrue(StringUtils.isNotEmpty(retryJob.key()), String.format("%s开启幂等时key不能为空", prefix));
        }
    }
}
