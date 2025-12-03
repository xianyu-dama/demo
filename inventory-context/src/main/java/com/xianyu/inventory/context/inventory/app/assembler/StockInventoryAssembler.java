package com.xianyu.inventory.context.inventory.app.assembler;

import com.xianyu.inventory.context.inventory.app.dto.StockInventoryDto;
import com.xianyu.inventory.context.inventory.domain.aggregation.StockInventory;
import org.springframework.stereotype.Component;

/**
 * 库存DTO转换器<br/>
 * Created on : 2024-04-12
 * @author xian_yu_da_ma
 */
@Component
public class StockInventoryAssembler {

    public StockInventoryDto toDTO(StockInventory stockInventory) {
        if (stockInventory == null) {
            return null;
        }
        StockInventoryDto dto = new StockInventoryDto();
        dto.setId(stockInventory.getId());
        dto.setProductId(stockInventory.getProductId());
        dto.setFreeNum(stockInventory.getFreeNum());
        dto.setLockNum(stockInventory.getLockNum());
        return dto;
    }

    public StockInventory toDomain(StockInventoryDto dto) {
        if (dto == null) {
            return null;
        }
        return StockInventory.builder()
                .id(dto.getId())
                .productId(dto.getProductId())
                .freeNum(dto.getFreeNum())
                .lockNum(dto.getLockNum())
                .build();
    }
} 
