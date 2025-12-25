package com.xianyu.order.context.cart.infr.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xianyu.component.mybatis.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车明细Po
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cart_item")
public class CartItemPo extends BasePo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String itemId;

    private Integer quantity;

    @TableLogic
    private Long deleteTimestamp;

}
