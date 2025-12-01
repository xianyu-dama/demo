package com.xianyu.order.context.order.domain.value;

import com.xianyu.component.exception.BizException;
import com.xianyu.component.utils.enums.EnumValue;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态<br/>
 * Created on : 2023-09-04 10:00
 *
 * @author xian_yu_da_ma
 */
@Getter
@AllArgsConstructor
public enum OrderStatus implements EnumValue<String>, Serializable {

    WAIT_PAY("WAIT_PAY", "待支付") {
        @Override
        public OrderStatus pay() {
            return PAID;
        }

        @Override
        public OrderStatus cancel() {
            return CANCELED;
        }
    }, PAID("PAID", "已支付") {
        @Override
        public OrderStatus pay() {
            throw new BizException("订单已支付");
        }

        @Override
        public OrderStatus cancel() {
            return CANCELED;
        }
    }, CANCELED("CANCELED", "已取消") {
        @Override
        public OrderStatus pay() {
            throw new BizException("取消的订单不能支付");
        }

        @Override
        public OrderStatus cancel() {
            throw new BizException("订单已取消");
        }
    }, SHIPPED("SHIPPED", "已发货") {
        @Override
        public OrderStatus pay() {
            throw new BizException("已发货的订单不能支付");
        }

        @Override
        public OrderStatus cancel() {
            throw new BizException("已发货的订单不能取消");
        }
    };

    private final String value;
    private final String desc;

    public abstract OrderStatus pay();

    public abstract OrderStatus cancel();

    public boolean isCancel() {
        return this == CANCELED;
    }

    public boolean isShipped() {
        return this == SHIPPED;
    }
}