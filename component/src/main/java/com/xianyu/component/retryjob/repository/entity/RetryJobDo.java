package com.xianyu.component.retryjob.repository.entity;

import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.Assert;

/**
 * RetryJob
 * @author 
 * @date 2023/02/24
 */
@Builder(toBuilder = true)
@Getter
@FieldNameConstants
public class RetryJobDo implements Serializable {

    /**
     * 唯一键
     */
    private Long id;

    /**
     * 相同业务类型的业务唯一ID
     */
    @NonNull
    private String key;

    /**
     * 重试任务类型
     */
    @NonNull
    private String type;

    /**
     * 参数
     */
    @NonNull
    private String content;

    /**
     * 任务状态
     */
    @NonNull
    private RetryJobStatusEnum status;

    /**
     * 当前重试次数
     */
    @NonNull
    private Integer currentRetryTimes;

    /**
     * 最大重试次数
     */
    @NonNull
    private Integer maxRetryTimes;

    /**
     * 下次执行时间
     */
    private LocalDateTime nextExecuteTime;

    /**
     * 上次失败原因
     */
    private String lastErrorMsg;

    /**
     * 是否到了执行时间
     */
    public boolean hasReachedExecuteTime() {
        return !getNextExecuteTime().isAfter(LocalDateTime.now());
    }

    public void addTimes() {
        currentRetryTimes += 1;
    }

    public boolean reachMaxRetryTimes() {
        return currentRetryTimes >= maxRetryTimes;
    }

    public void success() {
        status = RetryJobStatusEnum.SUCCESS;
        nextExecuteTime = null;
    }

    public void failedWaitRetry(int intervalMinutes, String msg) {
        status = RetryJobStatusEnum.PROCESSING;
        lastErrorMsg = msg;
        nextExecuteTime = LocalDateTime.now().plusMinutes(intervalMinutes);
    }

    public void failedFinish(String reason) {
        status = RetryJobStatusEnum.FAILED;
        lastErrorMsg = reason;
    }

    /**
     * 延迟执行
     */
    public RetryJobDo delay(LocalDateTime nextExecuteTime) {
        Assert.notNull(nextExecuteTime, "nextExecuteTime must not be null");
        return toBuilder().nextExecuteTime(nextExecuteTime).build();
    }

    public boolean isFinish(){
        return status.isFinish();
    }
}
