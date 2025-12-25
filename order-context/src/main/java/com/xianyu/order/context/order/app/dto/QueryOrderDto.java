package com.xianyu.order.context.order.app.dto;

import java.util.List;
import lombok.Builder;

/**
 * 订单列表查询入参
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Builder(toBuilder = true)
public record QueryOrderDto(
    List<Long> orderIds,
    String email,
    int pageIndex,
    int pageSize
) {
}
