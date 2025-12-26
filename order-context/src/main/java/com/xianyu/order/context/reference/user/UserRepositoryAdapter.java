package com.xianyu.order.context.reference.user;

import java.util.Optional;

import com.xianyu.order.context.order.app.service.OrderQueryService;
import com.xianyu.order.context.sdk.order.api.OrderQueryApiService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * 用户仓储适配器
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private OrderQueryApiService orderQueryApiService;

    @Override
    public Optional<User> get(Long id) {
        var orderIds = orderQueryApiService.queryOrderIdsByUserId(id);
        return Optional.of(User.builder().id(id).orderIds(() -> orderIds).build());
    }

}
