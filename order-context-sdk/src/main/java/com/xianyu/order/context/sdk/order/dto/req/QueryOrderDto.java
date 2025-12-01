package com.xianyu.order.context.sdk.order.dto.req;

import java.util.List;
import lombok.Builder;

/**
 * 订单列表查询入参（API）
 */
@Builder(toBuilder = true)
public record QueryOrderDto(
    List<Long> orderIds,
    String email,
    int pageIndex,
    int pageSize
) {
}