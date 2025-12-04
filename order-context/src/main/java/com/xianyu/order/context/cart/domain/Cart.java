package com.xianyu.order.context.cart.domain;

import com.xianyu.component.ddd.aggregation.BaseAggregation;
import com.xianyu.component.ddd.composition.HasOne;
import com.xianyu.component.ddd.composition.Reference;
import com.xianyu.order.context.reference.user.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

/**
 * <br/>
 * Created on : 2023-12-27 20:24
 * @author xian_yu_da_ma
 */
@Slf4j
@SuperBuilder
@Jacksonized
@Getter
@Setter(AccessLevel.PACKAGE)
public class Cart extends BaseAggregation<Cart, Long> {

    @NonNull
    private final transient HasOne<User> user;
    @NonNull
    @Builder.Default
    private final Map<String, CartItem> items = new HashMap<>();
    @NonNull
    private Long userId;

    public static Cart of(User user) {
        return builder().userId(user.id()).user(Reference.of(() -> user)).build();
    }

    public int getQuantity(String itemId) {
        CartItem item = getItems().get(itemId);
        Assert.notNull(item, "Cart item does not exist:" + itemId);
        return item.getQuantity();
    }

    public User user() {
        return user.getOrThrow();
    }

    public void addItems(List<CartItem> addItems) {
        if (CollectionUtils.isEmpty(addItems)) {
            return;
        }
        addItems.forEach(addItem -> {
            CartItem existing = items.get(addItem.getItemId());
            if (existing != null) {
                existing.addQuantity(addItem.getQuantity());
            } else {
                items.put(addItem.getItemId(), addItem);
            }
        });
    }

    public Optional<CartItem> get(String itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public int getItemSize() {
        return items.size();
    }

    public Set<String> getItemCodes() {
        return new HashSet<>(items.keySet());
    }


    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Long id() {
        return userId;
    }

    public void removeItem(String itemId) {
        if (!items.containsKey(itemId)) {
            throw new IllegalArgumentException("Shopping cart item does not exist:" + itemId);
        }
        items.remove(itemId);
    }
}
