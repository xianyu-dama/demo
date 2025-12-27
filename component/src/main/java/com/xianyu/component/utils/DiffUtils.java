package com.xianyu.component.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.custom.CustomValueComparator;
import org.springframework.util.Assert;

@Slf4j
public class DiffUtils {

    public static final String ID_FIELD = "id";

    private static final Javers javers = JaversBuilder.javers()
            .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
            .registerValue(BigDecimal.class, new CustomFixedEqualBigDecimalComparator())
            .build();

    /**
     * @param oldList       旧list
     * @param newList       新list
     * @param clazz         class
     * @param addConsume    新增
     * @param updateConsume 修改
     * @param removeConsume 删除
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> void listChangeFunction(List<T> oldList,
            List<T> newList,
            Class<T> clazz,
            Consumer<List<T>> addConsume,
            Consumer<List<T>> updateConsume,
            Consumer<List<T>> removeConsume) {

        Assert.notNull(addConsume, "addConsume不能为空");
        Assert.notNull(updateConsume, "updateConsume不能为空");
        Assert.notNull(removeConsume, "removeConsume不能为空");

        collectionChangeFunction(oldList, newList, clazz,
            addMap -> addConsume.accept(new ArrayList<>(addMap.values())),
            updateMap -> updateConsume.accept(new ArrayList<>(updateMap.values())),
            removeMap -> removeConsume.accept((new ArrayList<>(removeMap.values()))));
    }

    /**
     * map 里面的key 是@id 的值,需要用到long类型 需要自己强转
     *
     * @param oldList
     * @param newList
     * @param clazz
     * @param addConsume
     * @param updateConsume
     * @param removeConsume
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> void collectionChangeFunction(Collection<T> oldList,
        Collection<T> newList,
        Class<T> clazz,
        Consumer<Map<String, T>> addConsume,
        Consumer<Map<String, T>> updateConsume,
        Consumer<Map<String, T>> removeConsume) {

        Diff listDiff = javers.compareCollections(oldList, newList, clazz);

        Map<String, T> addMap = new HashMap<>();
        Map<String, T> removeMap = new HashMap<>();
        Map<String, T> updateMap = new HashMap<>();

        for (Change change : listDiff.getChanges()) {
            if ((change instanceof NewObject)) {
                addMap.put(change.getAffectedLocalId().toString(), (T) change.getAffectedObject().get());
            }

            if ((change instanceof ObjectRemoved)) {
                removeMap.put(change.getAffectedLocalId().toString(), (T) change.getAffectedObject().get());
            }

            if (change instanceof ValueChange valueChange
                && valueChange.getLeft() != null
                && valueChange.getAffectedLocalId() != null) {
                updateMap.put(valueChange.getAffectedLocalId().toString(), (T) valueChange.getAffectedObject().get());
            }
        }
        if (!addMap.isEmpty()) {
            addConsume.accept(addMap);
        }
        if (!updateMap.isEmpty()) {
            updateConsume.accept(updateMap);
        }
        if (!removeMap.isEmpty()) {
            removeConsume.accept(removeMap);
        }
    }


    /**
     *
     * @param oldList
     * @param newList
     * @return left=新增集合(addList),middle=更新集合(updateList),right=删除集合(removeList)
     */
    public static <T extends Serializable> ImmutableTriple<List<T>, List<T>, List<T>> diff(Class<T> entityClass, List<T> oldList, List<T> newList) {
        List<T> addList = new ArrayList<>();
        List<T> updateList = new ArrayList<>();
        List<T> removeList = new ArrayList<>();
        listChangeFunction(
                oldList,
                newList,
                entityClass,
                addList::addAll,
                updateList::addAll,
            removeList::addAll
        );
        return ImmutableTriple.of(addList, updateList, removeList);
    }

    private static class CustomFixedEqualBigDecimalComparator implements CustomValueComparator<BigDecimal> {

        @Override
        public boolean equals(BigDecimal a, BigDecimal b) {
            return a.compareTo(b) == 0;
        }

        @Override
        public String toString(BigDecimal value) {
            return value.toPlainString();
        }
    }

}
