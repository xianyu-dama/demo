package com.xianyu.inventory.context.inventory.adapter.web;

import com.xianyu.inventory.context.inventory.app.dto.StockInventoryDto;
import com.xianyu.inventory.context.inventory.app.service.StockInventoryAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存Web接口<br/>
 * Created on : 2024-04-12
 * @author xian_yu_da_ma
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class StockInventoryController {

    private final StockInventoryAppService stockInventoryAppService;

    @GetMapping("/{skuId}")
    public StockInventoryDto getStockInventory(@PathVariable Long skuId) {
        return stockInventoryAppService.getStockInventory(skuId);
    }

}