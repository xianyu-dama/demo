package com.xianyu.order.context.order.infr.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xianyu.component.mybatis.BasePo;
import com.xianyu.component.mybatis.handler.String2JsonTypeHandler;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javers.core.metamodel.annotation.Id;

/**
 * <p>
 * 
 * </p>
 *
 * @author xian_yu_da_ma
 * @since 2023-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("\"order\"")
public class OrderPo extends BasePo {

    @Id
    @TableId(type = IdType.INPUT)
    private Long orderId;

    private OrderStatus orderStatus;

    private String currency;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    private BigDecimal shouldPay;

    private BigDecimal actualPay;

    ///////////////////////////////////////////////////////////////////////////
    // 地址相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phoneNumber;

    private String firstName;

    private String lastName;

    /**
     * 地址1
     */
    private String addressLine1;

    /**
     * 地址2
     */
    private String addressLine2;

    /**
     * 国家
     */
    private String country;

    /**
     * 扩展
     */
    @TableField(typeHandler = String2JsonTypeHandler.class)
    private String extension;

    private Long userId;
}