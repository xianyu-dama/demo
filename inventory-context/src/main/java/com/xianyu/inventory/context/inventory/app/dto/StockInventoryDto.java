package com.xianyu.inventory.context.inventory.app.dto;

import lombok.Data;

/**
 * 库存DTO<br/>
 * Created on : 2024-04-12
 * @author xian_yu_da_ma
 */
@Data
public class StockInventoryDto {
    
    private Long id;
    
    /**
     * 商品id
     */
    private Long productId;
    
    /**
     * 库存数量
     */
    private Integer freeNum;
    
    /**
     * 锁定数量
     */
    private Integer lockNum;
} 
