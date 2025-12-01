package com.xianyu.component.retryjob.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryJobKey {

    /**
     * 相同业务类型的业务唯一ID
     */
    private String key;

    /**
     * 重试任务类型
     */
    private String type;

    public static RetryJobKey of(RetryJobDo retryJob) {
        return builder()
                .key(retryJob.getKey())
                .type(retryJob.getType())
                .build();
    }
}
