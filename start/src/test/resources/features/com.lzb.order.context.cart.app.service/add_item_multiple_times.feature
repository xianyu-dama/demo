Feature: Cart - Add Item Multiple Times
  Scenario: 用户多次添加商品到购物车
    Given 用户 1 的购物车为空
    When 添加商品 "SKU-1" 数量 2
    When 添加商品 "SKU-1" 数量 2
    When 添加商品 "SKU-2" 数量 2
    Then 用户 1 购物车包含商品 "SKU-1" 数量 4，商品 "SKU-2" 数量 2