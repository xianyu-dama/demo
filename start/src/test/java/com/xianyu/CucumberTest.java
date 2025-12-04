package com.xianyu;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import io.cucumber.spring.ScenarioScope;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;
import org.wiremock.spring.EnableWireMock;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

///////////////////////////////////////////////////////////////////////////
// 用于集成测试或者api测试
///////////////////////////////////////////////////////////////////////////

/**
 * 测试集成
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.xianyu")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/Cucumber.html")

/**
 * spring 相关
 */
@Slf4j
@EnableWireMock
@CucumberContextConfiguration
@TestPropertySource(locations = "classpath:application-addition.properties")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = CucumberTest.WireMockInitializer.class)
//无效，只能手动管理事务
//@Transactional
public class CucumberTest {

    /**
     * public才能识别
     */
    @BeforeAll
    public static void _beforeAll() {
        log.info("Starting CucumberTest");
    }

    @AfterAll
    public static void _afterAll() {
        log.info("Ending CucumberTest");
    }

    @Before
    public void _before() {
        log.info("Starting CucumberTest Scenario");
    }

    @After
    public void _after(Scenario scenario) {
    }

    @TestConfiguration
    public static class CucumberContextConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

    }

    @Slf4j
    public static class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            String baseUrl = applicationContext.getEnvironment().getProperty("wiremock.server.baseUrl");
            String port = applicationContext.getEnvironment().getProperty("wiremock.server.port");
            log.info("WireMock url {} 端口 {}", baseUrl, port);
            WireMock.configureFor(Integer.valueOf(port));

        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 事务管理
    ///////////////////////////////////////////////////////////////////////////

    @Component
    @ScenarioScope
    @Data
    private static class TransactionScenarioContext {

        private TransactionStatus status;

    }

    @Slf4j
    public static class TransactionalHooks {

            @Resource
            private PlatformTransactionManager transactionManager;

            @Resource
            private TransactionScenarioContext context;

            @Before(order = 0)
            public void begin() {
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                context.setStatus(transactionManager.getTransaction(def));
            }

            @After(order = 1000)
            public void rollback(Scenario scenario) {
                var status = context.getStatus();
                if (status != null && !status.isCompleted()) {
                    transactionManager.rollback(status);
                    context.setStatus(null);
                }
            }
        }
}