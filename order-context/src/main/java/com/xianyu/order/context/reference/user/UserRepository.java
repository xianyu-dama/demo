package com.xianyu.order.context.reference.user;

import com.xianyu.component.ddd.composition.Reference;
import com.xianyu.component.ddd.repository.GetRepository;
import com.xianyu.order.context.reference.user.db.UserMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;

import static com.xianyu.order.context.Tables.ORDER_PO_TABLE;


/**
 * <br/>
 * Created on : 2025-04-26 15:28
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class UserRepository implements GetRepository<User, Long> {

    private final JSqlClient jSqlClient;

    private final UserMapper userMapper;

    @Override
    public Optional<User> get(Long id) {
        var orderIds = Lazy.of(() -> jSqlClient.createQuery(ORDER_PO_TABLE)
                .where(ORDER_PO_TABLE.userId().eq(id))
                .select(ORDER_PO_TABLE.orderId()).execute());
        return Optional.of(User.builder().id(id).orderIds(orderIds).build());
    }

    public Reference<User> getReference(Long userId) {
        return Reference.of(() -> get(userId).orElse(null));
    }
}
