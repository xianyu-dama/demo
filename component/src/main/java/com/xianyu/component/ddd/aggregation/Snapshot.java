package com.xianyu.component.ddd.aggregation;

import org.apache.commons.lang3.SerializationUtils;

/**
 * 如果聚合根需要实现有状态，在持久化的时候，需要通过快照diff，只更新变化的实体，提高性能，大部分情况没必要diff，毕竟都是小聚合
 * <p>
 * 1.这里为什么要用ThreadLocal，而不是一个布尔值变量控制？
 * 防止聚合根异步更新，如果异步更新，会检查快照是否存在，不存在则直接报错
 * 事务是线程相关的，保证聚合根在一次事务有效
 * <p>
 * 2.为了解决的问题：get一次聚合根，只能update一次，update两次会出问题：
 * Order order = orderRepository.get(orderId);
 * order.cancel(xxxx);
 * orderRepository.update(order);
 * <p>
 * // order的缓存对象已经变化了，diff的时候会把脏数据更新（上面改成【已取消】，下面没判断出来改成【未取消】）
 * order.suspend(xxx, xxx);
 * orderRepository.update(order);
 * 注意：缓存聚合根快照（不能声明成static，会导致内存异常）
 *
 * @author xian_yu_da_ma
 **/
public class Snapshot<R extends BaseAggregation<R, I>, I> {

    private /*static 会导致内存异常*/ final ThreadLocal<R> context = new ThreadLocal<>();

    /**
     * 设置快照
     */
    @SuppressWarnings("unchecked")
    public void set(R root) {
        context.remove();
        context.set(SerializationUtils.clone(root));
    }

    /**
     * 获取快照
     *
     * @return
     */
    public R get() {
        return context.get();
    }

    /**
     * 移除快照
     */
    public void remove() {
        context.remove();
    }

}