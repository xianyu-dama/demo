package com.xianyu.jspecify;

import com.xianyu.base.BaseUnitTest;
import com.xianyu.component.utils.json.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * <br/>
 * Created on : 2025-11-03 19:50
 * @author xian_yu_da_ma
 */
public class JspecifyUnitTest extends BaseUnitTest {

    @Test
    @DisplayName("构建person")
    void should_build_person() {
        assertThrows(NullPointerException.class, () -> Person.builder().age(null).name("name").build());
        var person = Person.builder().age(1L).name("name").build();
        System.out.println(person.getName().toLowerCase() + " " + person.getAge().toString().toLowerCase());
        System.out.println(JsonUtils.toJSONString(person));

        // 应该在编译期报错
        var myName = person.getMyName(null);
        System.out.println(myName);
    }

}
