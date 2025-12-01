package com.xianyu.component.retryjob.context.aware;

import com.xianyu.component.retryjob.context.RetryJobContext;
import com.xianyu.component.retryjob.context.RetryJobResult;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * 拦截器
 *
 * @author 
 * @date 2024/11/20
 */
public interface RetryJobContextInterceptor {


    /**
     * 最开始执行
     */
    Order FIRST = new Order(Integer.MIN_VALUE);
    /**
     * 最后执行
     */
    Order LAST = new Order(Integer.MAX_VALUE);

    /**
     * 执行任务执行调用
     * - 如果返回值不为空,直接使用返回的结果,并且不会执行后序逻辑
     * - 没返回就用正常执行结果
     */
    @Nullable
    RetryJobResult beforeExecute(RetryJobContext context);

    /**
     * 执行任务且完成重试任务内部处理之后调用
     */
    void afterExecute(RetryJobContext context);

    /**
     * 顺序
     */
    Order order();

    @EqualsAndHashCode
    @AllArgsConstructor
    class Order implements Comparable<Order> {
        /**
         * 越小越优先
         */
        private final int value;


        /**
         * 提高优先级
         */
        public Order up() {
            return new Order(value - 1);
        }

        /**
         * 降低优先级
         */
        public Order down() {
            return new Order(value + 1);
        }

        @Override
        public int compareTo(@NotNull Order o) {
            return Integer.compare(value, o.value);
        }
    }
}
