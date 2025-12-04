package com.xianyu.component.retryjob.task;

import com.xianyu.component.retryjob.context.RetryJobContext;
import com.xianyu.component.retryjob.context.handler.DefaultRetryJobHandler;
import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import com.xianyu.component.retryjob.repository.dto.RetryJobQuery;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import com.xianyu.component.retryjob.service.RetryJobScanner;
import com.xianyu.component.retryjob.service.RetryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetryJobScheduleTask {

    private final RetryService retryService;

    private final RetryJobScanner retryJobScanner;

    private final RetryJobMapper retryJobMapper;

    //@XxlJob("retryJobScheduleTask")
    public void retryJobScheduleTask() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[retry-job]开始执行重试任务调度，执行时间点:{}", now);
        // 获取状态执行中，待执行时间小于当前时间,每次获取100条
        List<RetryJobDo> retryJobDos = retryJobMapper.list(RetryJobQuery.builder()
                .count(100L)
                .statusList(List.of(RetryJobStatusEnum.PROCESSING))
                // 只查1分钟之前的数据,临时解决一下定时任务查询与切面并发执行的问题
                .nextExecuteTimeEnd(now.minusMinutes(1))
                .build());
        if (CollectionUtils.isEmpty(retryJobDos)) {
            log.info("[retry-job]本次无待执行任务，结束");
            return;
        }
        for (RetryJobDo retryJobDo : retryJobDos) {
            startRetryJob(retryJobDo);
        }
        log.info("[retry-job]重试任务调度，执行完成");
    }


    public void run(List<Long> ids) {
        List<RetryJobDo> retryJobDos = retryJobMapper.listByIds(ids);
        retryJobDos.forEach(this::startRetryJob);
    }


    private void startRetryJob(RetryJobDo retryJobDo) {

        var jobName = retryJobDo.getType();
        Optional<DefaultRetryJobHandler> jobHandlerOpt = retryJobScanner.getJobHandler(jobName);
        if (jobHandlerOpt.isEmpty()) {
            log.error("[retry-job]任务[{}]获取重试任务执行器失败", jobName);
            retryJobDo.failedFinish(String.format("获取重试任务执行器失败:%s", jobName));
            retryJobMapper.update(retryJobDo);
            return;
        }
        DefaultRetryJobHandler handler = jobHandlerOpt.get();
        RetryJobContext context = RetryJobContext.ofRetry(retryJobDo, handler);
        RetryJobContext.start(context);
        try {
            retryService.process(context);
        } catch (Throwable e) {
            log.error("[retry-job][{}]执行异常;bizId:{}", retryJobDo.getType(), retryJobDo.getKey(), e);
        } finally {
            RetryJobContext.end();
        }
    }
}
