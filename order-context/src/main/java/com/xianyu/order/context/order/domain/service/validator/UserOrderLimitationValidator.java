package com.xianyu.order.context.order.domain.service.validator;

import com.xianyu.component.exception.BizException;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.reference.user.User;
import com.xianyu.order.context.reference.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * 一个用户最多下10个订单<br/>
 * Created on : 2025-11-10 22:08
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class UserOrderLimitationValidator extends OrderValidator {

    private final UserRepository userRepository;

    @Override
    public void validate(@NonNull Order order) {
        User user = userRepository.getOrThrow(order.getUserId());
        long orderCount = user.orderIds().size();
        if (orderCount >= 10) {
            throw new BizException("一个用户最多下10个订单");
        }
    }
}
