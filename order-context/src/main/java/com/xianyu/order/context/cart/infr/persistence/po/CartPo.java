package com.xianyu.order.context.cart.infr.persistence.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.xianyu.component.mybatis.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车Po
 * <br/>
 * Created on : 2025-12-25
 *
 * @author xian_yu_da_ma
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cart")
public class CartPo extends BasePo {

    @TableId
    private Long userId;

    @Version
    private Integer version;

}
