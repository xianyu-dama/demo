package com.xianyu.inventory.context.sdk.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 库存锁定结果<br/>
 * Created on : 2025-11-18 09:27
 *
 * @author xian_yu_da_ma
 */
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
@Getter
public class LockStockResultDto {

    /**
     * 库存锁定id
     */
    private String stockLockId;

     /**
      * 是否锁定成功
      */
    private Boolean locked;

     /**
      * 业务id 等价于 idempotentId
      */
    private String bizId;

}
