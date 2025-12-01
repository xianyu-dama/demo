package com.xianyu.order.context.reference.product.rpc.client;

import com.xianyu.order.context.reference.product.rpc.req.ProductReq;
import com.xianyu.order.context.reference.product.rpc.rsp.ProductRsp;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 实际是rpc接口<br/>
 * Created on : 2024-04-21 12:12
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class ProductClient {

    public List<ProductRsp> getProduct(ProductReq request) {
        return Collections.emptyList();
    }

}
