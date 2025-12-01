package com.xianyu.component.ddd.repository;

import com.xianyu.component.ddd.aggregation.BaseEntity;

/**
 * 带锁获取聚合<br/>
 * Created on : 2025-05-10 23:33
 * @author xian_yu_da_ma
 */
public interface GetWithLockRepository<R extends BaseEntity<I>, I> {

    /**
     * 根据id查询，不存在则抛异常
     * @param id
     * @return
     */
    R getWithLockOrThrow(I id);

}
