package com.xianyu.order.context.cart.app.service;

import com.xianyu.order.context.cart.domain.Cart;
import com.xianyu.order.context.cart.domain.CartRepository;
import io.cucumber.java.en.Then;
import jakarta.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

public class AddItemMultipleTimesSteps {

    @Resource
    private CartRepository cartRepository;

    @Then("用户 {long} 购物车包含商品 {string} 数量 {int}，商品 {string} 数量 {int}")
    public void 用户_购物车包含商品_数量_商品_数量(long userId, String string, Integer int1, String string2, Integer int2) {
        Cart cart = cartRepository.getOrThrow(userId);
        int quantity1 = cart.getQuantity(string);
        int quantity2 = cart.getQuantity(string2);
        assertThat(quantity1).isEqualTo(int1);
        assertThat(quantity2).isEqualTo(int2);
    }

}