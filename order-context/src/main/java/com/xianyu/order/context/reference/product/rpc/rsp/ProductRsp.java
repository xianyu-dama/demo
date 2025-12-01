package com.xianyu.order.context.reference.product.rpc.rsp;

import lombok.Data;

/**
 * 外部返回对象<br/>
 * Created on : 2025-05-09 20:19
 * @author xian_yu_da_ma
 */
@Data
public class ProductRsp {

    private int skuId;
    private String name;
    private boolean onSale;
    private int stock;
}
