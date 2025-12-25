package com.xianyu.component.retryjob;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.id.IdGenerator;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(properties = {
        "redisson.host=localhost",
        "retry.intervalMinutes=1",
        "test.delay-times=2"
})
@Import(RetryJobFirstExecutionIntegrationTest.Cfg.class)
class RetryJobFirstExecutionIntegrationTest extends BaseIntegrationTest {

    @Resource
    private RetryJobMapper retryJobMapper;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private TestBean testBean;

    @Test
    @DisplayName("同步立即执行成功")
    void should_execute_sync_immediately_success() {
        TestDto dto = new TestDto(1, LocalDateTime.now(), "p1");
        testBean.syncImmediateSuccess(dto);

        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_SYNC_SUCCESS", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.SUCCESS);
        assertThat(testBean.getSyncImmediateSuccessExecuted()).isEqualTo(1);
    }

    @Test
    @DisplayName("异步立即执行成功")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void should_execute_async_immediately_success() throws InterruptedException {
        TestDto dto = new TestDto(System.currentTimeMillis(), LocalDateTime.now(), "p2");
        testBean.asyncImmediateSuccess(dto);

        Thread.sleep(1500);

        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_ASYNC_SUCCESS", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.SUCCESS);
        assertThat(testBean.getAsyncImmediateSuccessExecuted()).isEqualTo(1);
    }

    @Test
    @DisplayName("非立即执行：仅保存，延迟1秒")
    void should_save_only_with_positive_delay() {
        TestDto dto = new TestDto(System.currentTimeMillis(), LocalDateTime.now(), "p3");
        testBean.numericDelay(dto);

        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_NUMERIC_DELAY", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.PROCESSING);
        assertThat(job.getNextExecuteTime()).isAfter(LocalDateTime.now());
        assertThat(testBean.getNumericDelayExecuted()).isEqualTo(0);
    }

    @TestConfiguration
    static class Cfg {

        @Bean
        public TestBean testBean() {
            return new TestBean();
        }

    }

    @Getter
    static class TestBean {
        volatile int syncImmediateSuccessExecuted = 0;
        volatile int asyncImmediateSuccessExecuted = 0;
        volatile int numericDelayExecuted = 0;

        @RetryJob(value = "TYPE_SYNC_SUCCESS", async = false, delayTime = "0", key = "1")
        public RetryJobResult syncImmediateSuccess(TestDto dto) {
            syncImmediateSuccessExecuted++;
            return RetryJobResult.success();
        }

        @RetryJob(value = "TYPE_ASYNC_SUCCESS", async = true, delayTime = "0", key = "#dto.id")
        public RetryJobResult asyncImmediateSuccess(TestDto dto) {
            asyncImmediateSuccessExecuted++;
            return RetryJobResult.success();
        }

        @RetryJob(value = "TYPE_NUMERIC_DELAY", async = false, delayTime = "1", key = "#dto.id")
        public RetryJobResult numericDelay(TestDto dto) {
            numericDelayExecuted++;
            return RetryJobResult.success();
        }
    }

    static class TestDto {
        public long id;
        public LocalDateTime times;
        public String payload;

        public TestDto(long id, LocalDateTime times, String payload) {
            this.id = id;
            this.times = times;
            this.payload = payload;
        }
    }
}

