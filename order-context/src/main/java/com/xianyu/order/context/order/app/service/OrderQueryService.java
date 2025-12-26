package com.xianyu.order.context.order.app.service;

import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface OrderQueryService {

    Page<OrderReadOnlyDto> listForPage(List<Long> orderIds, String email, int pageIndex, int pageSize);

    List<OrderReadOnlyDto> list(long... orderIds);

    Page<OrderReadOnlyDto> listBy(long userId, int pageIndex, int pageSize);

    Optional<OrderReadOnlyDto> getReadonly(long orderId);

    Optional<OrderReadOnlyDto> getReadonlyInCache(long orderId);

    int countByUserId(long userId);

    long count();

    List<Long> queryOrderIdsByUserId(Long userId);
}