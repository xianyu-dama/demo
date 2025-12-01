package com.xianyu.order.context.order.app.assembler;

import com.xianyu.component.id.IdGenerator;
import com.xianyu.order.context.order.app.dto.PlaceOrderDetailDto;
import com.xianyu.order.context.order.app.dto.PlaceOrderDto;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.OrderBuilder;
import com.xianyu.order.context.order.domain.OrderDetail;
import com.xianyu.order.context.order.domain.factory.OrderFactory;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2023-09-17 13:49
 *
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class OrderAssembler {

    private final OrderFactory orderFactory;

    private final IdGenerator idGenerator;

    public Order toOrder(PlaceOrderDto req) {

        OrderAddress orderAddress = OrderAddress.builder()
            .email(req.email())
            .phoneNumber(req.phoneNumber())
            .fullName(FullName.of(req.firstName(), req.lastName()))
            .fullAddressLine(FullAddressLine.of(req.addressLine1(), req.addressLine2()))
            .country(req.country())
            .build();

        OrderBuilder orderBuilder = orderFactory.createOrderBuilder();

        for (PlaceOrderDetailDto detailReq : req.details()) {
            OrderDetail orderDetail = OrderDetail.builder()
                .productId(detailReq.getProductId())
                .price(detailReq.getPrice())
                .id(idGenerator.id())
                .quantity(detailReq.getQuantity())
                .build();
            orderBuilder.addOrderDetail(orderDetail);
        }

        return orderBuilder
            .currency(req.currency())
            .exchangeRate(req.exchangeRate())
            .shouldPay(req.shouldPay())
            .actualPay(req.actualPay())
            .orderAddress(orderAddress)
            .id(idGenerator.id())
            .extension(Extension.builder().isFirstOrder(req.isFirstOrder()).shipInsuranceFee(req.shipInsuranceFee()).build())
            .userId(req.userId())
            .orderStatus(OrderStatus.WAIT_PAY)
            .build();
    }

}
