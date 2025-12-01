package com.xianyu.component.ddd.repository;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import java.util.Optional;

/**
 * 获取聚合根<br/>
 * Created on : 2022-03-10 10:09
 *
 * @author xian_yu_da_ma
 */
public interface GetRepository<R extends BaseEntity<I>, I> {

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    Optional<R> get(I id);

    /**
     * 根据id查询，不存在则抛异常
     * @param id
     * @return
     */
    default R getOrThrow(I id) {
        return get(id).orElseThrow(() -> new IllegalArgumentException("聚合根不存在:%s".formatted(id)));
    }

}
