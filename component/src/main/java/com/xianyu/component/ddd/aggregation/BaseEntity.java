package com.xianyu.component.ddd.aggregation;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 实体基类
 * @author xian_yu_da_ma
 * @date 2022/11/14
 */
@Slf4j
@Getter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Setter(AccessLevel.PACKAGE)
public abstract class BaseEntity<I> implements Serializable, EntityId<I> {

    public static final int DEFAULT_VERSION = 1;

    /**
     * 版本号，乐观锁使用,若数据库没有此字段，可忽略此字段
     */
    @Builder.Default
    protected Integer version = DEFAULT_VERSION;

    protected BaseEntity() {
    }

}
