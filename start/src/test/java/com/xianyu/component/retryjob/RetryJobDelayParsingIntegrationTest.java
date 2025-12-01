package com.xianyu.component.retryjob;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.id.IdGenerator;
import com.xianyu.component.retryjob.annotation.RetryJob;
import com.xianyu.component.retryjob.enums.RetryJobStatusEnum;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import com.xianyu.component.retryjob.repository.mapper.RetryJobMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "redisson.host=localhost",
        "retry.intervalMinutes=1",
        "test.delay-times=2"
})
@Import(RetryJobDelayParsingIntegrationTest.Cfg.class)
class RetryJobDelayParsingIntegrationTest extends BaseIntegrationTest {

    @Resource
    private RetryJobMapper retryJobMapper;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private TestBean testBean;

    @Test
    @DisplayName("属性占位延迟解析")
    void should_parse_delay_from_property() {
        TestDto dto = new TestDto(System.currentTimeMillis(), LocalDateTime.now(), "p1");
        testBean.propertyDelay(dto);

        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_PROP_DELAY", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.PROCESSING);
        assertThat(testBean.propertyDelayExecuted).isEqualTo(0);
    }

    @Test
    @DisplayName("SpEL LocalDateTime 延迟解析")
    void should_parse_delay_from_spel_localdatetime() {
        LocalDateTime targetTime = LocalDateTime.now().plusSeconds(2);
        TestDto dto = new TestDto(System.currentTimeMillis(), targetTime, "p2");
        testBean.spelDelay(dto);

        Optional<RetryJobDo> jobOpt = retryJobMapper.get("TYPE_SPEL_DELAY", String.valueOf(dto.id));
        assertThat(jobOpt).isPresent();
        RetryJobDo job = jobOpt.get();
        assertThat(job.getStatus()).isEqualTo(RetryJobStatusEnum.PROCESSING);
        assertThat(job.getNextExecuteTime()).isEqualTo(targetTime);
        assertThat(testBean.spelDelayExecuted).isEqualTo(0);
    }

    @TestConfiguration
    static class Cfg {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    public static class TestBean {
        volatile int propertyDelayExecuted = 0;
        volatile int spelDelayExecuted = 0;

        @RetryJob(value = "TYPE_PROP_DELAY", delayTime = "${test.delay-times:10}", key = "#dto.id")
        public void propertyDelay(TestDto dto) {
            propertyDelayExecuted++;
        }

        @RetryJob(value = "TYPE_SPEL_DELAY", delayTimeUnit = java.time.temporal.ChronoUnit.SECONDS, delayTime = "#dto.times", key = "#dto.id")
        public void spelDelay(TestDto dto) {
            spelDelayExecuted++;
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

