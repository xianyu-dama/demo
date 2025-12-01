package com.xianyu.order.context.order.infr.adapter;

import com.xianyu.component.ddd.aop.annotation.Snapshot;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.infr.convertor.OrderConvertor;
import com.xianyu.order.context.order.infr.convertor.OrderPoConvertor;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.order.infr.persistence.repository.OrderItemPoRepository;
import com.xianyu.order.context.order.infr.persistence.repository.OrderPoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import static com.xianyu.order.context.order.infr.persistence.repository.OrderPoRepository.FETCHER;

/**
 * <br/>
 * Created on : 2023-08-30 23:08
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"order"})
public class OrderRepositoryAdapter implements OrderRepository {

    private final JSqlClient jSqlClient;
    private final OrderPoRepository orderPoRepository;
    private final OrderItemPoRepository orderItemPoRepository;
    private final OrderConvertor orderConvertor;

    @Override
    public Long add(@NonNull Order aggregate) {
        OrderPo orderPo = OrderPoConvertor.toOrderPo(aggregate);
        return jSqlClient.saveCommand(orderPo)
            .setMode(SaveMode.INSERT_ONLY)
            .setAssociatedModeAll(AssociatedSaveMode.APPEND)
            .execute()
            .getModifiedEntity()
            .orderId();
    }

    @CacheEvict(key = "#order.id")
    public int update(@NonNull Order order) {
        OrderPo updateOrderPo = OrderPoConvertor.toUpdateOrderPo(order);
        return jSqlClient.saveCommand(updateOrderPo)
            .setMode(SaveMode.UPDATE_ONLY)
            // 看你的场景需要update、merge、replace
            .setAssociatedModeAll(AssociatedSaveMode.UPDATE)
            .execute()
            .getTotalAffectedRowCount();
    }

    @Override
    public Optional<Order> get(Long id) {
        return orderPoRepository.findById(id, FETCHER).map(orderConvertor::toOrder);
    }

    /**
     * 不缓存为null的值
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(key = "#id", unless = "#result == null")
    public Optional<Order> getInCache(@NonNull Long id) {
        return get(id);
    }

    @Override
    @Snapshot
    public Order getWithLockOrThrow(Long id) {
        return jSqlClient.createQuery(OrderPoRepository.ORDER_PO_TABLE)
            .where(OrderPoRepository.ORDER_PO_TABLE.orderId().eq(id))
            .select(OrderPoRepository.ORDER_AGGREGATION_SELECTION)
            .forUpdate() // 添加 FOR UPDATE 子句，实现行级锁
            .fetchOptional()
            .map(orderConvertor::toOrder)
            .orElseThrow();
    }
}
