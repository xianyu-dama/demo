package com.xianyu.order.context.cart.app.service;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class CartContext {
    public Long userId;
}