package com.xianyu.order.context.order.app.view;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xianyu.component.exception.BizException;
import com.xianyu.order.context.reference.product.Sku;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <br/>
 * Created on : 2023-09-25 22:52
 * @author xian_yu_da_ma
 */
@Slf4j
@Getter
public class OrderDetailViewContext {

    /**
     * 订单查询线程池，订单查询的线程数不会太多，按道理不会导致OOM
     */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(
            30,
            new ThreadFactoryBuilder().setNameFormat("queryBy-order-context-%d").build()
    );
    private Map<Integer, Sku> productId2SkuInfo = Collections.emptyMap();

    private OrderDetailViewContext() {}

    public static OrderVoContextBuilder builder() {
        return new OrderVoContextBuilder();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder

    ///////////////////////////////////////////////////////////////////////////

    public static class OrderVoContextBuilder {

        private final OrderDetailViewContext orderVoContext;

        /**
         * 构建的过程中，异步查询
         */
        private final List<CompletableFuture<Void>> taskList = new ArrayList<>();

        private OrderVoContextBuilder() {
            orderVoContext = new OrderDetailViewContext();
        }

        /**
         * 添加任务
         *
         * @param runnable
         */
        private void addTask(Runnable runnable) {
            taskList.add(CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE));
        }

        /**
         * 查询sku
         *
         * @param skuList
         * @return
         */
        public OrderVoContextBuilder sku(Supplier<List<Sku>> skuInfoSupplier) {
            addTask(() -> orderVoContext.productId2SkuInfo = skuInfoSupplier.get().stream()
                    .collect(Collectors.toMap(Sku::getSkuId, Function.identity())));
            return this;
        }

        /**
         * 构建上下文实例
         *
         * @return
         */
        public OrderDetailViewContext build() {
            try {
                CompletableFuture.allOf(taskList.toArray(CompletableFuture[]::new)).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("查询异常", e);
                throw new BizException("订单列表查询异常");
            }
            return orderVoContext;
        }
    }

}
