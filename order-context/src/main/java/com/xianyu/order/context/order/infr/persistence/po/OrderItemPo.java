package com.xianyu.order.context.order.infr.persistence.po;

import com.xianyu.component.ddd.persistent.BasePo;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import java.math.BigDecimal;
import org.babyfish.jimmer.client.TNullable;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.KeyUniqueConstraint;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(name = "order_item")
@KeyUniqueConstraint
public interface OrderItemPo extends BasePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    Integer productId();

    OrderStatus orderStatus();

    BigDecimal price();

    @TNullable
    Boolean locked();

    @Key
    @ManyToOne
    OrderPo order();

    @IdView
    long orderId();
}
