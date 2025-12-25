package com.xianyu.order.context.reference.user;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import java.util.List;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.jspecify.annotations.NonNull;

/**
 * <br/>
 * Created on : 2025-04-26 15:17
 *
 * @author xian_yu_da_ma
 */
@Jacksonized
@Builder(toBuilder = true)
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor(staticName = "of")
public class User extends BaseEntity<Long> {

    /**
     * 订单id集合
     */
    @NonNull
    @Builder.Default
    private final transient Supplier<List<Long>> orderIds = List::of;
    /**
     * 业务标识
     */
    private long id;

    public List<Long> orderIds() {
        return orderIds.get();
    }

    @Override
    public Long id() {
        return id;
    }

}
