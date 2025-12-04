package com.xianyu.order.context.order.app;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.order.context.order.app.dto.UpdateAddressDto;
import com.xianyu.order.context.order.app.dto.UpdateFullNameDto;
import com.xianyu.order.context.order.app.service.OrderAppService;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

class UpdateOrderAddressUseCaseIntegerationTest extends BaseIntegrationTest {

    @Resource
    private OrderAppService orderAppService;

    @Resource
    private OrderRepository orderRepository;

    @Test
    @DisplayName("测试订单地址修改")
    @Sql("/sql/UpdateOrderAddressUseCaseIntegerationTest/should_update_order_address.sql")
    void should_update_order_address() {
        long orderId = 1L;
        UpdateAddressDto updateAddressDto = new UpdateAddressDto(orderId, "email1", "phoneNumber1", "firstName1",
                "lastName1", "addressLine11", "addressLine21", "country1");
        orderAppService.updateAddress(updateAddressDto);

        Order order = orderRepository.getOrThrow(orderId);
        assertJSON(order.getOrderAddress());
    }

    @Test
    @DisplayName("测试姓名修改")
    @Sql("/sql/UpdateOrderAddressUseCaseIntegerationTest/should_update_full_name.sql")
    void should_update_full_name() {
        long orderId = 1L;

        UpdateFullNameDto cmd = new UpdateFullNameDto(orderId, "li", "");
        orderAppService.updateFullName(cmd);

        Order order = orderRepository.getOrThrow(orderId);
        assertJSON(order.getOrderAddress());
    }

}