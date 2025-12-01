package com.xianyu.component.retryjob.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RetryConfig {

    /**
     * 当前任务失败后下次重试的间隔时间
     */
    @Value("${retry.intervalMinutes:1}")
    private int intervalMinutes;


}
