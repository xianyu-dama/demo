package com.xianyu.order.context.order.domain.service.validator;

import com.xianyu.order.context.order.domain.Order;
import org.jspecify.annotations.NonNull;
import org.springframework.validation.annotation.Validated;

/**
 * 订单校验<br/>
 * Created on : 2025-04-27 20:14
 * @author xian_yu_da_ma
 */
@Validated
public abstract class OrderValidator {

    public abstract void validate(@NonNull Order order);

}
