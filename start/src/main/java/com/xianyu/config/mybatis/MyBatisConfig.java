package com.xianyu.config.mybatis;

import com.github.pagehelper.PageInterceptor;
import java.util.Properties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.xianyu"}, annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class MyBatisConfig {

    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties props = new Properties();
        props.setProperty("banner", "false");
        interceptor.setProperties(props);
        return interceptor;
    }

}