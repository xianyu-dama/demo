package com.xianyu.component.ddd.aggregation;

/**
 * id接口，可以定义成泛型<br/>
 * Created on : 2024-04-03 22:11
 *
 * @author xian_yu_da_ma
 */
public interface EntityId<T> {

    /**
     * 获取id
     * @return
     */
    T id();

}
