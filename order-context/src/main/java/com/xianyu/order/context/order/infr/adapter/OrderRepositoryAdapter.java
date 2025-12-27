package com.xianyu.order.context.order.infr.adapter;

import com.xianyu.component.utils.DiffUtils;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.infr.convertor.OrderConvertor;
import com.xianyu.order.context.order.infr.convertor.OrderPoConvertor;
import com.xianyu.order.context.order.infr.persistence.mapper.OrderPoMapper;
import com.xianyu.order.context.order.infr.persistence.po.OrderDetailPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.order.infr.persistence.service.OrderDetailPoRepository;
import com.xianyu.order.context.order.infr.persistence.service.OrderPoRepository;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

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

    /**
     * diff出需要新增、更新和删除的订单明细
     * @param order
     * @return left=新增列表, middle=更新列表, right=删除列表
     */
    private static ImmutableTriple<List<OrderDetailPo>, List<OrderDetailPo>, List<OrderDetailPo>> diffOrderDetailPos(Order order) {
        Order snapshot = order.snapshot();
        List<OrderDetailPo> currentOrderDetailPos = OrderPoConvertor.toOrderDetailPos(order.getId(), order.getOrderItems());
        List<OrderDetailPo> snapshotOrderDetailPos = OrderPoConvertor.toOrderDetailPos(snapshot.getId(), snapshot.getOrderItems());
        return DiffUtils.diff(OrderDetailPo.class, snapshotOrderDetailPos, currentOrderDetailPos);
    }

    @Override
    @CacheEvict(key = "#order.id")
    public int update(Order order) {
        ImmutableTriple<List<OrderDetailPo>, List<OrderDetailPo>, List<OrderDetailPo>> diffResult = diffOrderDetailPos(order);
        OrderPo orderPo = OrderPoConvertor.toOrderPo(order);
        boolean success = orderPoRepository.updateById(orderPo);
        if (!success) {
            throw new ConcurrentModificationException("并发修改异常");
        }
        saveUpdateAndRemove(diffResult.getLeft(), diffResult.getMiddle(), diffResult.getRight());
        return 1;
    }

    private void saveUpdateAndRemove(List<OrderDetailPo> addOrderDetailPos, List<OrderDetailPo> updateOrderDetailPos, List<OrderDetailPo> removeOrderDetailPos) {
        orderDetailPoRepository.saveBatch(addOrderDetailPos, 200);
        orderDetailPoRepository.updateBatchById(updateOrderDetailPos, 200);
        if (!removeOrderDetailPos.isEmpty()) {
            orderDetailPoRepository.removeByIds(removeOrderDetailPos.stream().map(OrderDetailPo::getId).toList());
        }
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
