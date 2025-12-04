package com.xianyu.component.retryjob.repository.dto;

import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * RetryJobQuery
 * @author 
 * @date 2023/02/24
 */
@Builder
@Getter
public class RetryJobQuery {

    /**
     * 数量
     */
    private Long count;

    /**
     * 上一次的ID
     */
    private Long lastId;

    /**
     * 状态列表
     */
    private List<RetryJobStatusEnum> statusList;

    /**
     * 类型
     */
    private String retryJobType;

    /**
     * 最小下次执行时间
     */
    private LocalDateTime nextExecuteTimeStart;

    /**
     * 最大下次执行时间
     */
    private LocalDateTime nextExecuteTimeEnd;
}
