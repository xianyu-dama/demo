package com.xianyu.order.context.cart.infr.persistence.po;

import com.xianyu.component.ddd.persistent.BasePo;
import java.util.List;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.Version;

@Entity
@Table(name = "cart")
public interface CartPo extends BasePo {

    @Id
    long userId();

    @Version
    int version();

    @OneToMany(mappedBy = "cart")
    List<CartItemPo> items();
}