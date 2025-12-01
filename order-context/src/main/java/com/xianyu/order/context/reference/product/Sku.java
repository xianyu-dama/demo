package com.xianyu.order.context.reference.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * <br/>
 * Created on : 2024-06-04 10:04
 * @author xian_yu_da_ma
 */
@Getter
@Builder
@AllArgsConstructor
public class Sku {

    private Integer skuId;
    private Integer availableNum;
    private Boolean onSaleFlag;
    private String name;
    private String picUrl;

}
