package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.order.context.Immutables;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPoDraft;
import com.xianyu.order.context.order.infr.persistence.po.OrderItemPoProps;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPoDraft;
import com.xianyu.order.context.order.infr.persistence.po.OrderPoProps;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.DraftObjects;
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

    public static OrderPo toUpdateOrderPo(Order order) {
        OrderPo currentOrderPo = toOrderPo(order);

        OrderPo snapshotOrderPo = toOrderPo(order.snapshot());
        var snapshotOrderDetails = snapshotOrderPo.orderItems().stream().collect(Collectors.toMap(OrderItemPo::id, Function.identity()));

        var updateOrderDetailPos = currentOrderPo.orderItems()
            .stream()
            .filter(currentOrderDetailPo -> {
                OrderItemPo snapshotOrderDetailPo = snapshotOrderDetails.get(currentOrderDetailPo.id());
                // 新增
                if (Objects.isNull(snapshotOrderDetailPo)) {
                    return true;
                }
                // 删除（对应的是对象脱钩），业务场景下不允许删除，所以这里不处理
                // 更新，id作为主键，id相同认为是同一个对象，所以要设置id unload
                OrderItemPo updateOrderDetailPoNoId = Immutables.createOrderItemPo(currentOrderDetailPo,
                    draft -> DraftObjects.unload(draft, OrderItemPoProps.ID));
                OrderItemPo snapshotOrderDetailPoNoId = Immutables.createOrderItemPo(snapshotOrderDetailPo,
                    draft -> DraftObjects.unload(draft, OrderItemPoProps.ID));
                return !Objects.equals(updateOrderDetailPoNoId, snapshotOrderDetailPoNoId);
            }).toList();

        return Immutables.createOrderPo(currentOrderPo,
            draft -> DraftObjects.set(draft, OrderPoProps.ORDER_ITEMS, updateOrderDetailPos));
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
