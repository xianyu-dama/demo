package com.xianyu.order.context.reference.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Product {

    private Integer productId;
    private Integer availableNum;
    private Boolean onSaleFlag;
    private String name;
    private String picUrl;

}
