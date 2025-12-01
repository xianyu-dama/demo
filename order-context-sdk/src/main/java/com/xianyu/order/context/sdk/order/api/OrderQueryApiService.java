package com.xianyu.order.context.sdk.order.api;

import com.xianyu.order.context.sdk.order.dto.req.QueryOrderDto;
import com.xianyu.order.context.sdk.order.dto.rsp.OrderReadOnlyDto;
import org.springframework.data.domain.Page;

/**
 * 对外公开的订单查询服务（Published Language）
 */
public interface OrderQueryApiService {

    /**
     * 分页查询订单列表
     */
    Page<OrderReadOnlyDto> listForPage(QueryOrderDto queryDto);

}