package com.xianyu.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.jsonunit.assertj.JsonAssert;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;

/**
 * 通过jsonPath定位元素，值定义是断言
 * 详见：JsonPathAssertionsTest
 */
@Slf4j
public class JsonPathAssertions {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String TYPE_KEY = "type";
    private static final String VALUE_KEY = "value";
    private static final String OPTIONS_KEY = "options";

    public static void assertJsonPathValues(String json, String jsonPath2Values) throws JsonProcessingException {
        DocumentContext ctx = JsonPath.parse(json);

        String jsonPath2ValuesNoComment = removeComments(jsonPath2Values);

        List<JsonPathAssertion> assertions = toJsonPathAssertions(jsonPath2ValuesNoComment);

        for (JsonPathAssertion assertion : assertions) {
            assertion.doAssert(ctx);
        }
    }

    /**
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    private static List<JsonPathAssertion> toJsonPathAssertions(String json) throws JsonProcessingException {

        Map<String, Map<String, Object>> jsonPath2Assertions = mapper.readValue(json, Map.class);

        return jsonPath2Assertions.entrySet()
            .stream()
            .map(entry -> {
                String jsonPath = entry.getKey();
                Map<String, Object> ruleMap = entry.getValue();
                Type type = Type.fromString(Objects.requireNonNull((String) ruleMap.get(TYPE_KEY), "type类型不能为空"));
                Object expectedValue = Objects.requireNonNull(ruleMap.get(VALUE_KEY), "value值不能为空");

                String[] options = null;
                Object optionObj = ruleMap.get(OPTIONS_KEY);
                if (optionObj instanceof Iterable<?> list) {
                    options = StreamSupport.stream(list.spliterator(), false)
                        .map(Object::toString)
                        .toArray(String[]::new);
                }

                Assert.notNull(jsonPath, "jsonPath is null");
                Assert.notNull(type, StrUtil.format("`{}` type is null", jsonPath));
                Assert.notNull(expectedValue, StrUtil.format("`{}` expectedValue is null", jsonPath));
                return new JsonPathAssertion(jsonPath, type, expectedValue, Objects.requireNonNullElseGet(options, () -> new String[0]));
            }).toList();
    }

    private static void assertByJsonCompare(String jsonPath, Object actualValue,
        Object expectedValue,
        String[] options) throws JsonProcessingException {
        String actualJson = mapper.writeValueAsString(actualValue);
        String expectedJson = mapper.writeValueAsString(expectedValue);

        JsonAssert.ConfigurableJsonAssert jsonAssert = JsonAssertions.assertThatJson(actualJson);

        if (options != null) {
            for (String optStr : options) {
                try {
                    jsonAssert = jsonAssert.when(Option.valueOf(optStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn(StrUtil.format("`{}` Unknown option: {}", jsonPath, optStr));
                }
            }
        }

        jsonAssert.isEqualTo(expectedJson);
    }

    /*
    {
        "jsonPath": {
            "type": "",
            "value": "",
            "options": ""
        }
    }
    */

    private static void assertDecimalCompare(String jsonPath, Object actualValue, Object expectedValue) {
        BigDecimal expected = new BigDecimal(expectedValue.toString());
        BigDecimal actual;
        if (actualValue instanceof BigDecimal bd)
            actual = bd;
        else if (actualValue instanceof Long l)
            actual = BigDecimal.valueOf(l);
        else if (actualValue instanceof Double d)
            actual = BigDecimal.valueOf(d);
        else
            actual = new BigDecimal(actualValue.toString());

        Assertions.assertThat(actual)
            .as("Decimal comparison failed for path: " + jsonPath)
            .isEqualByComparingTo(expected);
    }

    private static String removeComments(String json) {
        StringBuilder result = new StringBuilder();
        for (String line : json.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("#") && !trimmed.startsWith("//")) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    enum Type {
        /**
         * 按类型匹配
         */
        BY_TYPE,
        /**
         * 按正则表达式匹配
         */
        BY_REGEX,
        /**
         * 按值相等匹配
         */
        BY_EQUALITY,
        /**
         * 按 JSON 比较匹配
         */
        BY_JSON_COMPARE,
        /**
         * 按数值比较匹配
         */
        BY_DECIMAL_COMPARE,
        /**
         * 检查是否为 null
         */
        BY_NULL,
        /**
         * 检查字段是否不存在
         */
        ABSENT,
        /**
         * 检查数组是否包含指定值
         */
        ARRAY_CONTAINS,
        /**
         * 检查数组长度
         */
        ARRAY_LENGTH,
        /**
         * JSON 比较时的选项
         */
        OPTION;

        static Type fromString(String value) {
            try {
                return valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unsupported matcher type: " + value, e);
            }
        }

    }

    private record JsonPathAssertion(@NonNull String jsonPath,
                                     @NonNull Type type,
                                     @NonNull Object expectedValue,
                                     @NonNull String[] options) {

        private void doAssert(DocumentContext ctx) throws JsonProcessingException {

            try {
                Object actualValue = ctx.read(jsonPath);

                switch (type()) {
                    case BY_REGEX -> Assertions.assertThat(actualValue.toString())
                        .as("Regex match failed for path: " + jsonPath)
                        .matches(expectedValue.toString());
                    case BY_EQUALITY -> Assertions.assertThat(actualValue)
                        .as("Equality match failed for path: " + jsonPath)
                        .isEqualTo(expectedValue);
                    case BY_JSON_COMPARE -> assertByJsonCompare(jsonPath, actualValue, expectedValue, options);
                    case BY_DECIMAL_COMPARE -> assertDecimalCompare(jsonPath, actualValue, expectedValue);
                    case BY_NULL -> Assertions.assertThat(Objects.isNull(actualValue))
                        .as("Null check failed for path: " + jsonPath)
                        .isEqualTo(Boolean.valueOf(expectedValue.toString()));
                    case ABSENT -> throw new AssertionError("Field exists but should be absent: " + jsonPath);
                    case ARRAY_CONTAINS -> Assertions.assertThat(actualValue)
                        .asList()
                        .as("Contains check failed for path: " + jsonPath)
                        .contains(expectedValue);
                    case ARRAY_LENGTH -> Assertions.assertThat(actualValue)
                        .asList()
                        .as("Length check failed for path: " + jsonPath)
                        .hasSize((Integer) expectedValue);
                    default -> Assertions.assertThat(actualValue)
                        .as("Default equality check failed for path: " + jsonPath)
                        .isEqualTo(expectedValue);
                }
            } catch (com.jayway.jsonpath.PathNotFoundException e) {
                if (type() == Type.ABSENT)
                    return;
                log.info("JsonPath {} 原字符串 {}", jsonPath, ctx.json().toString());
                throw e;
            }
        }
    }

}
