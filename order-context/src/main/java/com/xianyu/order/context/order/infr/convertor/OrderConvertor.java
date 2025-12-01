package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.inventory.context.sdk.inventory.dto.LockStockDto;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.OrderDetail;
import com.xianyu.order.context.order.domain.OrderDetails;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.reference.parcel.ParcelsFactory;
import com.xianyu.order.context.reference.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2023-08-30 23:10
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class OrderConvertor {

    private final ParcelsFactory parcelsFactory;

    private final UserRepository userRepository;

    public static OrderDetails toOrderDetails(List<OrderItemPo> orderDetailPos) {
        return new OrderDetails(orderDetailPos.stream()
                .map(OrderConvertor::toOrderDetail).toList());
    }

    public static OrderDetail toOrderDetail(OrderItemPo orderDetailPo) {
        return OrderDetail.builder()
                .id(orderDetailPo.id())
                .productId(orderDetailPo.productId())
                .orderStatus(orderDetailPo.orderStatus())
                .price(orderDetailPo.price())
                .locked(orderDetailPo.locked())
                .build();
    }

    public static OrderAddress toOrderAddress(OrderPo orderPo) {
        return OrderAddress.builder()
                .fullName(orderPo.fullName())
                .fullAddressLine(orderPo.fullAddressLine())
                .email(orderPo.email())
                .phoneNumber(orderPo.phoneNumber())
                .country(orderPo.country())
                .build();
    }

    public Order toOrder(OrderPo orderPo) {
        return Order.builder()
                .id(orderPo.orderId())
                .orderStatus(orderPo.orderStatus())
                .currency(orderPo.currency())
                .exchangeRate(orderPo.exchangeRate())
                .shouldPay(orderPo.shouldPay())
                .actualPay(orderPo.actualPay())
                .orderAddress(toOrderAddress(orderPo))
                .orderDetails(toOrderDetails(orderPo.orderItems()))
                .extension(orderPo.extension())
                .userId(orderPo.userId())
                .parcels(parcelsFactory.create(orderPo.orderId()))
                .user(userRepository.getReference(orderPo.userId()))
                .build();
    }

    public List<Order> toOrders(List<OrderPo> orders) {
        return orders.stream().map(this::toOrder).toList();
    }

    public LockStockDto toLockStockRequest(Order order) {
        List<LockStockDto.Sku> skus = order.getOrderDetails().toStream()
            .map(detail -> LockStockDto.Sku.of(Long.valueOf(detail.getProductId()), Long.valueOf(detail.getQuantity())))
            .toList();
        return LockStockDto.builder()
            .bizId(String.valueOf(order.getOrderId()))
            .skus(skus)
            .build();
    }
}
