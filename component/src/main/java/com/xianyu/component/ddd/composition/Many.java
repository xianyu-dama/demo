package com.xianyu.component.ddd.composition;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Many<E extends BaseEntity<?>> extends Iterable<E> {

    int size();

    Many<E> page(int pageIndex, int pageSize);

    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
