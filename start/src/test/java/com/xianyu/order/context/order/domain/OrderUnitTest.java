package com.xianyu.order.context.order.domain;

import com.xianyu.base.BaseUnitTest;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import com.xianyu.order.context.reference.inventory.ProductStockLock;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderUnitTest extends BaseUnitTest {

    @Test
    @DisplayName("锁库存成功：全部订单明细设置为locked，扩展写入lockId")
    void should_lock_all_items_and_set_extension_lockId_when_locked() {
        OrderItem od1 = OrderItem.builder()
            .id(101)
            .productId(1)
            .orderStatus(OrderStatus.WAIT_PAY)
            .price(BigDecimal.ONE)
            .quantity(2)
            .build();

        OrderItem od2 = OrderItem.builder()
            .id(102)
            .productId(2)
            .orderStatus(OrderStatus.WAIT_PAY)
            .price(BigDecimal.TEN)
            .quantity(1)
            .build();

        Order order = Order.builder()
            .id(1L)
            .orderStatus(OrderStatus.WAIT_PAY)
            .orderItems(new OrderItems(List.of(od1, od2)))
            .extension(Extension.builder().build())
            .build();
        order.lockStock(ProductStockLock.builder().locked(true).stockLockId("LOCK123").build());

        OrderItem d1 = order.getOrderItems().toStream().filter(d -> d.getProductId() == 1).findFirst().orElseThrow();
        OrderItem d2 = order.getOrderItems().toStream().filter(d -> d.getProductId() == 2).findFirst().orElseThrow();

        assertThat(d1.getLocked()).isTrue();
        assertThat(d2.getLocked()).isTrue();
        assertThat(order.getExtension().getLockId()).isNotBlank();
    }

    @Test
    @DisplayName("锁库存失败：抛出业务异常")
    void should_throw_when_not_locked() {
        Order order = Order.builder()
            .id(1L)
            .orderStatus(OrderStatus.WAIT_PAY)
            .orderItems(new OrderItems(List.of(
                OrderItem.builder().id(201).productId(3).orderStatus(OrderStatus.WAIT_PAY).price(BigDecimal.ONE).quantity(1).build()
            )))
            .extension(Extension.builder().build())
            .build();

        assertThrows(com.xianyu.component.exception.BizException.class, () -> {
            order.lockStock(ProductStockLock.builder().locked(false).stockLockId("LOCK456").build());
        });
    }
}
