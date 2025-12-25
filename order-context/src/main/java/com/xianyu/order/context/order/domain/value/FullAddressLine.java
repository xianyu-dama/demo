package com.xianyu.order.context.order.domain.value;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class FullAddressLine implements Serializable {

    @NonNull
    private final String addressLine1;

    private final String addressLine2;
}
