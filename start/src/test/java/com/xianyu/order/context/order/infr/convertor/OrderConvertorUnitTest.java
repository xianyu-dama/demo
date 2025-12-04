package com.xianyu.order.context.order.infr.convertor;

import com.xianyu.base.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderConvertorUnitTest extends BaseUnitTest {

    @Test
    @DisplayName("测试字段映射")
    void toOrderDetail() {
        // 使用 Jimmer 的 Draft API 创建实体实例
        /*OrderDetailPo orderDetailPo = OrderDetailPoDraft.$.produce(draft -> {
            draft.setId(0L);
            draft.setSkuId(0);
            draft.setOrderStatus(OrderStatus.WAIT_PAY);
            draft.setPrice(BigDecimal.TEN);
            draft.setLocked(false);
            // 创建关联的 OrderPo
            draft.setOrder(OrderPoDraft.$.produce(orderDraft -> {
                orderDraft.setOrderId(0L);
            }));
        });

        OrderDetail orderDetail = OrderConvertor.toOrderDetail(orderDetailPo);
        assertJSON(orderDetail);*/
    }

    @Test
    @DisplayName("测试订单地址字段映射")
    void toOrderAddress() {
        // 使用 Jimmer 的 Draft API 创建实体实例
        /*OrderPo orderPo = OrderPoDraft.$.produce(draft -> {
            draft.setOrderId(888L);
            draft.setEmail("thisIsEmail@qq.com");
            draft.setPhoneNumber("123456");
            draft.setFirstName("firstName");
            draft.setLastName("lastName");
            draft.setAddressLine1("addressLine1");
            draft.setAddressLine2("addreaaLine2");
            draft.setCountry("country");
        });

        OrderAddress orderAddress = OrderConvertor.toOrderAddress(orderPo);
        assertJSON(orderAddress);*/
    }
}