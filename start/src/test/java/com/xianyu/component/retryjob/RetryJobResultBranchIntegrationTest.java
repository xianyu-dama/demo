package com.xianyu.component.retryjob;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.exception.BizException;
import com.xianyu.component.id.IdGenerator;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.context.RetryJobResult;
import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "redisson.host=localhost",
        "retry.intervalMinutes=1"
})
@Import(RetryJobResultBranchIntegrationTest.Cfg.class)
class RetryJobResultBranchIntegrationTest extends BaseIntegrationTest {

    @Resource
    private RetryJobMapper retryJobMapper;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private TestBean testBean;

    @Test
    @DisplayName("成功分支：状态成功")
    void should_finish_success_on_success_result() {
        TestDto dto = new TestDto(1L, LocalDateTime.now(), "p1");
        testBean.success(dto);
        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_RESULT_SUCCESS", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        assertThat(jobOpt.get().getStatus()).isEqualTo(RetryJobStatusEnum.SUCCESS);
    }

    @Test
    @DisplayName("失败待重试：RuntimeException 可重试")
    void should_wait_retry_on_retryable_exception() {
        TestDto dto = new TestDto(1L, LocalDateTime.now(), "p2");
        testBean.failRetryable(dto);
        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_RESULT_FAIL_RETRY", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.PROCESSING);
        assertThat(job.getNextExecuteTime()).isAfter(LocalDateTime.now());
        assertThat(job.getLastErrorMsg()).isNotBlank();
    }

    @Test
    @DisplayName("失败终止：BizException 不重试")
    void should_finish_failed_on_nonretryable_exception() {
        TestDto dto = new TestDto(1L, LocalDateTime.now(), "p3");
        assertThrows(BizException.class, () -> testBean.failNonRetryable(dto));
        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_RESULT_FAIL_STOP", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        assertThat(jobOpt.get().getStatus()).isEqualTo(RetryJobStatusEnum.FAILED);
    }

    @Test
    @DisplayName("延迟分支：更新下次执行时间")
    void should_update_delay_on_delay_result() {
        LocalDateTime next = LocalDateTime.now().plusSeconds(2);
        TestDto dto = new TestDto(1L, next, "p4");
        testBean.delay(dto);
        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_RESULT_DELAY", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.PROCESSING);
        assertThat(job.getNextExecuteTime()).isEqualTo(next);
    }

    @TestConfiguration
    static class Cfg {
        @org.springframework.context.annotation.Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    static class TestBean {
        @RetryJob(value = "TYPE_RESULT_SUCCESS", async = false, delayTime = "0", key = "#dto.id")
        public RetryJobResult success(TestDto dto) {
            return RetryJobResult.success();
        }

        @RetryJob(value = "TYPE_RESULT_FAIL_RETRY", async = false, delayTime = "0", key = "#dto.id")
        public RetryJobResult failRetryable(TestDto dto) {
            return RetryJobResult.fail(new RuntimeException("retry"));
        }

        @RetryJob(value = "TYPE_RESULT_FAIL_STOP", async = false, delayTime = "0", key = "#dto.id")
        public RetryJobResult failNonRetryable(TestDto dto) {
            return RetryJobResult.fail(new BizException("no-retry"));
        }

        @RetryJob(value = "TYPE_RESULT_DELAY", async = false, delayTime = "0", key = "#dto.id")
        public RetryJobResult delay(TestDto dto) {
            return RetryJobResult.delay(dto.times);
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

