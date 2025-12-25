package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.component.utils.json.JsonUtils;
import com.xianyu.inventory.context.sdk.inventory.dto.LockStockDto;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.OrderItem;
import com.xianyu.order.context.order.domain.OrderItems;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.infr.persistence.po.OrderDetailPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2023-08-30 23:10
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class OrderConvertor {

    public static Order toOrder(OrderPo orderPo,
            List<OrderDetailPo> orderDetailPos) {
        return Order.builder()
                .id(orderPo.getOrderId())
                .orderStatus(orderPo.getOrderStatus())
                .currency(orderPo.getCurrency())
                .exchangeRate(orderPo.getExchangeRate())
                .shouldPay(orderPo.getShouldPay())
                .actualPay(orderPo.getActualPay())
                .orderAddress(toOrderAddress(orderPo))
                .orderItems(toOrderItems(orderDetailPos))
                .extension(JsonUtils.json2JavaBean(orderPo.getExtension(), Extension.class))
                .userId(orderPo.getUserId())
                .build();
    }

    public static OrderItems toOrderItems(List<OrderDetailPo> orderDetailPos) {
        return new OrderItems(orderDetailPos.stream().map(OrderConvertor::toOrderItem).toList());
    }

    public static OrderItem toOrderItem(OrderDetailPo orderDetailPo) {
        return OrderItem.builder()
                .id(orderDetailPo.getId())
                .productId(orderDetailPo.getProductId())
                .orderStatus(orderDetailPo.getOrderStatus())
                .price(orderDetailPo.getPrice())
                .locked(orderDetailPo.getLocked())
                .build();
    }

    public static OrderAddress toOrderAddress(OrderPo orderPo) {
        return OrderAddress.builder()
                .fullName(FullName.of(orderPo.getFirstName(), orderPo.getLastName()))
                .fullAddressLine(FullAddressLine.of(orderPo.getAddressLine1(), orderPo.getAddressLine2()))
                .email(orderPo.getEmail())
                .phoneNumber(orderPo.getPhoneNumber())
                .country(orderPo.getCountry())
                .build();
    }


    public static List<Order> toOrders(List<OrderPo> orders, List<OrderDetailPo> orderDetails) {
        Map<Long, List<OrderDetailPo>> orderId2OrderDetailPos = StreamEx.of(orderDetails).groupingBy(OrderDetailPo::getOrderId);
        return orders.stream().map(order -> toOrder(order, orderId2OrderDetailPos.get(order.getOrderId()))).toList();
    }

    public LockStockDto toLockStockRequest(Order order) {
        List<LockStockDto.Product> products = order.getOrderItems().toStream()
                .map(orderItem -> LockStockDto.Product.builder()
                        .productId(orderItem.getProductId().longValue())
                        .quantity((long) orderItem.getQuantity())
                        .build())
                .toList();
        
        return LockStockDto.builder()
                .bizId(String.valueOf(order.getId()))
                .products(products)
                .build();
    }

}
