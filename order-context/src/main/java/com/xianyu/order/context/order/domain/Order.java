package com.xianyu.order.context.order.domain;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import com.xianyu.component.ddd.composition.HasOne;
import com.xianyu.component.exception.BizException;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import com.xianyu.order.context.reference.inventory.SkuStockLock;
import com.xianyu.order.context.reference.parcel.Parcels;
import com.xianyu.order.context.reference.user.User;
import com.xianyu.order.context.sdk.order.event.OrderCanceledEvent;
import com.xianyu.order.context.sdk.order.event.OrderPlacedEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * 订单聚合根<br/>
 * 实体所有写的操作，必须通过聚合根调用，所以改成包可见(default)
 * 实体所有读操作，可以改成公共(public)
 * Created on : 2022-06-11 14:41
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@Setter(AccessLevel.PACKAGE)
public class Order extends BaseAggregation<Order, Long> {

    public static final int PLACE_ORDER_MAX_SKU_QUANTITY = 1;
    private final transient Parcels parcels;
    private final transient HasOne<User> user;
    private Long id;
    @NonNull
    private OrderStatus orderStatus;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal shouldPay;
    private BigDecimal actualPay;
    private OrderAddress orderAddress;
    @Builder.Default
    private OrderItems orderItems = new OrderItems(new ArrayList<>());
    /**
     * 扩展字段
     */
    @Nullable
    private Extension extension;
    @Builder.Default
    private Long userId = 1L;

    public long getOrderId() {
        return id;
    }

    public List<Integer> getProductIds() {
        return orderItems.getProductIds();
    }

    public void relocateTo(OrderAddress newOrderAddress) {
        if (isShipped()) {
            throw new BizException("订单已发货，不能修改地址");
        }
        orderAddress = newOrderAddress;
    }

    void lockStock(SkuStockLock skuStockLock) {
        if (!skuStockLock.locked()) {
            throw new BizException("库存不足");
        }
        orderItems.forEach(orderDetail -> orderDetail.updateLocked(true));
        extension = Optional.ofNullable(extension).orElse(Extension.builder().build()).toBuilder().lockId(skuStockLock.stockLockId()).build();
    }


    public void pay(BigDecimal actualPay, BigDecimal shouldPay) {
        this.actualPay = actualPay;
        this.shouldPay = shouldPay;
        orderStatus = orderStatus.pay();
    }

    /**
     * 取消订单
     */
    public void cancel() {
        orderStatus = orderStatus.cancel();
        orderItems.forEach(OrderItem::cancel);
        addEvent(OrderCanceledEvent.create(id));
    }

    /**
     * 是否取消
     * @return
     */
    public boolean isCancel() {
        return orderStatus.isCancel();
    }

    /**
     * 生单
     */
    void place() {
        orderStatus = OrderStatus.WAIT_PAY;
        orderItems.forEach(OrderItem::place);
        addEvent(OrderPlacedEvent.create(id));
    }

    public boolean isShipped() {
        return orderStatus.isShipped();
    }

    /**
     * 更新姓名
     * @param fullName
     */
    public void updateFullName(FullName fullName) {
        fullName.validate();
        orderAddress = orderAddress.toBuilder().fullName(fullName).build();
    }

    public boolean canCancel() {
        return false;
    }

    void add(@NonNull OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItems.validate();
    }

    @Override
    public Long id() {
        return id;
    }

    boolean isSkuQuantityValidate(int placeOrderMaxSkuQuantity) {
        return orderItems.toStream()
                .allMatch(od -> od.getQuantity() <= placeOrderMaxSkuQuantity);
    }

    public boolean isCanceled() {
        return orderStatus.isCancel();
    }
}
