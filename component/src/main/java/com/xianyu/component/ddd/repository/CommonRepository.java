package com.xianyu.component.ddd.repository;

import com.xianyu.component.ddd.aggregation.BaseEntity;

/**
 * 通用仓储接口
 *
 * @author xian_yu_da_ma
 * @date 2022/11/01
 */
public interface CommonRepository<R extends BaseEntity<I>, I>
        extends AddRepository<R, I>, UpdateRepository<R, I>,
        GetRepository<R, I> {

}
