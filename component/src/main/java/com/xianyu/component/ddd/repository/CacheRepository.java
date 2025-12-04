package com.xianyu.component.ddd.repository;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

/**
 * <br/>
 * Created on : 2023-09-03 11:41
 * @author xian_yu_da_ma
 */
public interface CacheRepository<R extends BaseAggregation<R, I>, I> {

    /**
     * 从缓存获取聚合根（非实时数据）
     * @param id
     * @return
     */
    Optional<R> getInCache(@NonNull I id);

}