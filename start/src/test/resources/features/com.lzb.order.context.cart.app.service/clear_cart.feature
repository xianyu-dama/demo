Feature: Cart - Clear Cart
  Scenario: 用户清空购物车
    Given 用户 1 的购物车已有商品 "PRODUCT-3" 数量 1
    And 购物车已有商品 "PRODUCT-4" 数量 2
    When 清空购物车
    Then 购物车商品数为 0
    And 购物车为空
