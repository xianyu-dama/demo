package com.xianyu.order.context.cart.app.service;

import com.xianyu.order.context.cart.domain.CartRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <br/>
 * Created on : 2025-11-15 13:06
 *
 * @author xian_yu_da_ma
 */
public class ClearCartSteps {

    @Resource
    private CartAppService cartAppService;

    @Resource
    private CartRepository cartRepository;

    private long userId;

    @Given("用户 {long} 的购物车已有商品 {string} 数量 {int}")
    public void 用户_的购物车已有商品_数量(Long userId, String string, Integer int2) {
        this.userId = userId;
        cartAppService.add(userId, string, int2);
    }

    @Given("购物车已有商品 {string} 数量 {int}")
    public void 购物车已有商品_数量(String string, Integer int1) {
        cartAppService.add(userId, string, int1);
    }

    @When("清空购物车")
    public void 清空购物车() {
        cartAppService.clear(userId);
    }

    @Then("购物车商品数为 {int}")
    public void 购物车商品数为(Integer int1) {
        int size = cartRepository.getOrThrow(userId).getItemSize();
        assertThat(size).isEqualTo(int1);
    }

    @Then("购物车为空")
    public void 购物车为空() {
        boolean empty = cartRepository.getOrThrow(userId).isEmpty();
        assertThat(empty).isTrue();
    }


}
