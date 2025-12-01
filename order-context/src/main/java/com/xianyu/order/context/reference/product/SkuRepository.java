package com.xianyu.order.context.reference.product;

import com.xianyu.order.context.reference.product.rpc.client.ProductClient;
import com.xianyu.order.context.reference.product.rpc.req.ProductReq;
import com.xianyu.order.context.reference.product.rpc.rsp.ProductRsp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <br/>
 * Created on : 2024-06-04 10:09
 *
 * @author xian_yu_da_ma
 */
@Service("order.skuRepositoryAdapter")
@RequiredArgsConstructor
public class SkuRepository {

    private final ProductClient productClient;

    public List<Sku> skuStocks(List<Integer> skuIds) {
        List<ProductRsp> products = productClient.getProduct(ProductReq.builder().skuIds(skuIds).build());
        return products.stream().map(p -> {
            return Sku.builder()
                .skuId(p.getSkuId())
                .onSaleFlag(p.isOnSale())
                .availableNum(p.getStock())
                .build();
        }).toList();
    }

    public List<Sku> list(List<Integer> skuIds) {
        return skuIds.stream().map(skuId -> Sku.builder().skuId(skuId).name("name-" + skuId).picUrl("picUrl-" + skuId).build()).toList();
    }
}
