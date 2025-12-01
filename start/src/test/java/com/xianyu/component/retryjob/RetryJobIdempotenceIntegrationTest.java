package com.xianyu.component.retryjob;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.helper.TransactionHelper;
import com.xianyu.component.id.IdGenerator;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "redisson.host=localhost",
        "retry.intervalMinutes=1"
})
@Import(RetryJobIdempotenceIntegrationTest.Cfg.class)
class RetryJobIdempotenceIntegrationTest extends BaseIntegrationTest {

    @Resource
    private RetryJobMapper retryJobMapper;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private TestBean testBean;

    @Resource
    private TransactionHelper transactionHelper;

    @Test
    @DisplayName("幂等=true：第二次调用不执行真实方法")
    void should_ignore_second_call_when_idempotence_true_duplicate_key() {
        TestDto dto = new TestDto(System.currentTimeMillis(), LocalDateTime.now(), "p1");
        testBean.idempotenceTrue(dto);
        testBean.idempotenceTrue(dto);
        assertThat(testBean.getIdempotenceTrueExecuted()).isEqualTo(1);
    }

    @Test
    @DisplayName("幂等=false：第二次调用删除旧记录并重新插入执行")
    void should_delete_existing_then_insert_when_idempotence_false() {
        TestDto dto = new TestDto(System.currentTimeMillis(), LocalDateTime.now(), "p2");
        testBean.idempotenceFalse(dto);
        testBean.idempotenceFalse(dto);

        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_IDEMP_FALSE", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        assertThat(testBean.getIdempotenceFalseExecuted()).isEqualTo(2);
    }

    @TestConfiguration
    static class Cfg {
        @org.springframework.context.annotation.Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    @Getter
    static class TestBean {
        private volatile int idempotenceTrueExecuted = 0;
        private volatile int idempotenceFalseExecuted = 0;

        @RetryJob(value = "TYPE_IDEMP_TRUE", async = false, delayTime = "0", idempotence = true, key = "#dto.id")
        public void idempotenceTrue(TestDto dto) {
            idempotenceTrueExecuted++;
        }

        @RetryJob(value = "TYPE_IDEMP_FALSE", async = false, delayTime = "0", idempotence = false, key = "#dto.id")
        public void idempotenceFalse(TestDto dto) {
            idempotenceFalseExecuted++;
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

