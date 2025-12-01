package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.OrderItem;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.sdk.order.dto.rsp.ExtensionDto;
import com.xianyu.order.context.sdk.order.dto.rsp.FullAddressLineDto;
import com.xianyu.order.context.sdk.order.dto.rsp.FullNameDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderAddressDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderDetailDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderReadOnlyConvertor {

    public OrderReadOnlyDto toDto(Order order) {
        return OrderReadOnlyDto.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus().name())
                .currency(order.getCurrency())
                .exchangeRate(order.getExchangeRate())
                .shouldPay(order.getShouldPay())
                .actualPay(order.getActualPay())
                .orderAddress(toOrderAddressDto(order.getOrderAddress()))
                .orderDetails(order.getOrderItems().toStream().map(this::toOrderDetailDto).toList())
                .extension(toExtensionDto(order.getExtension()))
                .userId(order.getUserId())
                .cancelFlag(order.isCanceled())
                .shippedFlag(order.isShipped())
                .canCancelFlag(order.canCancel())
                .build();
    }

    private OrderAddressDto toOrderAddressDto(OrderAddress address) {
        if (address == null) {
            return null;
        }
        return OrderAddressDto.builder()
                .fullName(FullNameDto.builder()
                        .firstName(address.getFullName().getFirstName())
                        .lastName(address.getFullName().getLastName())
                        .build())
                .fullAddressLine(FullAddressLineDto.builder()
                        .addressLine1(address.getFullAddressLine().getAddressLine1())
                        .addressLine2(address.getFullAddressLine().getAddressLine2())
                        .build())
                .email(address.getEmail())
                .phoneNumber(address.getPhoneNumber())
                .country(address.getCountry())
                .build();
    }

    private OrderDetailDto toOrderDetailDto(OrderItem d) {
        return OrderDetailDto.builder()
                .id(d.getId())
                .productId(d.getProductId())
                .orderStatus(d.getOrderStatus().name())
                .price(d.getPrice())
                .locked(d.getLocked())
                .quantity(d.getQuantity())
                .build();
    }

    private ExtensionDto toExtensionDto(Extension ext) {
        if (ext == null) {
            return null;
        }
        return ExtensionDto.builder()
                .shipInsuranceFee(ext.getShipInsuranceFee())
                .isFirstOrder(ext.getIsFirstOrder())
                .lockId(ext.getLockId())
                .build();
    }

}
