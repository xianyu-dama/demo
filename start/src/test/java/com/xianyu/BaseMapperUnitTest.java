package com.xianyu;

import com.xianyu.base.BaseTest;
import com.xianyu.component.mybatis.MybatisPlusConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-addition.properties")
@SpringJUnitConfig(classes = {MybatisPlusConfig.class})
public abstract class BaseMapperUnitTest extends BaseTest {

}