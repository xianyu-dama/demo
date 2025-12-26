package com.xianyu.order.context.order.infr.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 订单查询Mapper（仅用于MyBatis集成测试）
 * <br/>
 * Created on : 2025-12-26
 *
 * @author xian_yu_da_ma
 */
@Mapper
public interface OrderQueryMapper {
    
    int ping();
    
    String currentSchema();
    
    Integer select();
}
