package com.xianyu.component.ddd.composition;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import java.util.Optional;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.springframework.data.util.Lazy;


public class Reference<E extends BaseEntity<?>> implements HasOne<E> {

    private final Lazy<E> entitySupplier;

    private Reference(@NonNull Supplier<E> e) {
        entitySupplier = Lazy.of(e);
    }

    public static <E extends BaseEntity<?>> Reference<E> of(@NonNull Supplier<E> e) {
        return new Reference<>(e);
    }

    @Override
    public Optional<E> get() {
        return entitySupplier.getOptional();
    }
}