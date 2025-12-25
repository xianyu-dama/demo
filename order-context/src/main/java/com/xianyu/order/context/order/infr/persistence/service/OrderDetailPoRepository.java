package com.xianyu.order.context.order.infr.persistence.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.xianyu.order.context.order.infr.persistence.mapper.OrderDetailPoMapper;
import com.xianyu.order.context.order.infr.persistence.po.OrderDetailPo;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2024-10-29 22:40
 *
 * @author xian_yu_da_ma
 */
@Component
public class OrderDetailPoRepository extends CrudRepository<OrderDetailPoMapper, OrderDetailPo> {

    public List<OrderDetailPo> listByOrderId(long id) {
        LambdaQueryWrapper<OrderDetailPo> query = Wrappers.lambdaQuery(OrderDetailPo.class);
        query.eq(OrderDetailPo::getOrderId, id);
        return list(query);
    }

    public List<OrderDetailPo> listByOrderIds(Set<Long> orderIds) {
        LambdaQueryWrapper<OrderDetailPo> query = Wrappers.lambdaQuery(OrderDetailPo.class);
        query.in(OrderDetailPo::getOrderId, orderIds);
        return list(query);
    }

}
