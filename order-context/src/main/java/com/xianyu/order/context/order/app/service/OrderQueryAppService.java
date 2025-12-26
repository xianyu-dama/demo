package com.xianyu.order.context.order.app.service;

import com.xianyu.order.context.order.app.view.OrderDetailView;
import com.xianyu.order.context.order.app.view.OrderDetailViewContext;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.repository.OrderRepository;
import com.xianyu.order.context.reference.product.ProductRepository;
import com.xianyu.order.context.sdk.order.api.OrderQueryApiService;
import com.xianyu.order.context.sdk.order.dto.req.QueryOrderDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryAppService implements OrderQueryApiService {

    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;
    private final ProductRepository productRepository;

    @Override
    public Page<OrderReadOnlyDto> listForPage(QueryOrderDto queryDto) {
        return orderQueryService.listForPage(queryDto.orderIds(), queryDto.email(), queryDto.pageIndex(), queryDto.pageSize());
    }

    @Override
    public List<Long> queryOrderIdsByUserId(Long id) {
        return orderQueryService.queryOrderIdsByUserId(id);
    }

    public OrderDetailView detail(long orderId) {
        Order order = orderRepository.getInCache(orderId).orElseThrow();
        OrderDetailViewContext context = OrderDetailViewContext.builder()
                .product(() -> productRepository.list(order.getOrderItems().getProductIds()))
                .build();
        return OrderDetailView.builder().order(order).orderDetailViewContext(context).build();
    }

    public List<OrderReadOnlyDto> list(long... orderIds) {
        return orderQueryService.list(orderIds);
    }

    public Optional<OrderReadOnlyDto> getReadonly(long orderId) {
        return orderQueryService.getReadonly(orderId);
    }

    public Optional<OrderReadOnlyDto> getReadonlyInCache(long orderId) {
        return orderQueryService.getReadonlyInCache(orderId);
    }

    public Page<OrderReadOnlyDto> listBy(long userId, int pageIndex, int pageSize) {
        return orderQueryService.listBy(userId, pageIndex, pageSize);
    }

    public int count(long userId) {
        return orderQueryService.countByUserId(userId);
    }

    public long count() {
        return orderQueryService.count();
    }
}
