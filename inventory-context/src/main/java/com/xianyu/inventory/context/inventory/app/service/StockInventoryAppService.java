package com.xianyu.inventory.context.inventory.app.service;

import com.xianyu.inventory.context.inventory.app.assembler.StockInventoryAssembler;
import com.xianyu.inventory.context.inventory.app.dto.StockInventoryDto;
import com.xianyu.inventory.context.inventory.domain.repository.StockInventoryRepository;
import com.xianyu.inventory.context.sdk.inventory.api.StockInventoryApiService;
import com.xianyu.inventory.context.sdk.inventory.dto.LockStockDto;
import com.xianyu.inventory.context.sdk.inventory.dto.LockStockResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存应用服务<br/>
 * Created on : 2024-04-12
 * @author xian_yu_da_ma
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockInventoryAppService implements StockInventoryApiService {

    private final StockInventoryRepository stockInventoryRepository;
    private final StockInventoryAssembler stockInventoryAssembler;

    @Transactional(readOnly = true)
    public StockInventoryDto getStockInventory(Long skuId) {
        return stockInventoryRepository.get(skuId)
                .map(stockInventoryAssembler::toDTO)
                .orElse(null);
    }

    @Override
    public LockStockResultDto lockStock(LockStockDto lockStockDto) {
        log.info("lockStock {}", lockStockDto);
        return LockStockResultDto.builder()
                .locked(true)
                .stockLockId("1")
                .bizId(lockStockDto.getBizId())
                .build();
    }
}