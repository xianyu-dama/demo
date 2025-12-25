package com.xianyu.order.context.cart.app.service;

import com.xianyu.order.context.cart.domain.CartRepository;
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
public class RemoveItemSteps {
    @Resource
    private CartAppService cartAppService;
    @Resource
    private CartRepository cartRepository;

    private long userId;

    @When("用户 {long} 删除商品 {string}")
    public void 用户_删除商品(long userId, String itemId) {
        this.userId = userId;
        cartAppService.removeItem(userId, itemId);
    }

    @Then("购物车不包含商品 {string}")
    public void 购物车不包含商品(String itemId) {
        boolean contains = cartRepository.getOrThrow(userId).getItemCodes().contains(itemId);
        assertThat(contains).isFalse();
    }

}
