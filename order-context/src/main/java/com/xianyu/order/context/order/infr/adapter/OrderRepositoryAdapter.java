package com.xianyu.order.context.order.infr.adapter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.infr.convertor.OrderConvertor;
import com.xianyu.order.context.order.infr.convertor.OrderPoConvertor;
import com.xianyu.order.context.order.infr.persistence.mapper.OrderDetailPoMapper;
import com.xianyu.order.context.order.infr.persistence.mapper.OrderPoMapper;
import com.xianyu.order.context.order.infr.persistence.po.OrderDetailPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.order.infr.persistence.service.OrderDetailPoRepository;
import com.xianyu.order.context.order.infr.persistence.service.OrderPoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单仓储适配器
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderPoMapper orderPoMapper;
    private final OrderDetailPoMapper orderDetailPoMapper;
    private final OrderPoRepository orderPoRepository;
    private final OrderDetailPoRepository orderDetailPoRepository;

    @Override
    public Optional<Order> get(Long id) {
        OrderPo orderPo = orderPoMapper.selectById(id);
        if (orderPo == null) {
            return Optional.empty();
        }

        List<OrderDetailPo> orderDetailPos = orderDetailPoRepository.listByOrderId(id);
        Order order = OrderConvertor.toOrder(orderPo, orderDetailPos);
        return Optional.of(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Order order) {
        OrderPo orderPo = OrderPoConvertor.toOrderPo(order);
        orderPoMapper.insert(orderPo);

        List<OrderDetailPo> orderDetailPos = OrderPoConvertor.toOrderDetailPos(order.getId(), order.getOrderItems());
        for (OrderDetailPo orderDetailPo : orderDetailPos) {
            orderDetailPoMapper.insert(orderDetailPo);
        }

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Order order) {
        OrderPo orderPo = OrderPoConvertor.toOrderPo(order);
        int result = orderPoMapper.updateById(orderPo);

        // 删除旧的订单明细
        LambdaQueryWrapper<OrderDetailPo> deleteQuery = Wrappers.lambdaQuery(OrderDetailPo.class);
        deleteQuery.eq(OrderDetailPo::getOrderId, order.getId());
        orderDetailPoMapper.delete(deleteQuery);

        // 插入新的订单明细
        List<OrderDetailPo> orderDetailPos = OrderPoConvertor.toOrderDetailPos(order.getId(), order.getOrderItems());
        for (OrderDetailPo orderDetailPo : orderDetailPos) {
            orderDetailPoMapper.insert(orderDetailPo);
        }

        return result;
    }

    @Override
    public Optional<Order> getInCache(Long id) {
        // 暂时直接从数据库获取，后续可以添加缓存逻辑
        return get(id);
    }

    @Override
    public Order getWithLockOrThrow(Long id) {
        OrderPo orderPo = orderPoRepository.getForUpdateById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在:" + id));
        
        List<OrderDetailPo> orderDetailPos = orderDetailPoRepository.listByOrderId(id);
        return OrderConvertor.toOrder(orderPo, orderDetailPos);
    }

}
