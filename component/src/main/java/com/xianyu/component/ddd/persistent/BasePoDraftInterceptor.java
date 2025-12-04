package com.xianyu.component.ddd.persistent;

import java.time.LocalDateTime;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.jspecify.annotations.Nullable;

/**
 * 会有一个查询SQL，先不需要了，add_time和update_time应该通过触发器控制，由数据库控制，但是有查询需求
 */
// @Component
public class BasePoDraftInterceptor implements DraftInterceptor<BasePo, BasePoDraft> {

    @Override
    public void beforeSave(BasePoDraft draft, @Nullable BasePo original) {
        // 更新
        if (!ImmutableObjects.isLoaded(draft, BasePoProps.UPDATE_TIME)) {
            draft.setUpdateTime(LocalDateTime.now());
        }
        // 新增
        if (original == null) {
            if (!ImmutableObjects.isLoaded(draft, BasePoProps.ADD_TIME)) {
                draft.setAddTime(LocalDateTime.now());
            }
        }
    }

}