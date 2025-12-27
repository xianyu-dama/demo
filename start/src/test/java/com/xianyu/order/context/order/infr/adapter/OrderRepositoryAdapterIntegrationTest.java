package com.xianyu.order.context.order.infr.adapter;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.ddd.event.persistence.DomainEventPo;
import com.xianyu.component.ddd.event.persistence.service.DomainEventPoService;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import jakarta.annotation.Resource;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static java.time.Instant.ofEpochMilli;

class OrderRepositoryAdapterIntegrationTest extends BaseIntegrationTest {

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private DomainEventPoService domainEventPoService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_order_get.sql")
    @DisplayName("测试聚合根查询")
    void should_order_get() {
        long orderId = 1L;
        Order order = orderRepository.getWithLockOrThrow(orderId);
        assertThat(order.snapshot()).isNotNull();

        Map<String, Order> excepted = new HashMap<>();
        excepted.put("current", order);
        excepted.put("snapshot", order.snapshot());

        assertJSON(excepted);
    }

    @Test
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_update_order_info.sql")
    @DisplayName("测试聚合根查询")
    void should_update_order_info() {
        long orderId = 1L;

        Order order = orderRepository.getWithLockOrThrow(orderId);
        order.cancel();
        orderRepository.update(order);

        Order newOrder = orderRepository.get(orderId).orElseThrow();
        assertJSON(newOrder);
    }

    // 必须开启事务
    @Transactional
    public void cancel(long orderId) {
        // 查询加锁（锁主单即可）select * from order where id = ? for update
        // 加载聚合根，并设置快照，用于更新时对比
        Order order = orderRepository.getWithLockOrThrow(orderId);
        order.cancel();
        // 所有PO对象都通过diff对比，只更新变化的记录
        orderRepository.update(order);
    }

    @Test
    @DisplayName("测试取消订单")
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_cancel_order.sql")
    void should_cancel_order() {
        // given
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
        orderRepository.update(order);

        // then
        Order newOrder = orderRepository.get(orderId).orElseThrow();
        List<DomainEventPo> events = domainEventPoService.list();
        assertThat(events).hasSize(1);

        var assertMap = new HashMap<>();
        assertMap.put("event", events.get(0).getContent());
        assertMap.put("order", newOrder);
        assertJSON(assertMap);
    }

    @Test
    @DisplayName("测试缓存查询")
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_get_in_cache.sql")
    void should_get_in_cache() {

        long orderId = 1L;
        Order order1 = orderRepository.getInCache(orderId).orElseThrow();

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableInfoHelper.getTableInfo(OrderPo.class).getTableName());

        Order order2 = orderRepository.getInCache(orderId).orElseThrow();

        assertThrows(IllegalArgumentException.class, order1::snapshot);
        assertThrows(IllegalArgumentException.class, order2::snapshot);
        var assertMap = Map.of("order1", order1, "order2", order2);
        assertJSON(assertMap);

    }

    @Test
    @DisplayName("测试缓存清除")
    @Sql("/sql/OrderRepositoryImplIntegrationTest/should_clear_cache_when_get_in_cache.sql")
    void should_clear_cache_when_get_in_cache() {
        long orderId = 1L;
        assertThat(orderRepository.getInCache(orderId).orElseThrow()).isNotNull();

        Order newOrder = orderRepository.getWithLockOrThrow(orderId);
        newOrder.cancel();
        orderRepository.update(newOrder);

        Order cacheOrder = orderRepository.getInCache(orderId).orElseThrow();
        assertThat(cacheOrder.isCancel()).isTrue();
        assertJSON(cacheOrder);
    }

}