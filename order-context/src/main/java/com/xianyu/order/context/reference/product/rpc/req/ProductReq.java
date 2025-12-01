package com.xianyu.order.context.reference.product.rpc.req;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * <br/>
 * Created on : 2025-05-09 20:24
 * @author xian_yu_da_ma
 */
@Data
@Builder
public class ProductReq {

    private List<Integer> skuIds;

}
