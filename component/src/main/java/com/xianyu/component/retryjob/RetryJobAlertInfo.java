package com.xianyu.component.retryjob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
@Getter
public class RetryJobAlertInfo {

    private String title;

    private String message;

    /**
     * 重试任务id
     */
    private Long retryJobId;

    /**
     * 相同业务类型的业务唯一ID
     */
    private String taskLockId;

    /**
     * 重试任务类型
     */
    private String type;

}
