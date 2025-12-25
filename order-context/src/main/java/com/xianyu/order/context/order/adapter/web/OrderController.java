package com.xianyu.order.context.order.adapter.web;

import com.xianyu.order.context.order.app.dto.PlaceOrderDto;
import com.xianyu.order.context.order.app.service.OrderAppService;
import com.xianyu.order.context.order.app.service.OrderQueryAppService;
import com.xianyu.order.context.order.app.view.OrderDetailView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生单<br/>
 * Created on : 2023-12-11 20:22
 * @author xian_yu_da_ma
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderAppService orderAppService;

    private final OrderQueryAppService orderQueryAppService;

    @PutMapping
    public Long placeOrder(@RequestBody PlaceOrderDto placeOrderDto) {
        return orderAppService.placeOrder(placeOrderDto);
    }

    @GetMapping("/{orderId}")
    public OrderDetailView orderView(@PathVariable long orderId) {
        return orderQueryAppService.detail(orderId);
    }
}
