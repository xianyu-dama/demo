package com.xianyu.order.context.order.domain.service.validator;

import com.xianyu.component.exception.BizException;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.reference.product.Product;
import com.xianyu.order.context.reference.product.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductOnSaleValidator extends OrderValidator {

    private final ProductRepository productRepository;

    @Override
    public void validate(Order order) {
        List<Product> products = productRepository.productStocks(order.getProductIds());
        boolean allOnSale = products.stream().allMatch(Product::getOnSaleFlag);
        if (!allOnSale) {
            throw new BizException("订单明细包含下架商品，无法创建订单");
        }
    }

}
