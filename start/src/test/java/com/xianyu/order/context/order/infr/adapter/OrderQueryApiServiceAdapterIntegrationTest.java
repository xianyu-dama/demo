package com.xianyu.order.context.order.infr.adapter;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.order.context.order.app.service.OrderQueryAppService;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

class OrderQueryApiServiceAdapterIntegrationTest extends BaseIntegrationTest {

    @Resource
    private OrderQueryAppService orderQueryAppService;

    @Test
    @DisplayName("测试读模型")
    @Sql("/sql/OrderQueryServiceAdapterIntegrationTest/should_get_readonly_order.sql")
    void should_get_readonly_order() {
        Optional<OrderReadOnlyDto> readonly = orderQueryAppService.getReadonly(1L);
        assertThat(readonly).isPresent();
        OrderReadOnlyDto order = readonly.get();
        assertJSON(order);
    }

    @Test
    @DisplayName("根据userId查询订单")
    @Sql("/sql/OrderQueryServiceAdapterIntegrationTest/should_query_order_by_user_id.sql")
    void should_query_order_by_user_id() {
        // 查询用户1的订单，第一页，每页2条
        var pageDto = orderQueryAppService.listBy(1L, 1, 2);

        // 验证分页信息
        assertThat(pageDto.getTotalElements()).isEqualTo(3); // 用户1有3条订单
        assertThat(pageDto.toList()).hasSize(2); // 第一页有2条数据

        // 验证订单按ID降序排序
        List<OrderReadOnlyDto> orders = pageDto.toList();
        assertThat(orders.get(0).orderId()).isEqualTo(1L);
        assertThat(orders.get(1).orderId()).isEqualTo(2L);

        // 查询第二页
        var page2 = orderQueryAppService.listBy(1L, 2, 2);
        assertThat(page2.getTotalElements()).isEqualTo(3);
        assertThat(page2.toList()).hasSize(1); // 第二页只有1条数据
        assertThat(page2.toList()
                .get(0).orderId()).isEqualTo(3L);

        // 查询用户2的订单
        var user2Orders = orderQueryAppService.listBy(2L, 1, 10);
        assertThat(user2Orders.getTotalElements()).isEqualTo(1);
        assertThat(user2Orders.toList()).hasSize(1);
        assertThat(user2Orders.toList()
                .get(0).orderId()).isEqualTo(4L);

        // 查询不存在的用户
        var emptyOrders = orderQueryAppService.listBy(999L, 1, 10);
        assertThat(emptyOrders.getTotalElements()).isEqualTo(0);
        assertThat(emptyOrders.toList()).isEmpty();
    }

    @Test
    @DisplayName("select count()")
    void should_select_count() {
        assertThat(orderQueryAppService.count()).isEqualTo(0L);
    }

}