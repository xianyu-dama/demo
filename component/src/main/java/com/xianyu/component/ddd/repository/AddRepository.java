package com.xianyu.component.ddd.repository;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.validation.annotation.Validated;

/**
 * 新增聚合<br/>
 * Created on : 2022-03-10 09:59
 *
 * @author xian_yu_da_ma
 */
@Validated
public interface AddRepository<R extends BaseEntity<I>, I> {

    /**
     * 新增聚合根
     * @param aggregate
     */
    I add(@NonNull R aggregate);

}
