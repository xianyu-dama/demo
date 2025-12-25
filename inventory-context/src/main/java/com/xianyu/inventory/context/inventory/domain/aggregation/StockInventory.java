package com.xianyu.inventory.context.inventory.domain.aggregation;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * <br/>
 * Created on : 2024-04-11 22:58
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Getter
@SuperBuilder(toBuilder = true)
public class StockInventory extends BaseAggregation<StockInventory, Long> {

    private long id;

    /**
     * 商品id
     */
    private long productId;

    /**
     * 库存数量
     */
    private int freeNum;

    /**
     * 锁定数量
     */
    private int lockNum;

    @Override
    public Long id() {
        return id;
    }

    public boolean lock(long productId, int count) {
        log.info("lock stock: productId={}, count={}", productId, count);
        return true;
    }
} 
