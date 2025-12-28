package com.xianyu.order.context.order.infr.adapter;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.ddd.event.persistence.DomainEventPo;
import com.xianyu.component.ddd.event.persistence.service.DomainEventPoService;
import com.xianyu.component.helper.SpringHelper;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.infr.convertor.OrderConvertor;
import com.xianyu.order.context.order.infr.persistence.repository.OrderPoRepository;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.JSqlClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static java.time.Instant.ofEpochMilli;

class OrderPoRepositoryAdapterIntegrationTest extends BaseIntegrationTest {

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private DomainEventPoService domainEventPoService;

    @Resource
    private SpringHelper springHelper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private JSqlClient jSqlClient;

    @Resource
    private OrderConvertor orderConvertor;

    @Test
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_order_get.sql")
    @DisplayName("测试聚合根查询")
    void should_order_get() {
        long orderId = 1L;
        Order order = orderRepository.get(orderId).orElseThrow();
        assertJSON(order);
    }

    @Test
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_update_order_info.sql")
    @DisplayName("测试更新聚合根")
    void should_update_order_info() {
        long orderId = 1L;
        Order order = orderRepository.getWithLockOrThrow(orderId);
        order.pay(new BigDecimal("8888"), new BigDecimal("9999"));

        int updateCount = orderRepository.update(order);
        // 订单和订单item都全量更新
        assertThat(updateCount).isEqualTo(3);

        Order newOrder = orderRepository.get(orderId).orElseThrow();
        assertJSON(newOrder);
    }

    @Test
    @DisplayName("测试取消订单")
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_cancel_order.sql")
    void should_cancel_order() {
        long orderId = 1L;
        Order order = orderRepository.getWithLockOrThrow(orderId);
        Clock constantClock = Clock.fixed(ofEpochMilli(0), ZoneId.systemDefault());

        // DomainEvent设置了默认值，这里设置固定返回
        UUID fixedUUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
        try (
                MockedStatic<Instant> instant = mockStatic(Instant.class);
                MockedStatic<UUID> idUtil = mockStatic(UUID.class)
        ) {
            instant.when(Instant::now).thenReturn(constantClock.instant());
            idUtil.when(UUID::randomUUID).thenReturn(fixedUUID);
            order.cancel();
        }

        // when
        int updateCount = orderRepository.update(order);
        assertThat(updateCount).isEqualTo(3);

        // then
        Order newOrder = orderRepository.get(orderId).orElseThrow();
        List<DomainEventPo> events = domainEventPoService.list();
        assertThat(events).hasSize(1);

        var assertMap = new HashMap<>();
        assertMap.put("events", events.get(0).content());
        assertMap.put("order", newOrder);
        assertJSON(assertMap);
    }

    @Test
    @DisplayName("测试缓存清除")
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_clear_cache_when_get_in_cache.sql")
    void should_clear_cache_when_get_in_cache() {
        long orderId = 1L;
        Order order = orderRepository.getInCache(orderId).orElseThrow();
        assertThat(order).isNotNull();

        order = orderRepository.getWithLockOrThrow(orderId);
        order.cancel();
        orderRepository.update(order);

        Order cacheOrder = orderRepository.getInCache(orderId).orElseThrow();
        assertThat(cacheOrder.isCancel()).isTrue();
        assertJSON(cacheOrder);
    }

    @Test
    @DisplayName("测试数据不存在的情况")
    void should_return_empty_when_order_not_found() {
        assertThat(orderRepository.getInCache(1L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("测试空聚合根")
    void should_throw_exception_when_input_null() {
        assertThrows(NullPointerException.class, () -> orderRepository.add(null));
        assertThrows(NullPointerException.class, () -> orderRepository.update(null));
    }

    @Test
    @DisplayName("测试聚合加锁")
    @Sql("/sql/OrderRepositoryAdapterIntegrationTest/should_get_order_with_lock.sql")
    void should_get_order_with_lock() {
        // 使用悲观锁获取订单
        long orderId = 100L;

        // 基本验证：确保能正确获取订单
        Order order = orderRepository.getWithLockOrThrow(orderId);
        assertThat(order).isNotNull();

        // 验证订单基本信息
        assertThat(order.getOrderId()).isEqualTo(orderId);
        assertThat(order.getUserId()).isEqualTo(1L);
        assertThat(order.getShouldPay()).isEqualTo(new BigDecimal("199.99"));

        // 验证订单详情
        assertThat(order.getOrderItems()).hasSize(1);
    }

    @Test
    @DisplayName("根据id查询订单")
    @Sql("/sql/OrderRepositoryAdapterIntegrationTest/should_find_by_id.sql")
    void should_find_by_id() {
        var order = jSqlClient.findById(OrderPoRepository.FETCHER, 100L);
        Order aggregate = orderConvertor.toOrder(order);
        assertJSON(aggregate);
    }

}
