package com.xianyu.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xianyu.utils.JsonTestUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.approvaltests.JsonApprovals;
import org.approvaltests.approvers.FileApprover;
import org.approvaltests.core.Options;
import org.approvaltests.core.VerifyResult;
import org.mockito.Mockito;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;

/**
 * 测试基类<br/>
 * Created on : 2023-03-01 10:31
 * @author xian_yu_da_ma
 */
@Slf4j
public abstract class BaseTest extends Mockito implements BaseAssertions {

    /**
     * jimmer 序列化：ObjectMapper mapper = getObjectMapper().registerModule(new ImmutableModule());
     *
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        return JsonTestUtils.OBJECT_MAPPER.copy();
    }

    private static Options getOptions1() {
        Options options = new Options();
        options = options.withComparator((actualFile, exceptFile) -> {
            if (!exceptFile.exists()) {
                return FileApprover.approveTextFile(actualFile, exceptFile);
            }
            try {
                String actualString = Files.readString(Paths.get(actualFile.getAbsolutePath()));
                String expectString = Files.readString(Paths.get(exceptFile.getAbsolutePath()));
                assertThatJson(actualString)
                        // 忽略数组顺序
                        .when(IGNORING_ARRAY_ORDER)
                        .isEqualTo(expectString);
                return VerifyResult.SUCCESS;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return VerifyResult.FAILURE;
            }
        });
        return options;
    }

    /**
     * 采用json-unit断言，具体规则：getOptions1()
     * 忽略字段的写法：
     * String expected = "{ \"b\": \"${json-unit.ignore}\" }";
     * String actual   = "{ \"a\": \"val2\", \"b\": \"val1\" }";
     *
     * assertThatJson(actual)
     *     .when(IGNORING_EXTRA_FIELDS)
     *     .isEqualTo(expected);
     * @param o
     */
    protected static void assertJSON(Object o) {
        Options options = getOptions1();
        if (o instanceof String str) {
            JsonApprovals.verifyJson(str, options);
            return;
        }
        JsonApprovals.verifyJson(JsonTestUtils.toJSONString(o), options);
    }

    protected static void assertJSON(Object o, ObjectMapper objectMapper) {
        Options options = getOptions1();
        try {
            if (o instanceof String str) {
                JsonApprovals.verifyJson(str, options);
                return;
            }
            JsonApprovals.verifyJson(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o), options);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void assertJSON(Object o, BiConsumer<String, String> assertion) {
        Options options = getOptions2(assertion);
        if (o instanceof String str) {
            JsonApprovals.verifyJson(str, options);
            return;
        }
        JsonApprovals.verifyJson(JsonTestUtils.toJSONString(o), options);
    }

    private static Options getOptions2(BiConsumer<String, String> assertion) {
        Options options = new Options();
        options = options.withComparator((actualFile, exceptFile) -> {
            if (!exceptFile.exists()) {
                return FileApprover.approveTextFile(actualFile, exceptFile);
            }
            try {
                String actualString = Files.readString(Paths.get(actualFile.getAbsolutePath()));
                String expectString = Files.readString(Paths.get(exceptFile.getAbsolutePath()));
                assertion.accept(actualString, expectString);
                return VerifyResult.SUCCESS;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return VerifyResult.FAILURE;
            }
        });
        return options;
    }

}
