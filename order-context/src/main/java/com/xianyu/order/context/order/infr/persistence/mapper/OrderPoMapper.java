package com.xianyu.order.context.order.infr.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xianyu.order.context.order.infr.persistence.po.OrderPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xian_yu_da_ma
 * @since 2023-08-29
 */
@Mapper
public interface OrderPoMapper extends BaseMapper<OrderPo> {
    OrderPo getForUpdateById(Long id);
}