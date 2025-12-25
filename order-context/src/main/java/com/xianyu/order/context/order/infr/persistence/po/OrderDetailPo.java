package com.xianyu.order.context.order.infr.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xianyu.component.mybatis.BasePo;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javers.core.metamodel.annotation.Id;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_item")
public class OrderDetailPo extends BasePo {

    @Id
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Integer productId;

    private OrderStatus orderStatus;

    private BigDecimal price;

    private Boolean locked;


}
