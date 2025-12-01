package com.xianyu.component.ddd.composition;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * 关联（数据库、RPC）集合
 * @param <Id>
 * @param <E>
 */
public abstract class Entities<Id, E extends BaseEntity<?>> implements Many<E>, HasMany<Id, E> {
    @Override
    public final Many<E> findAll() {
        return this;
    }

    @Override
    public final Optional<E> find(Id identifier) {
        return Optional.ofNullable(findById(identifier));
    }

    @Override
    public final Many<E> page(int from, int to) {
        return new MemoryEntities<>(findByPage(from, to)) { };
    }

    @Override
    public final Iterator<E> iterator() {
        return new BatchIterator();
    }

    protected int batchSize() {
        return 100;
    }

    protected abstract List<E> findByPage(int pageIndex, int pageSize);

    @Nullable
    protected abstract E findById(Id id);

    private static class MemoryEntities<Id, E extends BaseEntity<?>> implements Many<E>, HasMany<Id, E> {

        private final List<E> list;

        MemoryEntities(List<E> list) {
            this.list = list;
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public Many<E> page(int pageIndex, int pageSize) {
            return new MemoryEntities<>(list.stream()
                    .skip((long) (pageIndex - 1) * pageSize)
                    .limit(pageSize).toList());
        }

        @Override
        public @NonNull Iterator<E> iterator() {
            return list.iterator();
        }

        @Override
        public Many<E> findAll() {
            return this;
        }

        @Override
        public Optional<E> find(Id identifier) {
            return stream().filter(it -> it.id().equals(identifier))
                    .findFirst();
        }
    }

    private class BatchIterator implements Iterator<E> {

        private final int size;
        private Iterator<E> iterator;
        private int current = 0;

        public BatchIterator() {
            size = size();
            iterator = nextBatch();
        }

        private Iterator<E> nextBatch() {
            return page(current, Math.min(batchSize(), size)).iterator();
        }

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public E next() {
            if (!iterator.hasNext()) iterator = nextBatch();
            current++;
            return iterator.next();
        }
    }
}
