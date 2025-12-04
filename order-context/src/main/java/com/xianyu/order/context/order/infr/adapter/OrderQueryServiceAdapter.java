package com.xianyu.order.context.order.infr.adapter;

import com.xianyu.order.context.order.app.service.OrderQueryService;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.infr.convertor.OrderConvertor;
import com.xianyu.order.context.order.infr.convertor.OrderReadOnlyConvertor;
import com.xianyu.order.context.order.infr.persistence.repository.OrderItemPoRepository;
import com.xianyu.order.context.order.infr.persistence.repository.OrderPoRepository;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import one.util.streamex.LongStreamEx;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import static com.xianyu.order.context.order.infr.persistence.repository.OrderPoRepository.ORDER_AGGREGATION_SELECTION;
import static com.xianyu.order.context.order.infr.persistence.repository.OrderPoRepository.ORDER_PO_TABLE;

@Repository
@RequiredArgsConstructor
public class OrderQueryServiceAdapter implements OrderQueryService {

    private final JSqlClient jSqlClient;
    private final OrderPoRepository orderPoRepository;
    private final OrderItemPoRepository orderItemPoRepository;

    private final OrderConvertor orderConvertor;
    private final OrderReadOnlyConvertor orderReadOnlyConvertor;
    private final OrderRepository orderRepository;

    @Override
    public Page<OrderReadOnlyDto> listForPage(List<Long> orderIds, String email, int pageIndex, int pageSize) {
        return jSqlClient.createQuery(ORDER_PO_TABLE)
                .whereIf(CollectionUtils.isNotEmpty(orderIds), () -> ORDER_PO_TABLE.orderId().in(orderIds))
                .whereIf(StringUtils.isNotEmpty(email), () -> ORDER_PO_TABLE.email().ilike(email + "%"))
                .select(ORDER_AGGREGATION_SELECTION)
                .fetchPage(pageIndex - 1, pageSize, SpringPageFactory.getInstance())
                .map(orderConvertor::toOrder)
                .map(orderReadOnlyConvertor::toDto);
    }

    @Override
    public List<OrderReadOnlyDto> list(long... orderIds) {
        if (ArrayUtils.isEmpty(orderIds)) {
            return Collections.emptyList();
        }
        return jSqlClient.createQuery(ORDER_PO_TABLE)
                .where(ORDER_PO_TABLE.orderId().in(LongStreamEx.of(orderIds).boxed().toList()))
                .select(ORDER_AGGREGATION_SELECTION)
                .map(orderConvertor::toOrder)
                .stream()
                .map(orderReadOnlyConvertor::toDto)
                .toList();
    }

    @Override
    public Page<OrderReadOnlyDto> listBy(long userId, int pageIndex, int pageSize) {
        return jSqlClient.createQuery(ORDER_PO_TABLE)
                .where(ORDER_PO_TABLE.userId().eq(userId))
                .select(ORDER_AGGREGATION_SELECTION)
                .fetchPage(pageIndex - 1, pageSize, SpringPageFactory.getInstance())
                .map(orderConvertor::toOrder)
                .map(orderReadOnlyConvertor::toDto);
    }

    @Override
    public Optional<OrderReadOnlyDto> getReadonly(long orderId) {
        return orderRepository.get(orderId).map(orderReadOnlyConvertor::toDto);
    }

    @Override
    public Optional<OrderReadOnlyDto> getReadonlyInCache(long orderId) {
        return orderRepository.getInCache(orderId).map(orderReadOnlyConvertor::toDto);
    }

    @Override
    public int countByUserId(long userId) {
        Long value = jSqlClient.createQuery(ORDER_PO_TABLE)
                .where(ORDER_PO_TABLE.userId().eq(userId))
                .select(ORDER_PO_TABLE.count())
                .fetchOne();
        return Math.toIntExact(value);
    }

    @Override
    public long count() {
        return jSqlClient.createQuery(ORDER_PO_TABLE)
                .select(ORDER_PO_TABLE.count())
                .fetchOne();
    }
}
