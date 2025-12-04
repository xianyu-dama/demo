# JsonPathAssertions 增强功能

## ✨ 概述

* `JsonPathAssertions` 类现在支持以下增强功能，适用于 API 自动化测试、JSON 响应验证等场景
* 灵感来自于契约测试`Spring Cloud Contract`，通过定义yml对response断言

---

## 📦 依赖配置

在 `pom.xml` 中添加以下依赖（或使用等效 Gradle 配置）：

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
</dependency>
<dependency>
    <groupId>net.javacrumbs.json-unit</groupId>
    <artifactId>json-unit-assertj</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

---

## 功能说明

### 基本语法

```jsonc
{
  "jsonPath表达式": {
    # 这是注释
    "type": "断言类型",
    // 这是注释
    "value": "期望值",
    "options": ["可选配置"]
  }
}
```

---

## ✅ 支持的断言类型一览

| 断言类型（忽略大小写）          | 含义                                                        |
|----------------------|-----------------------------------------------------------|
| `by_equality`        | 值等于预期值（字符串、数值、布尔等）                                        |
| `array_contains`     | 断言数组中包含指定值                                                |
| `array_length`       | 断言数组长度                                                    |
| `by_json_compare`    | JSON结构比较，支持`options`（net.javacrumbs.jsonunit.core.Option） |
| `by_regex`           | 使用正则表达式匹配                                                 |
| `by_decimal_compare` | 精度敏感的数值比较                                                 |
| `by_null`            | 断言字段为 null; value = `true or false`                       |
| `absent`             | 断言字段不存在; value = `true or false`                          |

---

## 完整示例

```json
{
  "id": 12345,
  "email": "test@example.com",
  "username": "testuser",
  "address": {
    "city": "Beijing",
    "postalCode": "100000",
    "street": "Unknown"  // 额外字段被options忽略
  },
  "balance": 99.990,     // 小数比较
  "middleName": null,    // null检查
  "roles": ["user", "admin"],
  "permissions": [1, 2, 3, 4, 5]
}
```

```jsonc
{
  // 1. 类型匹配
  "$.id": {
    "type": "BY_TYPE",
    "value": "number"
  },

  // 2. 正则匹配
  "$.email": {
    "type": "BY_REGEX",
    "value": "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
  },

  // 3. 值相等匹配
  "$.username": {
    "type": "BY_EQUALITY",
    "value": "testuser"
  },

  // 4. JSON对象比较
  "$.address": {
    "type": "BY_JSON_COMPARE",
    "value": {
      "city": "Beijing",
      "postalCode": "100000"
    },
    # 枚举:net.javacrumbs.jsonunit.core.Option
    "options": ["IGNORING_EXTRA_FIELDS"]
  },

  // 5. 数值比较
  "$.balance": {
    "type": "BY_DECIMAL_COMPARE",
    "value": 99.99
  },

  // 6. null检查
  "$.middleName": {
    "type": "BY_NULL",
    "value": true
  },

  // 7. 字段不存在检查
  "$.deletedField": {
    "type": "ABSENT"
  },

  // 8. 数组包含检查
  "$.roles": {
    "type": "ARRAY_CONTAINS",
    "value": "admin"
  },

  // 9. 数组长度检查
  "$.permissions": {
    "type": "ARRAY_LENGTH",
    "value": 5
  }
}
```

---

## 使用方式

```java

@Test
void testAllAssertionTypes() throws JsonProcessingException {
    String responseJson = """ 
        {
          "email": "test@example.com",
          "username": "testuser",
          "address": {
            "city": "Beijing",
            "postalCode": "100000",
            "street": "Unknown"
          },
          "balance": 99.990,
          "middleName": null,
          "roles": ["user", "admin"],
          "permissions": [1, 2, 3, 4, 5]
        }
        """;

    String assertions = """
        {
          # 判断email是否符合正则
          "$.email": { "type": "BY_REGEX", "value": "^[\\\\w-\\\\.]+@([\\\\w-]+\\\\.)+[\\\\w-]{2,4}$" },
        
          # username 是否等于 testuser 
          "$.username": { "type": "BY_EQUALITY", "value": "testuser" },
        
          # 地址通过json比较，只匹配city/postalCode字段
          "$.address": { 
            "type": "BY_JSON_COMPARE", 
            "value": { "city": "Beijing", "postalCode": "100000" },
            "options": ["IGNORING_EXTRA_FIELDS"]
          },
        
          # decimal 比较
          "$.balance": { "type": "BY_DECIMAL_COMPARE", "value": 99.99 },
        
          # json 可以有null值
          "$.middleName": { "type": "BY_NULL", "value": true },
        
          # 字段是否缺失
          "$.deletedField": { "type": "ABSENT", "value": true },
        
          # 数组是否包含
          "$.roles": { "type": "ARRAY_CONTAINS", "value": "admin" },
        
          # 数组长度
          "$.permissions": { "type": "ARRAY_LENGTH", "value": 5 }
        }
        """;

    JsonPathAssertions.assertJsonPathValues(responseJson, assertions);
}
```