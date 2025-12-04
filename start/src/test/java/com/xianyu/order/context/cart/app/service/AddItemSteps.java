package com.xianyu.order.context.cart.app.service;

import com.xianyu.order.context.cart.domain.CartRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

public class AddItemSteps {

    @Resource
    private CartAppService cartAppService;

    @Resource
    private CartRepository cartRepository;

    @Resource
    private CartContext cartContext;

    @Given("用户 {long} 的购物车为空" )
    public void 用户的购物车为空(long userId) {
        cartContext.userId = userId;
    }

    @When("添加商品 {string} 数量 {int}")
    public void 添加商品数量(String itemId, int quantity) {
        cartAppService.add(cartContext.userId, itemId, quantity);
    }

    @Then("购物车包含商品 {string} 数量 {int}")
    public void 购物车包含商品_数量(String string, Integer int1) {
        int quantity = cartRepository.getOrThrow(cartContext.userId).getItems().get(string).getQuantity();
        assertThat(quantity).isEqualTo(int1);
    }
}