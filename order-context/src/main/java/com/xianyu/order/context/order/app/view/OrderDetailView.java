package com.xianyu.order.context.order.app.view;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import com.xianyu.order.context.reference.product.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * <br/>
 * Created on : 2023-09-06 22:55
 * @author xian_yu_da_ma
 */
@Builder
@Jacksonized
@AllArgsConstructor
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.ANY)
public class OrderDetailView {

    @JsonIgnore
    private Order order;

    @JsonIgnore
    private OrderDetailViewContext orderDetailViewContext;

    ///////////////////////////////////////////////////////////////////////////
    // 订单相关
    ///////////////////////////////////////////////////////////////////////////

    public long getOrderId() {
        return order.getId();
    }

    public OrderStatus getOrderStatus() {
        return order.getOrderStatus();
    }

    public BigDecimal getActualPay() {
        return order.getActualPay();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 订单地址
    ///////////////////////////////////////////////////////////////////////////

    public OrderAddress getOrderAddress() {
        com.xianyu.order.context.order.domain.value.OrderAddress orderAddress = order.getOrderAddress();
        return OrderAddress.builder()
                .fullName(orderAddress.getFullName())
                .fullAddressLine(orderAddress.getFullAddressLine())
                .email(orderAddress.getEmail())
                .phoneNumber(orderAddress.getPhoneNumber())
                .country(orderAddress.getCountry())
                .build();
    }

    ///////////////////////////////////////////////////////////////////////////

    public List<OrderDetail> getOrderDetails() {
        Map<Integer, Product> productId2ProductInfo = orderDetailViewContext.getProductId2ProductInfo();
        return order.getOrderItems().toStream().map(orderDetail -> {
            return OrderDetail.builder()
                    .orderStatus(orderDetail.getOrderStatus())
                    .price(orderDetail.getPrice())
                    .locked(orderDetail.getLocked())
                    .picUrl(Optional.ofNullable(productId2ProductInfo.get(orderDetail.getProductId())).map(Product::getPicUrl).orElse(null))
                    .build();
        }).toList();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 商品信息

    @Getter
    @Builder
    public static class OrderAddress {

        private FullName fullName;

        private FullAddressLine fullAddressLine;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 电话
         */
        private String phoneNumber;

        /**
         * 国家
         */
        private String country;

    }

    @Getter
    @Builder
    public static class OrderDetail {
        private OrderStatus orderStatus;

        private BigDecimal price;

        /**
         * null:未知
         * true:锁定库存
         * false:缺货
         */
        private Boolean locked;

        /**
         * 图片链接
         */
        private String picUrl;
    }




}
