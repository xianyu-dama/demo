package com.xianyu.order.context.order.infr.adapter;

import com.xianyu.inventory.context.sdk.inventory.api.StockInventoryApiService;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.order.domain.service.StockLockService;
import com.xianyu.order.context.order.infr.convertor.OrderConvertor;
import com.xianyu.order.context.reference.inventory.ProductStockLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * <br/>
 * Created on : 2023-12-08 19:34
 * @author xian_yu_da_ma
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class StockLockServiceAdapter implements StockLockService {

    private final StockInventoryApiService stockInventoryApiService;

    private final OrderConvertor orderConvertor;

    @Override
    public ProductStockLock lockStock(Order order) {
        // 转成对应的rpc或者内部请求
        var lockStockResult = stockInventoryApiService.lockStock(orderConvertor.toLockStockRequest(order));
        return ProductStockLock.builder()
                .locked(lockStockResult.getLocked())
                .stockLockId(lockStockResult.getStockLockId())
                .build();
    }

}
