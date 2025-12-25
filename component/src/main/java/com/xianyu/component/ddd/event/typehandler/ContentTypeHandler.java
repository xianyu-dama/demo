package com.xianyu.component.ddd.event.typehandler;

import com.xianyu.component.mybatis.handler.JsonbTypeHandler;

/**
 * Content字段的TypeHandler
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
public class ContentTypeHandler extends JsonbTypeHandler<String> {

    @Override
    protected Class<String> getType() {
        return String.class;
    }
}
