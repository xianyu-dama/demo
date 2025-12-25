package com.xianyu.order.context.order.infr.persistence.po;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xianyu.base.BaseUnitTest;
import com.xianyu.order.context.Immutables;
import java.util.Collections;
import org.babyfish.jimmer.DraftObjects;
import org.babyfish.jimmer.ImmutableObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderPoUnitTest extends BaseUnitTest {

    @Test
    @DisplayName("测试orderPo的equals方法")
    void should_order_equals() throws JsonProcessingException {
        String orderPoString = """
                {
                    "addTime": "2025-11-10T15:09:43.888606",
                    "updateTime": "2025-11-10T15:09:43.888606",
                    "orderId": 1,
                    "orderStatus": "WAIT_PAY",
                    "currency": "CNY",
                    "exchangeRate": 1,
                    "shouldPay": 88,
                    "actualPay": 88,
                    "email": "123456@qq.com",
                    "phoneNumber": "13800000000",
                    "orderItems": [
                        {
                            "addTime": "2025-11-10T15:09:43.888606",
                            "updateTime": "2025-11-10T15:09:43.888606",
                            "id": 1,
                            "productId": 1,
                            "orderStatus": "WAIT_PAY",
                            "price": 1,
                            "locked": null
                        },
                        {
                            "addTime": "2025-11-10T15:09:43.888606",
                            "updateTime": "2025-11-10T15:09:43.888606",
                            "id": 2,
                            "productId": 2,
                            "orderStatus": "WAIT_PAY",
                            "price": 10,
                            "locked": null
                        }
                    ],
                    "firstName": "first",
                    "lastName": "last",
                    "addressLine1": "line1",
                    "addressLine2": "line2",
                    "country": "CHINA",
                    "extension": {
                        "shipInsuranceFee": null,
                        "isFirstOrder": null
                    },
                    "userId": 1
                }
                """;

        var o1 = ImmutableObjects.fromString(OrderPo.class, orderPoString);
        var o2 = ImmutableObjects.fromString(OrderPo.class, orderPoString);
        assertThat(o1.equals(o2)).isTrue();

        // 只比较非id字段
        var o1NoId = Immutables.createOrderPo(o1, orderDraft -> {
            DraftObjects.unload(orderDraft, OrderPoProps.ORDER_ID);
        });
        var o2NoId = Immutables.createOrderPo(o2, orderDraft -> {
            DraftObjects.unload(orderDraft, OrderPoProps.ORDER_ID);
        });
        assertThat(o1NoId.equals(o2NoId)).isTrue();

        var o1NoIdAndOrderDetails = Immutables.createOrderPo(o1, orderDraft -> {
            DraftObjects.unload(orderDraft, OrderPoProps.ORDER_ID);
            DraftObjects.set(orderDraft, OrderPoProps.ORDER_ITEMS, Collections.emptyList());
        });
        // 最终还是比较equals方法
        assertThat(o1NoIdAndOrderDetails.equals(o2NoId)).isFalse();
    }

}
