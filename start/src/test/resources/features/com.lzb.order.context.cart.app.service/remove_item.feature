Feature: Cart - Remove Item
  Scenario: 用户删除购物车的商品
    Given 用户 1 的购物车已有商品 "PRODUCT-2" 数量 3
    When 用户 1 删除商品 "PRODUCT-2"
    Then 购物车不包含商品 "PRODUCT-2"
    And 购物车商品数为 0
