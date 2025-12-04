package com.xianyu.inventory.context.inventory.adapter.eventhandler;

import com.xianyu.inventory.context.inventory.app.service.StockInventoryAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 库存事件处理器<br/>
 * Created on : 2024-04-12
 * @author xian_yu_da_ma
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockInventoryEventHandler {

    private final StockInventoryAppService stockInventoryAppService;

    // TODO: 添加事件处理方法
    // 例如：处理订单创建事件，锁定库存
    // 例如：处理订单取消事件，释放库存
} 