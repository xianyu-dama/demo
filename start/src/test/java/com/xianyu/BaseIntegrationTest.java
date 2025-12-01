package com.xianyu;

import com.xianyu.base.BaseTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBeans;
import org.springframework.transaction.annotation.Transactional;

/**
 * 集成测试，默认不启动容器<br/>
 * Created on : 2023-08-29 14:01
 * @author xian_yu_da_ma
 */
@BaseIntegrationTest.SharedMocks
@Transactional
@TestPropertySource(locations = "classpath:application-addition.properties")
@SpringBootTest(classes = {Application.class})
public abstract class BaseIntegrationTest extends BaseTest {

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void _beforeEach() {
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    /**
     * 集成测试的mockbean定义
     */
    /*@MockitoBeans({
        @MockitoBean(types = {})
    })*/
    @MockitoSpyBeans({
            @MockitoSpyBean(types = {com.xianyu.component.id.IdGenerator.class})
    })
    /**
     * mock bean 和 spy bean 声明
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SharedMocks {


    }

    @TestConfiguration
    public class BaseIntegrationTestConfig {


    }

}
