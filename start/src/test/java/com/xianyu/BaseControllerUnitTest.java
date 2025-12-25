package com.xianyu;

import com.xianyu.base.BaseTest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * <br/>
 * Created on : 2023-09-27 20:36
 * @author xian_yu_da_ma
 */
@SpringJUnitWebConfig
public abstract class BaseControllerUnitTest extends BaseTest {

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }


    protected void assertResponse(ResultActions perform) throws UnsupportedEncodingException {
        MvcResult mvcResult = perform.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString(Charset.defaultCharset());
        assertJSON(contentAsString);
    }
}
