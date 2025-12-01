package com.xianyu.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于测试打印JSON<br/>
 * Created on : 2023-01-31 14:43
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@UtilityClass
public class JsonTestUtils {

    public static final JsonMapper OBJECT_MAPPER = JsonMapper.builder()
        .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
        .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        // 枚举写成字符串
        .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
        // 时间序列化
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        // 时区
        .defaultLocale(Locale.CHINA)
        .defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        .addModule(new JavaTimeModule())
        .addModule(new SimpleModule().addSerializer(BigDecimal.class, new ToStringSerializer()))
        .build();

    /**
     * 将对象序列化成json字符串
     *
     * @param value javaBean
     * @param <T>   T 泛型标记
     * @return jsonString json字符串
     */
    public static <T> String toJSONString(T value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将json反序列化成对象
     *
     * @param content   content
     * @param valueType class
     * @param <T>       T 泛型标记
     * @return Bean
     */
    public static <T> T json2JavaBean(String content, Class<T> valueType) {
        if (Objects.isNull(content)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(content, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json反序列化成对象
     *
     * @param content       content
     * @param typeReference 泛型类型
     * @param <T>           T 泛型标记
     * @return Bean
     */
    public static <T> T json2JavaBean(String content, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(content, typeReference);
        } catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e.getMessage());
        }
    }

    public static <T> List<T> json2Array(String json, Class<T> valueTypeRef) {

        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, valueTypeRef);
        List<T> objectList = Collections.emptyList();
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return objectList;
    }

    /**
     * 合并两个json字符串，json2中的字段如果json1中不存在或者null，则添加到json1中
     *
     * @param json1 基准字符串（被填充字符串）
     * @param json2 填充字符串
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T mergeIfAbsent(T json1, T json2) {
        try {
            if (json1 instanceof String && json2 instanceof String) {
                return (T) doMergeIfAbsent((String) json1, (String) json2);
            }
            return (T) json2JavaBean(doMergeIfAbsent(toJSONString(json1), toJSONString(json2)), json1.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param json1 基准字符串（被填充字符串）
     * @param json2 填充字符串
     * @return
     * @throws Exception
     */
    private static String doMergeIfAbsent(String json1, String json2) throws Exception {
        JsonNode node1 = OBJECT_MAPPER.readTree(json1);
        JsonNode node2 = OBJECT_MAPPER.readTree(json2);

        mergeRecursive((ObjectNode) node1, (ObjectNode) node2);
        return OBJECT_MAPPER.writeValueAsString(node1);
    }

    private static void mergeRecursive(ObjectNode target, ObjectNode source) {
        source.fieldNames().forEachRemaining(field -> {
            JsonNode sourceValue = source.get(field);
            JsonNode targetValue = target.get(field);

            if (targetValue == null || targetValue.isNull()) {
                target.set(field, sourceValue);
            } else if (sourceValue.isObject() && targetValue.isObject()) {
                mergeRecursive((ObjectNode) targetValue, (ObjectNode) sourceValue);
            } else if (sourceValue.isArray() && targetValue.isArray()) {
                mergeArrays((ArrayNode) targetValue, (ArrayNode) sourceValue);
            }
            // else: if exists and is scalar, keep target's value
        });
    }

    private static void mergeArrays(ArrayNode targetArray, ArrayNode sourceArray) {
        // 遍历源数组中的每个元素
        for (int i = 0; i < sourceArray.size(); i++) {
            JsonNode sourceElement = sourceArray.get(i);
            boolean elementExists = false;

            // 检查目标数组中是否已存在完全相同的元素
            for (int j = 0; j < targetArray.size(); j++) {
                JsonNode targetElement = targetArray.get(j);

                // 如果元素完全相同，则标记为已存在
                if (sourceElement.equals(targetElement)) {
                    elementExists = true;
                    break;
                }

                // 如果两者都是对象，且需要递归合并，可以在这里添加额外的逻辑
                // 但根据您的要求，我们只检查完全相同的情况
            }

            // 如果元素不存在于目标数组中，则添加它
            if (!elementExists) {
                targetArray.add(sourceElement);
            }
        }
    }

}
