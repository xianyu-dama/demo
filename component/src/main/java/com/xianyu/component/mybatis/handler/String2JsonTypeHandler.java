package com.xianyu.component.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * <br/>
 * Created on : 2023-09-02 23:10
 * @author xian_yu_da_ma
 */
@MappedJdbcTypes(JdbcType.JAVA_OBJECT)
public class String2JsonTypeHandler extends JsonbTypeHandler<String> {

}
