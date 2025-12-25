package com.xianyu.order.context.order.app.service;

import com.xianyu.order.context.order.app.assembler.OrderAssembler;
import com.xianyu.order.context.order.app.dto.PlaceOrderDto;
import com.xianyu.order.context.order.app.dto.UpdateAddressDto;
import com.xianyu.order.context.order.app.dto.UpdateFullNameDto;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.OrderService;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.sdk.cart.api.ClearCartApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <br/>
 * Created on : 2024-05-31 22:21
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Valid
@Service
@RequiredArgsConstructor
public class OrderAppService {

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    private final OrderAssembler orderAssembler;

    private final ClearCartApiService clearCartApiService;

    /**
     * 生单
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public long placeOrder(@Valid PlaceOrderDto req) {

        log.info("place order: {}", req);

        // 构建订单聚合
        Order order = orderAssembler.toOrder(req);

        // 生单：order.place() 可见性变成default，委托给OrderService，体现封装
        orderService.place(order);

        // 保存订单
        orderRepository.add(order);

        clearCartApiService.clear(order.getUserId());

        return order.getId();
    }

    /**
     * 更新订单地址
     * @param updateOrderAddress
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(UpdateAddressDto updateOrderAddress) {
        Order order = orderRepository.getWithLockOrThrow(updateOrderAddress.orderId());

        OrderAddress newOrderAddress = updateOrderAddress.toOrderAddress();
        order.relocateTo(newOrderAddress);

        orderRepository.update(order);
    }

    /**
     * 只更新姓名
     * @param updateFullName
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFullName(UpdateFullNameDto updateFullName) {
        long orderId = updateFullName.orderId();
        Order order = orderRepository.getWithLockOrThrow(orderId);
        order.updateFullName(FullName.of(updateFullName.firstName(), updateFullName.lastName()));
        orderRepository.update(order);
    }

}
