package com.xianyu.order.context.order.app;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.id.IdGenerator;
import com.xianyu.component.utils.json.JsonUtils;
import com.xianyu.order.context.order.app.assembler.OrderAssembler;
import com.xianyu.order.context.order.app.dto.PlaceOrderDto;
import com.xianyu.order.context.order.app.dto.PlaceOrderItemDto;
import com.xianyu.order.context.order.app.service.OrderAppService;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <br/>
 * Created on : 2023-09-09 14:23
 * @author xian_yu_da_ma
 */
class PlaceOrderUseCaseIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private OrderAppService orderAppService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderAssembler orderAssembler;

    private static PlaceOrderDto createPlaceOrderDto(int quantity) {
        return new PlaceOrderDto("CNY", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, "email",
                "phoneNumber", "firstName", "lastName", "addressLine1", "addressLine2", "country",
            BigDecimal.TEN, true, List.of(new PlaceOrderItemDto(1, BigDecimal.ONE, quantity)), 1L);
    }

    @BeforeEach
    public void beforeEach() {
        // 在父类声明了mock bean
        doReturn(1L).when(idGenerator).id();
    }

    @Test
    @DisplayName("测试OrderDetailBuilder")
    void should_place_order() {

        PlaceOrderDto req = createPlaceOrderDto(1);
        System.out.println(JsonUtils.toJSONString(req));

        long orderId = orderAppService.placeOrder(req);
        Order order = orderRepository.getOrThrow(orderId);
        assertJSON(order);
    }

    @Test
    @DisplayName("测试工厂")
    void should_order_factory_create_order() {
        Order order = orderAssembler.toOrder(createPlaceOrderDto(1));
        assertJSON(order);
    }

    @Test
    @DisplayName("测试缓存查询")
    void should_place_order_in_cache() {

        long orderId = 1L;
        doReturn(orderId).when(idGenerator).id();

        assertThat(orderRepository.getInCache(orderId).isEmpty()).isTrue();
        orderAppService.placeOrder(createPlaceOrderDto(1));
        assertThat(orderRepository.getInCache(orderId).isPresent()).isTrue();
    }

    @Test
    @DisplayName("商品件数大于1件，需要抛异常")
    void should_throw_biz_exception_when_product_quantity_greater_max() {
        assertThatThrownBy(() -> orderAssembler.toOrder(createPlaceOrderDto(2)))
            .isInstanceOf(IllegalArgumentException.class);
    }

}