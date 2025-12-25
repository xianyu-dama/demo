package com.xianyu.order.context.order.adapter.web;

import com.xianyu.BaseControllerUnitTest;
import com.xianyu.order.context.order.app.service.OrderAppService;
import com.xianyu.order.context.order.app.service.OrderQueryAppService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderController1UnitTest extends BaseControllerUnitTest {

    @MockitoBean
    private OrderAppService orderAppService;

    @MockitoBean
    private OrderQueryAppService orderQueryAppService;

    @Test
    @DisplayName("测试api")
    void should_() throws Exception {
        mockMvc.perform(get("/order/1")).andExpect(status().isOk());
    }

}