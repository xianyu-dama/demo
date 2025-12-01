package com.xianyu.component.ddd.aggregation;

import java.util.Collection;
import java.util.function.Function;



public interface Identified<T, I> {

    default boolean isDuplicated() {
        var collection = getCollection();
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        long count = collection.stream().map(identify()).distinct().count();
        return count != collection.size();
    }

    Collection<T> getCollection();

    Function<T, I> identify();
}