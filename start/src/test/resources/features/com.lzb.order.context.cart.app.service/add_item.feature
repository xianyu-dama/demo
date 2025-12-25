Feature: Cart - Add Item
  Scenario: 用户添加商品到购物车
    Given 用户 1 的购物车为空
    When 添加商品 "PRODUCT-1" 数量 2
    Then 购物车包含商品 "PRODUCT-1" 数量 2
