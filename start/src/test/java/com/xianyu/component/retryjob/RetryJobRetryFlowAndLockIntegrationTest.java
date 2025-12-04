package com.xianyu.component.retryjob;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.id.IdGenerator;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import com.xianyu.component.retryjob.service.RetryJobScanner;
import com.xianyu.component.retryjob.task.RetryJobScheduleTask;
import com.xianyu.component.utils.json.JsonUtils;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "redisson.host=localhost",
        "retry.intervalMinutes=1"
})
@Import({RetryJobRetryFlowAndLockIntegrationTest.Cfg.class})
class RetryJobRetryFlowAndLockIntegrationTest extends BaseIntegrationTest {

    @Resource
    private RetryJobMapper retryJobMapper;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private RetryJobScheduleTask scheduleTask;

    @Resource
    private RetryJobScanner retryJobScanner;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private TestBean testBean;

    @BeforeEach
    void setup() {
        doReturn(1L).when(idGenerator).id();
    }

    @Test
    @DisplayName("重试流程：次数自增并成功终止")
    void should_increment_retry_times_and_finish_success_on_retry() {
        String type = "TYPE_RETRY_FLOW";
        TestDto dto = new TestDto(1L, LocalDateTime.now(), "p1");
        RetryJobDo item = RetryJobDo.builder()
                .key(String.valueOf(dto.id))
                .type(type)
                .content(JsonUtils.toJSONString(dto))
                .status(RetryJobStatusEnum.PROCESSING)
                .currentRetryTimes(0)
                .maxRetryTimes(8)
                .nextExecuteTime(LocalDateTime.now())
                .build();
        retryJobMapper.insert(item);

        List<RetryJobDo> list = retryJobMapper.listByIds(List.of(item.getId()));
        assertThat(list).hasSize(1);

        scheduleTask.run(List.of(item.getId()));

        Optional<RetryJobDo> jobOpt = retryJobMapper.get(type, String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.SUCCESS);
        assertThat(testBean.getRetryFlowExecuted()).isEqualTo(1);
    }

    @TestConfiguration
    static class Cfg {
        @org.springframework.context.annotation.Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    @Getter
    public static class TestBean {
        volatile int retryFlowExecuted = 0;

        @RetryJob(value = "TYPE_RETRY_FLOW", async = false, delayTime = "0", key = "#dto.id")
        public RetryJobResult retryFlow(TestDto dto) {
            retryFlowExecuted++;
            return RetryJobResult.success();
        }
    }

    public static class TestDto implements Serializable {
        public long id;
        public LocalDateTime times;
        public String payload;

        @JsonCreator
        public TestDto(long id, LocalDateTime times, String payload) {
            this.id = id;
            this.times = times;
            this.payload = payload;
        }
    }
}
