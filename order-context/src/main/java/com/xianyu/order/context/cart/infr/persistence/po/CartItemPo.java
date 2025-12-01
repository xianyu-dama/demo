package com.xianyu.order.context.cart.infr.persistence.po;

import com.xianyu.component.ddd.persistent.BasePo;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.KeyUniqueConstraint;
import org.babyfish.jimmer.sql.LogicalDeleted;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OnDissociate;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.LogicalDeletedLongGenerator;

@Entity
@Table(name = "cart_item")
@KeyUniqueConstraint
public interface CartItemPo extends BasePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    String itemId();

    int quantity();

    @LogicalDeleted(generatorType = LogicalDeletedLongGenerator.class)
    long deleteTimestamp();

    @Key
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDissociate(DissociateAction.DELETE)
    CartPo cart();

}