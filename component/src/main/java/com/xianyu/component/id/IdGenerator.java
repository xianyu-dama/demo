package com.xianyu.component.id;

import java.util.function.LongSupplier;

/**
 * <br/>
 * Created on : 2023-09-08 13:10
 * @author xian_yu_da_ma
 */
public interface IdGenerator {

    String PACKAGE_BEAN_NAME = "idGenerator";

    /**
     * 生成id
     * @return
     */
    long id();

    /**
     * 获取id
     *
     * @param key
     * @return
     */
    default long get(IdName key) {
        return id();
    }

    default LongSupplier getSupplier(IdName key) {
        return () -> get(key);
    }

}
