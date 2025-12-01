package com.xianyu.order.context.order.domain;

import com.xianyu.order.context.order.domain.service.StockLockService;
import com.xianyu.order.context.reference.inventory.SkuStockLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <br/>
 * Created on : 2024-04-21 10:01
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final StockLockService stockLockService;

    /**
     * 锁定库存
     * 这个动作不能声明成接口，最终的锁库存结果，需要更新到订单上，这一步算业务逻辑
     * @param order
     */
    void lockStock(Order order) {
        SkuStockLock skuStockLock = stockLockService.lockStock(order);
        order.lockStock(skuStockLock);
    }

    /**
     * 生单
     *
     * @param order
     */
    public void place(Order order) {

        order.place();

        // 锁定库存
        lockStock(order);

    }

}
