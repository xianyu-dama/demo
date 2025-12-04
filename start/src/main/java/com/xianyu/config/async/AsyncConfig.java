package com.xianyu.config.async;

import com.alibaba.ttl.threadpool.TtlExecutors;
import java.util.concurrent.Executor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * <br/>
 * Created on : 2024-05-18 08:26
 *
 * @author xian_yu_da_ma
 */
// 开启异步
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer, BeanFactoryPostProcessor {

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        // -javaagent:/Users/xian_yu_da_ma/Desktop/transmittable-thread-local-2.14.5.jar 也行
        return TtlExecutors.getTtlExecutor(executor);
    }

    /**
     * @Async 这个注解的切面不是通过代理生成的
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME);
        beanDefinition.getPropertyValues().add("exposeProxy", true);
    }

}
