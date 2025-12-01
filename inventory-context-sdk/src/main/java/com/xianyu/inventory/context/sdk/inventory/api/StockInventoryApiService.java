package com.xianyu.inventory.context.sdk.inventory.api;

import com.xianyu.inventory.context.sdk.inventory.dto.LockStockDto;
import com.xianyu.inventory.context.sdk.inventory.dto.LockStockResultDto;

/**
 * <br/>
 * Created on : 2025-11-18 09:27
 *
 * @author xian_yu_da_ma
 */
public interface StockInventoryApiService {

    LockStockResultDto lockStock(LockStockDto lockStockDto);

}
