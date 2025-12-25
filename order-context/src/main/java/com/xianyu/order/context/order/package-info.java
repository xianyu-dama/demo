@org.springframework.modulith.ApplicationModule(
        displayName = "订单模块",
        allowedDependencies = {
                "order.context.reference",
                "order.context.sdk",
                "inventory.context.sdk"}
)
package com.xianyu.order.context.order;