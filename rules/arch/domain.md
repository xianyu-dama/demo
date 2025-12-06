# 领域层

- 聚合:[domain_aggregation.md]
- 聚合工厂:[domain_factory.md]
  - 采用聚合工厂需要符合：只有当聚合创建需要复杂业务校验时才需要，如果聚合创建的场景相对简单，直接使用 builder 模式就足够了。避免过度设计，保持代码的简洁性。
  - 保证聚合的一致性，反之不需要聚合工厂
- 实体:[domain_entity.md]
- 领域服务:[domain_service.md]
- event：领域事件，类名以`Event`后缀结尾，继承`DomainEvent`，用【名词+动词过去式】命名，比如订单取消事件OrderCanceledEvent
- repository：仓储接口，类名以`Repository`后缀结尾，继承`CommonRepository`，用【名词+Repository】命名，比如订单仓储OrderRepository
  - 如果需要查询聚合需要从缓存获取，实现`CacheRepository`接口
- value：对应ddd的value object
  - 重写equals和hashCode方法
  - 不可变，采用 record 语法