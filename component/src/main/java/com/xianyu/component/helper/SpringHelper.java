package com.xianyu.component.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class SpringHelper implements ApplicationContextAware, EnvironmentAware {

    private static ApplicationContext applicationContext;
    private static StandardEnvironment enviroment;

    public static StandardEnvironment getEnvironment() {
        return enviroment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        enviroment = (StandardEnvironment) environment;
    }

    public static Object getBean(String name) {
        assertApplicationNotNull(applicationContext);
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        assertApplicationNotNull(applicationContext);
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        assertApplicationNotNull(applicationContext);
        return applicationContext.getBean(name, clazz);
    }

    private static void assertApplicationNotNull(ApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "容器还没初始化");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        assertApplicationNotNull(applicationContext);
        SpringHelper.applicationContext = applicationContext;
    }

}
