package com.xianyu.component.retryjob.context;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * RetryJobResult
 * @author 
 * @date 2023/08/19
 */
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class RetryJobResult {

    /**
     * 下次执行时间
     */
    private LocalDateTime nextExecuteTime;

    /**
     * 是否执行成功
     */
    private RetryJobResultType resultType;
    /**
     * 异常
     */
    private Throwable throwable;


    public static RetryJobResult success(){
        return builder()
                .resultType(RetryJobResultType.SUCCESS)
                .build();
    }


    public static RetryJobResult fail(@NonNull Throwable e){
        return builder()
                .resultType(RetryJobResultType.FAIL)
                .throwable(e)
                .build();
    }

    public static RetryJobResult delay(@NonNull LocalDateTime nextExecuteTime){
        return builder()
                .resultType(RetryJobResultType.DELAY)
                .nextExecuteTime(nextExecuteTime)
                .build();
    }
}
