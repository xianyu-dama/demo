package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.component.utils.json.JsonUtils;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.OrderItem;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderAddress;
import com.xianyu.order.context.order.infr.persistence.po.OrderDetailPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.sdk.order.dto.rsp.ExtensionDto;
import com.xianyu.order.context.sdk.order.dto.rsp.FullAddressLineDto;
import com.xianyu.order.context.sdk.order.dto.rsp.FullNameDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderAddressDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderDetailDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;

import java.util.List;
import java.util.Optional;

/**
 * Order 转换为 OrderReadOnlyDto 的转换器
 * <br/>
 * Created on : 2025-12-26
 *
 * @author xian_yu_da_ma
 */
public class OrderReadOnlyDtoConvertor {

    /**
     * 从领域对象 Order 转换为 OrderReadOnlyDto
     */
    public static OrderReadOnlyDto toOrderReadOnlyDto(Order order) {
        return OrderReadOnlyDto.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus().name())
                .currency(order.getCurrency())
                .exchangeRate(order.getExchangeRate())
                .shouldPay(order.getShouldPay())
                .actualPay(order.getActualPay())
                .orderAddress(toOrderAddressDto(order.getOrderAddress()))
                .orderDetails(toOrderDetailDtos(order.getOrderItems().getCollection().stream().toList()))
                .extension(toExtensionDto(order.getExtension()))
                .userId(order.getUserId())
                .cancelFlag(order.isCanceled())
                .shippedFlag(order.isShipped())
                .canCancelFlag(order.canCancel())
                .build();
    }

    /**
     * 从 PO 对象转换为 OrderReadOnlyDto
     */
    public static OrderReadOnlyDto toOrderReadOnlyDto(OrderPo orderPo, List<OrderDetailPo> orderDetailPos) {
        return OrderReadOnlyDto.builder()
                .orderId(orderPo.getOrderId())
                .orderStatus(orderPo.getOrderStatus().name())
                .currency(orderPo.getCurrency())
                .exchangeRate(orderPo.getExchangeRate())
                .shouldPay(orderPo.getShouldPay())
                .actualPay(orderPo.getActualPay())
                .orderAddress(toOrderAddressDto(orderPo))
                .orderDetails(toOrderDetailDtosFromPo(orderDetailPos))
                .extension(toExtensionDtoFromJson(orderPo.getExtension()))
                .userId(orderPo.getUserId())
                .cancelFlag(orderPo.getOrderStatus().isCancel())
                .shippedFlag(orderPo.getOrderStatus().isShipped())
                .canCancelFlag(false) // 默认值，需要根据业务规则判断
                .build();
    }

    private static OrderAddressDto toOrderAddressDto(OrderAddress orderAddress) {
        if (orderAddress == null) {
            return null;
        }
        return OrderAddressDto.builder()
                .fullName(toFullNameDto(orderAddress.getFullName()))
                .fullAddressLine(toFullAddressLineDto(orderAddress.getFullAddressLine()))
                .email(orderAddress.getEmail())
                .phoneNumber(orderAddress.getPhoneNumber())
                .country(orderAddress.getCountry())
                .build();
    }

    private static OrderAddressDto toOrderAddressDto(OrderPo orderPo) {
        if (orderPo == null) {
            return null;
        }
        return OrderAddressDto.builder()
                .fullName(FullNameDto.builder()
                        .firstName(orderPo.getFirstName())
                        .lastName(orderPo.getLastName())
                        .build())
                .fullAddressLine(FullAddressLineDto.builder()
                        .addressLine1(orderPo.getAddressLine1())
                        .addressLine2(orderPo.getAddressLine2())
                        .build())
                .email(orderPo.getEmail())
                .phoneNumber(orderPo.getPhoneNumber())
                .country(orderPo.getCountry())
                .build();
    }

    private static FullNameDto toFullNameDto(FullName fullName) {
        if (fullName == null) {
            return null;
        }
        return FullNameDto.builder()
                .firstName(fullName.getFirstName())
                .lastName(fullName.getLastName())
                .build();
    }

    private static FullAddressLineDto toFullAddressLineDto(FullAddressLine fullAddressLine) {
        if (fullAddressLine == null) {
            return null;
        }
        return FullAddressLineDto.builder()
                .addressLine1(fullAddressLine.getAddressLine1())
                .addressLine2(fullAddressLine.getAddressLine2())
                .build();
    }

    private static List<OrderDetailDto> toOrderDetailDtos(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return List.of();
        }
        return orderItems.stream()
                .map(OrderReadOnlyDtoConvertor::toOrderDetailDto)
                .toList();
    }

    private static List<OrderDetailDto> toOrderDetailDtosFromPo(List<OrderDetailPo> orderDetailPos) {
        if (orderDetailPos == null) {
            return List.of();
        }
        return orderDetailPos.stream()
                .map(OrderReadOnlyDtoConvertor::toOrderDetailDto)
                .toList();
    }

    private static OrderDetailDto toOrderDetailDto(OrderItem orderItem) {
        return OrderDetailDto.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .orderStatus(orderItem.getOrderStatus().name())
                .price(orderItem.getPrice())
                .locked(orderItem.getLocked())
                .quantity(orderItem.getQuantity())
                .build();
    }

    private static OrderDetailDto toOrderDetailDto(OrderDetailPo orderDetailPo) {
        return OrderDetailDto.builder()
                .id(orderDetailPo.getId())
                .productId(orderDetailPo.getProductId())
                .orderStatus(orderDetailPo.getOrderStatus().name())
                .price(orderDetailPo.getPrice())
                .locked(orderDetailPo.getLocked())
                .quantity(1) // OrderDetailPo 没有 quantity 字段，默认为1
                .build();
    }

    private static ExtensionDto toExtensionDto(Extension extension) {
        if (extension == null) {
            return null;
        }
        return ExtensionDto.builder()
                .shipInsuranceFee(extension.getShipInsuranceFee())
                .isFirstOrder(extension.getIsFirstOrder())
                .lockId(extension.getLockId())
                .build();
    }

    private static ExtensionDto toExtensionDtoFromJson(String extensionJson) {
        if (extensionJson == null || extensionJson.isBlank()) {
            return null;
        }
        Extension extension = JsonUtils.json2JavaBean(extensionJson, Extension.class);
        return toExtensionDto(extension);
    }
}
