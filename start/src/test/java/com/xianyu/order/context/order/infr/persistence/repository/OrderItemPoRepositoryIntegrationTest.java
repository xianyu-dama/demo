package com.xianyu.order.context.order.infr.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xianyu.BaseIntegrationTest;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPo;
import jakarta.annotation.Resource;
import java.util.List;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

class OrderItemPoRepositoryIntegrationTest extends BaseIntegrationTest {

    @Resource
    private OrderItemPoRepository orderItemPoRepository;

    @Test
    @DisplayName("listByOrderId")
    @Sql("/sql/OrderDetailPoRepositoryIntegrationTest/should_list_by_order_id.sql")
    void should_list_by_order_id() {
        ObjectMapper mapper = getObjectMapper().registerModule(new ImmutableModule());
        List<OrderItemPo> orderItemPos = orderItemPoRepository.listById(1L);
        assertJSON(orderItemPos, mapper);
    }
}
