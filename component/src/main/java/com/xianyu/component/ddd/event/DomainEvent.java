package com.xianyu.component.ddd.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class DomainEvent implements Serializable {

    private final long timestamp = Instant.now().toEpochMilli();

    private final String msgId = UUID.randomUUID().toString();

    private final String key;

    /**
     * 业务唯一id, 可用于做幂等
     */
    private final String bizId;

    private final String tag;

    /**
     * 事件需要有版本号
     */
    private final long version = 1L;

    protected DomainEvent(@NonNull String key, @NonNull String bizId) {
        this.key = key;
        this.bizId = bizId;
        tag = getTag();
    }

    public abstract String getTag();

}