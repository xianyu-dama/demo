package com.xianyu.order.context.order.domain.value;

import cn.hutool.core.lang.Assert;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * 姓名<br/>
 * Created on : 2023-08-31 12:53
 * @author xian_yu_da_ma
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class FullName implements Serializable {

    @NonNull
    private final String firstName;
    private final String lastName;

    public void validate() {
        Assert.notBlank(getFirstName(), "firstName is null");
    }
}
