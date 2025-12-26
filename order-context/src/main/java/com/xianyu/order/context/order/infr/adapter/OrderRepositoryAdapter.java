package com.xianyu.order.context.order.infr.adapter;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import com.xianyu.component.utils.DiffUtils;
import com.xianyu.component.utils.json.JsonUtils;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单仓储适配器
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"order"})
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
    public Long add(Order order) {
        OrderPo orderPo = OrderPoConvertor.toOrderPo(order);
        orderPoRepository.save(orderPo);
        orderDetailPoRepository.saveBatch(OrderPoConvertor.toOrderDetailPos(orderPo.getOrderId(), order.getOrderItems()), 200);
        return orderPo.getOrderId();
    }

    @Override
    @CacheEvict(key = "#order.id")
    public int update(Order order) {
        ImmutablePair<List<OrderDetailPo>, List<OrderDetailPo>> leftToAddAndRightToUpdate = diffOrderDetailPos(order);
        OrderPo orderPo = OrderPoConvertor.toOrderPo(order);
        updateByVersion(orderPo);
        saveAndUpdate(leftToAddAndRightToUpdate.getLeft(), leftToAddAndRightToUpdate.getRight());
        return 1;
    }

    private void updateByVersion(OrderPo orderPo) {
        boolean success = orderPoRepository.updateById(orderPo);
        // 只有订单才需要判断版本号
        if (!success) {
            log.info("根据版本号更新失败 {}", JsonUtils.toJSONString(orderPo));
            throw new ConcurrentModificationException("订单信息发生变化, 修改失败");
        }
    }

    private void saveAndUpdate(List<OrderDetailPo> addOrderDetailPos, List<OrderDetailPo> updateOrderDetailPos) {
        orderDetailPoRepository.saveBatch(addOrderDetailPos, 200);
        orderDetailPoRepository.updateBatchById(updateOrderDetailPos, 200);
    }

    /**
     * diff出需要新增和更新的订单明细
     * @param order
     * @return
     */
    private static ImmutablePair<List<OrderDetailPo>, List<OrderDetailPo>> diffOrderDetailPos(Order order) {
        Order snapshot = order.snapshot();
        List<OrderDetailPo> currentOrderDetailPos = OrderPoConvertor.toOrderDetailPos(order.getId(), order.getOrderItems());
        List<OrderDetailPo> snapshotOrderDetailPos = OrderPoConvertor.toOrderDetailPos(snapshot.getId(), snapshot.getOrderItems());
        return DiffUtils.diff(OrderDetailPo.class, snapshotOrderDetailPos, currentOrderDetailPos);
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
