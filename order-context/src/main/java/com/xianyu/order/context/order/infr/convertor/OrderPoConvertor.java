package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.component.utils.json.JsonUtils;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.OrderItem;
import com.xianyu.order.context.order.domain.OrderItems;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.infr.persistence.po.OrderDetailPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import java.util.List;
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
        OrderPo orderPo = new OrderPo();
        orderPo.setOrderId(order.getId());
        orderPo.setOrderStatus(order.getOrderStatus());
        orderPo.setCurrency(order.getCurrency());
        orderPo.setExchangeRate(order.getExchangeRate());
        orderPo.setShouldPay(order.getShouldPay());
        orderPo.setActualPay(order.getActualPay());
        orderPo.setExtension(JsonUtils.toJSONString(order.getExtension()));
        orderPo.setUserId(order.getUserId());
        setOrderAddress(orderPo, order.getOrderAddress());
        return orderPo;
    }

    public static List<OrderDetailPo> toOrderDetailPos(long orderId, OrderItems orderItems) {
        return orderItems.toStream().map(d -> toOrderDetailPo(orderId, d)).toList();
    }

    private static OrderDetailPo toOrderDetailPo(long orderId, OrderItem orderItem) {
        OrderDetailPo orderDetailPo = new OrderDetailPo();
        orderDetailPo.setId(orderItem.getId());
        orderDetailPo.setOrderId(orderId);
        orderDetailPo.setProductId(orderItem.getProductId());
        orderDetailPo.setOrderStatus(orderItem.getOrderStatus());
        orderDetailPo.setPrice(orderItem.getPrice());
        orderDetailPo.setLocked(orderItem.getLocked());
        return orderDetailPo;
    }

    public static void setOrderAddress(OrderPo orderPo, OrderAddress orderAddress) {
        orderPo.setEmail(orderAddress.getEmail());
        orderPo.setPhoneNumber(orderAddress.getPhoneNumber());
        orderPo.setCountry(orderAddress.getCountry());

        FullName fullName = orderAddress.getFullName();
        orderPo.setFirstName(fullName.getFirstName());
        orderPo.setLastName(fullName.getLastName());

        FullAddressLine fullAddressLine = orderAddress.getFullAddressLine();
        orderPo.setAddressLine1(fullAddressLine.getAddressLine1());
        orderPo.setAddressLine2(fullAddressLine.getAddressLine2());
    }

}
