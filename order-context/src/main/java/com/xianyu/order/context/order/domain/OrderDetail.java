package com.xianyu.order.context.order.domain;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单明细<br/>
 * Created on : 2023-08-31 12:51
 * @author xian_yu_da_ma
 */
@Slf4j
@Getter
@Jacksonized
@SuperBuilder
@Setter(AccessLevel.PACKAGE)
public class OrderDetail extends BaseEntity<Long> {

    private long id;

    private Integer productId;

    private OrderStatus orderStatus;

    private BigDecimal price;

    /**
     * null:未知
     * true:锁定库存
     * false:缺货
     */
    private Boolean locked;

    @Builder.Default
    private int quantity = 1;

    /**
     * 订单明细取消
     */
    void cancel() {
        orderStatus = orderStatus.cancel();
    }

    /**
     * 更新库存锁定状态
     * @param locked
     */
    void updateLocked(Boolean locked) {
        this.locked = locked;
    }

    @Override
    public Long id() {
        return id;
    }

    void place() {
        orderStatus = OrderStatus.WAIT_PAY;
    }
}
