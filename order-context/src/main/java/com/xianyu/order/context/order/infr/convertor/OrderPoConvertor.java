package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPoDraft;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPoDraft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2023-09-24 22:44
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class OrderPoConvertor {

    public static OrderPo toOrderPo(Order order) {
        return OrderPoDraft.$.produce(orderDraft -> {
            orderDraft.setOrderId(order.getId());
            orderDraft.setOrderStatus(order.getOrderStatus());
            orderDraft.setCurrency(order.getCurrency());
            orderDraft.setExchangeRate(order.getExchangeRate());
            orderDraft.setShouldPay(order.getShouldPay());
            orderDraft.setActualPay(order.getActualPay());
            orderDraft.setExtension(order.getExtension());
            orderDraft.setOrderItems(order.getOrderItems()
                .toStream().map(orderDetail ->
                    OrderItemPoDraft.$.produce(orderDetailDraft -> {
                        orderDetailDraft.setId(orderDetail.getId());
                        orderDetailDraft.setOrderId(order.getId());
                        orderDetailDraft.setProductId(orderDetail.getProductId());
                        orderDetailDraft.setOrderStatus(orderDetail.getOrderStatus());
                        orderDetailDraft.setPrice(orderDetail.getPrice());
                        orderDetailDraft.setLocked(orderDetail.getLocked());
                    })).toList());
            orderDraft.setUserId(order.getUserId());
            setOrderAddress(orderDraft, order.getOrderAddress());
        });
    }

    public static void setOrderAddress(OrderPoDraft orderDraft, OrderAddress orderAddress) {
        orderDraft.setEmail(orderAddress.getEmail());
        orderDraft.setPhoneNumber(orderAddress.getPhoneNumber());
        orderDraft.setCountry(orderAddress.getCountry());

        FullName fullName = orderAddress.getFullName();
        orderDraft.setFirstName(fullName.getFirstName());
        orderDraft.setLastName(fullName.getLastName());

        FullAddressLine fullAddressLine = orderAddress.getFullAddressLine();
        orderDraft.setAddressLine1(fullAddressLine.getAddressLine1());
        orderDraft.setAddressLine2(fullAddressLine.getAddressLine2());
    }

}
