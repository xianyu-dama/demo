@org.springframework.modulith.ApplicationModule(
        displayName = "引用（外部上下文映射）模块",
        allowedDependencies = {"order.context.sdk"},
        type = ApplicationModule.Type.OPEN
)
package com.xianyu.order.context.reference;

// 引用模块
// 不依赖order-context内部模块，被其他模块依赖
// 定义与外部系统的领域模型，比如订单引用的包裹、订单引用的Buyer（实际外部是User）

import org.springframework.modulith.ApplicationModule;