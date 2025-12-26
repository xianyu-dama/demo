package com.xianyu.order.context.order.infr.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.xianyu.order.context.order.app.dto.QueryOrderDto;
import com.xianyu.order.context.order.app.service.OrderQueryService;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.order.infr.convertor.OrderReadOnlyDtoConvertor;
import com.xianyu.order.context.order.infr.persistence.mapper.OrderPoMapper;
import com.xianyu.order.context.order.infr.persistence.po.OrderDetailPo;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import com.xianyu.order.context.order.infr.persistence.service.OrderDetailPoRepository;
import com.xianyu.order.context.order.infr.persistence.service.OrderPoRepository;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * 订单gateway实现<br/>
 * Created on : 2023-12-19 10:00
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class OrderQueryServiceAdapter implements OrderQueryService {

    private final OrderPoRepository orderPoRepository;
    private final OrderDetailPoRepository orderDetailPoRepository;
    private final OrderRepository orderRepository;
    private final OrderPoMapper orderPoMapper;

    @Override
    public Page<OrderReadOnlyDto> listForPage(List<Long> orderIds, String email, int pageIndex, int pageSize) {
        // 使用 OrderPoRepository 的分页查询
        QueryOrderDto queryDto = QueryOrderDto.builder()
                .orderIds(orderIds)
                .email(email)
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OrderPo> poPage = orderPoRepository.listForPage(queryDto);
        
        // 获取订单ID列表
        List<Long> queriedOrderIds = poPage.getRecords().stream()
                .map(OrderPo::getOrderId)
                .toList();
        
        // 批量查询订单明细
        List<OrderDetailPo> orderDetailPos = CollectionUtils.isEmpty(queriedOrderIds) 
                ? List.of() 
                : orderDetailPoRepository.listByOrderIds(Set.copyOf(queriedOrderIds));
        
        // 按订单ID分组订单明细
        Map<Long, List<OrderDetailPo>> orderDetailMap = StreamEx.of(orderDetailPos)
                .groupingBy(OrderDetailPo::getOrderId);
        
        // 转换为 DTO
        List<OrderReadOnlyDto> dtoList = poPage.getRecords().stream()
                .map(orderPo -> OrderReadOnlyDtoConvertor.toOrderReadOnlyDto(
                        orderPo, 
                        orderDetailMap.getOrDefault(orderPo.getOrderId(), List.of())))
                .toList();
        
        return new PageImpl<>(dtoList, 
                org.springframework.data.domain.PageRequest.of(pageIndex - 1, pageSize), 
                poPage.getTotal());
    }

    @Override
    public List<OrderReadOnlyDto> list(long... orderIds) {
        if (orderIds == null || orderIds.length == 0) {
            return List.of();
        }
        
        List<Long> orderIdList = Arrays.stream(orderIds).boxed().toList();
        
        // 使用 MyBatis-Plus 查询订单
        LambdaQueryWrapper<OrderPo> query = Wrappers.lambdaQuery(OrderPo.class);
        query.in(OrderPo::getOrderId, orderIdList);
        query.orderByAsc(OrderPo::getOrderId);
        List<OrderPo> orderPos = orderPoMapper.selectList(query);
        
        if (CollectionUtils.isEmpty(orderPos)) {
            return List.of();
        }
        
        // 批量查询订单明细
        List<OrderDetailPo> orderDetailPos = orderDetailPoRepository.listByOrderIds(
                Set.copyOf(orderIdList));
        
        // 按订单ID分组订单明细
        Map<Long, List<OrderDetailPo>> orderDetailMap = StreamEx.of(orderDetailPos)
                .groupingBy(OrderDetailPo::getOrderId);
        
        // 转换为 DTO
        return orderPos.stream()
                .map(orderPo -> OrderReadOnlyDtoConvertor.toOrderReadOnlyDto(
                        orderPo, 
                        orderDetailMap.getOrDefault(orderPo.getOrderId(), List.of())))
                .toList();
    }

    @Override
    public Page<OrderReadOnlyDto> listBy(long userId, int pageIndex, int pageSize) {
        // 使用 MyBatis-Plus 统计总数
        LambdaQueryWrapper<OrderPo> countQuery = Wrappers.lambdaQuery(OrderPo.class);
        countQuery.eq(OrderPo::getUserId, userId);
        long total = orderPoMapper.selectCount(countQuery);
        
        if (total == 0) {
            return new PageImpl<>(List.of(), 
                    org.springframework.data.domain.PageRequest.of(pageIndex - 1, pageSize), 
                    0);
        }
        
        // 使用 MyBatis-Plus 分页查询订单列表
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OrderPo> page = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<OrderPo> query = Wrappers.lambdaQuery(OrderPo.class);
        query.eq(OrderPo::getUserId, userId);
        query.orderByAsc(OrderPo::getOrderId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OrderPo> poPage = 
                orderPoMapper.selectPage(page, query);
        
        List<OrderPo> orderPos = poPage.getRecords();
        if (CollectionUtils.isEmpty(orderPos)) {
            return new PageImpl<>(List.of(), 
                    org.springframework.data.domain.PageRequest.of(pageIndex - 1, pageSize), 
                    total);
        }
        
        // 批量查询订单明细
        List<Long> orderIdList = orderPos.stream()
                .map(OrderPo::getOrderId)
                .toList();
        List<OrderDetailPo> orderDetailPos = orderDetailPoRepository.listByOrderIds(
                Set.copyOf(orderIdList));
        
        // 按订单ID分组订单明细
        Map<Long, List<OrderDetailPo>> orderDetailMap = StreamEx.of(orderDetailPos)
                .groupingBy(OrderDetailPo::getOrderId);
        
        // 转换为 DTO
        List<OrderReadOnlyDto> dtoList = orderPos.stream()
                .map(orderPo -> OrderReadOnlyDtoConvertor.toOrderReadOnlyDto(
                        orderPo, 
                        orderDetailMap.getOrDefault(orderPo.getOrderId(), List.of())))
                .toList();
        
        return new PageImpl<>(dtoList, 
                org.springframework.data.domain.PageRequest.of(pageIndex - 1, pageSize), 
                total);
    }

    @Override
    public Optional<OrderReadOnlyDto> getReadonly(long orderId) {
        // 查询订单PO
        OrderPo orderPo = orderPoMapper.selectById(orderId);
        if (orderPo == null) {
            return Optional.empty();
        }
        
        // 查询订单明细
        List<OrderDetailPo> orderDetailPos = orderDetailPoRepository.listByOrderId(orderId);
        
        // 转换为 DTO
        OrderReadOnlyDto dto = OrderReadOnlyDtoConvertor.toOrderReadOnlyDto(orderPo, orderDetailPos);
        return Optional.of(dto);
    }

    @Override
    public Optional<OrderReadOnlyDto> getReadonlyInCache(long orderId) {
        // 从缓存获取订单
        Optional<Order> orderOpt = orderRepository.getInCache(orderId);
        
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }
        
        // 转换为 DTO
        OrderReadOnlyDto dto = OrderReadOnlyDtoConvertor.toOrderReadOnlyDto(orderOpt.get());
        return Optional.of(dto);
    }

    @Override
    public int countByUserId(long userId) {
        LambdaQueryWrapper<OrderPo> query = Wrappers.lambdaQuery(OrderPo.class);
        query.eq(OrderPo::getUserId, userId);
        return Math.toIntExact(orderPoMapper.selectCount(query));
    }

    @Override
    public long count() {
        return orderPoMapper.selectCount(null);
    }

    @Override
    public List<Long> queryOrderIdsByUserId(Long userId) {
        LambdaQueryWrapper<OrderPo> query = Wrappers.lambdaQuery(OrderPo.class);
        query.eq(OrderPo::getUserId, userId);
        query.select(OrderPo::getOrderId);
        return orderPoMapper.selectList(query).stream()
                .map(OrderPo::getOrderId)
                .toList();
    }
}
