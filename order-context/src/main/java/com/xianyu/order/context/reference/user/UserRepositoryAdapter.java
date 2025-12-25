package com.xianyu.order.context.reference.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xianyu.order.context.order.infr.persistence.mapper.OrderPoMapper;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import java.util.List;
import java.util.Optional;
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

    private final OrderPoMapper orderPoMapper;

    @Override
    public Optional<User> get(Long id) {
        LambdaQueryWrapper<OrderPo> query = Wrappers.lambdaQuery(OrderPo.class);
        query.eq(OrderPo::getUserId, id);
        query.select(OrderPo::getOrderId);
        List<Long> orderIds = orderPoMapper.selectList(query).stream()
                .map(OrderPo::getOrderId)
                .toList();
        return Optional.of(User.builder().id(id).orderIds(() -> orderIds).build());
    }

}
