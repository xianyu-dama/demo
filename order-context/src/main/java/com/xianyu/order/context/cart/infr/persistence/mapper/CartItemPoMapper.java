package com.xianyu.order.context.cart.infr.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xianyu.order.context.cart.infr.persistence.po.CartItemPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车明细Mapper
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Mapper
public interface CartItemPoMapper extends BaseMapper<CartItemPo> {

}
