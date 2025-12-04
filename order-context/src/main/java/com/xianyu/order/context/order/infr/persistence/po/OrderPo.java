package com.xianyu.order.context.order.infr.persistence.po;

import com.xianyu.component.ddd.persistent.BasePo;
import com.xianyu.order.context.order.domain.value.Extension;
import com.xianyu.order.context.order.domain.value.FullAddressLine;
import com.xianyu.order.context.order.domain.value.FullName;
import com.xianyu.order.context.order.domain.value.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import org.babyfish.jimmer.Formula;
import org.babyfish.jimmer.client.TNullable;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.Serialized;
import org.babyfish.jimmer.sql.Table;

/**
 * <p>
 * 订单实体
 * </p>
 *
 * @author xian_yu_da_ma
 * @since 2023-08-29
 */
@Entity
@Table(name = "\"order\"")
public interface OrderPo extends BasePo {

    @Id
    long orderId();

    OrderStatus orderStatus();

    String currency();

    /**
     * 汇率
     */
    BigDecimal exchangeRate();

    BigDecimal shouldPay();

    BigDecimal actualPay();

    ///////////////////////////////////////////////////////////////////////////
    // 地址相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 邮箱
     */
    String email();

    /**
     * 电话
     */
    String phoneNumber();

    /**
     * 订单明细列表
     */
    @OneToMany(mappedBy = "order")
    List<OrderItemPo> orderItems();

    String firstName();

    @TNullable
    String lastName();

    @Formula(dependencies = {"firstName", "lastName"})
    default FullName fullName() {
        return FullName.of(firstName(), lastName());
    }

    @Formula(dependencies = {"addressLine1", "addressLine2"})
    default FullAddressLine fullAddressLine() {
        return FullAddressLine.of(addressLine1(), addressLine2());
    }

    /**
     * 地址1
     */
    String addressLine1();

    /**
     * 地址2
     */
    @TNullable
    String addressLine2();

    /**
     * 国家
     */
    String country();

    /**
     * 扩展
     */
    @TNullable
    @Serialized
    Extension extension();

    long userId();
}
