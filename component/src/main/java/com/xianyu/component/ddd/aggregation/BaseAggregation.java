package com.xianyu.component.ddd.aggregation;

import com.xianyu.component.ddd.event.DomainEvent;
import com.xianyu.component.ddd.exception.IllegalVersionException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 聚合根基类，包含共用属性和方法<br/>
 * <p>
 * Created on : 2022-02-24 22:49
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Getter
@SuperBuilder(toBuilder = true)
@Setter(AccessLevel.PACKAGE)
public abstract class BaseAggregation<R extends BaseAggregation<R, I>, I> extends BaseEntity<I> {

    /**
     * 领域事件
     */
    protected final transient Queue<DomainEvent> events = new LinkedList<>();

    /**
     * 快照组件
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient Snapshot<R, I> snapshot;

    protected BaseAggregation() {
    }

    public boolean hasSnapshot() {
        return Objects.nonNull(snapshot);
    }

    /**
     * 切面设置快照
     */
    public void attachSnapshot() {
        if (hasSnapshot()) {
            log.warn("快照已存在，当前线程={}", Thread.currentThread().getName());
        }
        snapshot = new Snapshot<>();
        snapshot.set((R) this);
    }

    /**
     * 获取快照
     *
     * @return
     */
    public R snapshot() {
        if (Objects.isNull(snapshot)) {
            throw new IllegalArgumentException("快照不存在，在查询聚合的方法上添加注解--@Snapshot");
        }
        return snapshot.get();
    }

    /**
     * 清空快照
     */
    public void removeSnapshot() {
        if (Objects.isNull(snapshot)) {
            return;
        }
        snapshot.remove();
    }

    /**
     * 检查当前对象和快照版本，如果抛异常，表示当前线程获取两次聚合根，并且做了一次更新，版本号发生变化
     *
     * @throws IllegalVersionException
     */
    public void checkForVersion() throws IllegalVersionException {
        if (Objects.isNull(snapshot)) {
            return;
        }
        R currentSnapshot = snapshot.get();
        if (Objects.nonNull(currentSnapshot) && !Objects.equals(version, currentSnapshot.getVersion())) {
            log.error("版本号有误：当前对象版本号={}，快照对象版本号={}", version, currentSnapshot.getVersion());
            throw new IllegalVersionException("快照版本号有误，请重试");
        }
    }

    /**
     * (领域事件)入队
     *
     * @param event
     */
    protected void addEvent(DomainEvent event) {
        events.add(event);
    }

}
