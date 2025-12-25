package com.xianyu.jspecify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.jspecify.annotations.NonNull;

/**
 * <br/>
 * Created on : 2025-11-03 19:56
 * @author xian_yu_da_ma
 */
@Getter
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class Person {

    @NonNull
    private String name;

    /**
     * 希望编译期+运行期可以检查
     */
    @NonNull
    private Long age;

    @NonNull
    public String getMyName(@org.springframework.lang.NonNull String prefix) {
        return prefix + name;
    }

}
