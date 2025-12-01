package com.xianyu.order.context.order.domain.service;

import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.reference.inventory.SkuStockLock;


/**
 * 库存锁定<br/>
 * Created on : 2024-01-05 13:50
 * @author xian_yu_da_ma
 */
public interface StockLockService {

    /**
     * 锁定库存
     *
     * @param order
     * @return
     */
    SkuStockLock lockStock(Order order);

}
