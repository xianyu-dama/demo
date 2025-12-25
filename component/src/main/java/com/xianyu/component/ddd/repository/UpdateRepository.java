package com.xianyu.component.ddd.repository;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import com.xianyu.component.ddd.aop.UpdateAspect;
import org.jspecify.annotations.NonNull;
import org.springframework.validation.annotation.Validated;

/**
 * 更新聚合根<br/>
 * Created on : 2022-03-10 10:08
 *
 * @author xian_yu_da_ma
 */
@Validated
public interface UpdateRepository<R extends BaseEntity<I>, I> {

    /**
     * 更新聚合根
     * 默认包含aop增强：校验聚合根是否有快照 ${@link UpdateAspect}
     *
     * @param aggregate
     * @return 更新的总行数
     */
    int update(@NonNull R aggregate);

}
