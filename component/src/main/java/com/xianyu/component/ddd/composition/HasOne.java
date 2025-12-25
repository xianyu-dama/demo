package com.xianyu.component.ddd.composition;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import java.util.Optional;

public interface HasOne<E extends BaseEntity<?>> {

    Optional<E> get();

    default E getOrThrow() {
        return get().orElseThrow();
    }


}
