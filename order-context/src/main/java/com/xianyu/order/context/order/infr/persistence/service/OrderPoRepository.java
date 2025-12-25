package com.xianyu.order.context.order.infr.persistence.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.xianyu.order.context.order.app.dto.QueryOrderDto;
import com.xianyu.order.context.order.infr.persistence.mapper.OrderPoMapper;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import io.micrometer.common.util.StringUtils;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <br/>
 * Created on : 2024-10-29 22:36
 *
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class OrderPoRepository extends CrudRepository<OrderPoMapper, OrderPo> {

    private final OrderPoMapper orderPoMapper;

    public Page<OrderPo> listForPage(QueryOrderDto queryDto) {
        LambdaQueryWrapper<OrderPo> query = Wrappers.lambdaQuery(OrderPo.class);
        query.in(CollectionUtils.isNotEmpty(queryDto.orderIds()), OrderPo::getOrderId, queryDto.orderIds());
        query.likeRight(StringUtils.isNotBlank(queryDto.email()), OrderPo::getEmail, queryDto.email());
        return page(new Page<>(queryDto.pageIndex(), queryDto.pageSize()), query);
    }

    public Set<Long> listOrderIdsByUserId(long userId, int pageIndex, int pageSize) {
        LambdaQueryWrapper<OrderPo> query = Wrappers.lambdaQuery(OrderPo.class);
        query.eq(OrderPo::getUserId, userId);
        query.select(OrderPo::getOrderId);
        return page(new Page<>(pageIndex, pageSize, false), query)
                .getRecords()
                .stream()
                .map(OrderPo::getOrderId)
                .collect(Collectors.toSet());
    }

    public Optional<OrderPo> getForUpdateById(long id) {
        return Optional.ofNullable(orderPoMapper.getForUpdateById(id));
    }

}
