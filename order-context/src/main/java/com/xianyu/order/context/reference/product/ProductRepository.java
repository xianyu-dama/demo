package com.xianyu.order.context.reference.product;

import com.xianyu.order.context.reference.product.rpc.client.ProductClient;
import com.xianyu.order.context.reference.product.rpc.req.ProductReq;
import com.xianyu.order.context.reference.product.rpc.rsp.ProductRsp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("order.productRepositoryAdapter")
@RequiredArgsConstructor
public class ProductRepository {

    private final ProductClient productClient;

    public List<Product> productStocks(List<Integer> productIds) {
        List<ProductRsp> products = productClient.getProduct(ProductReq.builder().productIds(productIds).build());
        return products.stream().map(p -> Product.builder()
                .productId(p.getProductId())
                .onSaleFlag(p.isOnSale())
                .availableNum(p.getStock())
                .build()).toList();
    }

    public List<Product> list(List<Integer> productIds) {
        return productIds.stream()
                .map(productId -> Product.builder()
                        .productId(productId)
                        .name("name-" + productId)
                        .picUrl("picUrl-" + productId)
                        .build())
                .toList();
    }
}
