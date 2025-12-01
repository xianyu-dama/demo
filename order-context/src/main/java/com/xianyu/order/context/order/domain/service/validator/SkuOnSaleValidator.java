package com.xianyu.order.context.order.domain.service.validator;

import com.xianyu.component.exception.BizException;
import com.xianyu.order.context.order.domain.Order;
import com.xianyu.order.context.reference.product.Sku;
import com.xianyu.order.context.reference.product.SkuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * sku校验<br/>
 * Created on : 2023-09-09 20:15
 * @author xian_yu_da_ma
 */
@Component
@RequiredArgsConstructor
public class SkuOnSaleValidator extends OrderValidator {

    private final SkuRepository skuRepository;

    @Override
    public void validate(Order order) {
        List<Sku> skus = skuRepository.skuStocks(order.getProductIds());
        boolean allOnSale = skus.stream().allMatch(Sku::getOnSaleFlag);
        if (!allOnSale) {
            throw new BizException("订单明细包含下架商品，无法创建订单");
        }
    }

}
