package com.xianyu.component.ddd.composition;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import java.util.Optional;

public interface HasMany<ID, E extends BaseEntity<?>> {
    Many<E> findAll();

    Optional<E> find(ID identifier);
}