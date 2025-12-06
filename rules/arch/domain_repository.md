# domain.repository
* `AddRepository`：新增聚合根，参数为领域对象，返回主键。依赖校验注解，确保入参合法
* `UpdateRepository`：更新聚合根，返回受影响行数。默认有 AOP （UpdateAroundAspect）增强
  - 如果有快照，校验是否存在快照版本一致性
  - 如果有快照，更新之后清空快照
  - 如果有事件，发布事件
* `GetRepository`：按 ID 获取聚合根，提供 `get(id)` 与 `getOrThrow(id)` 两种方式
* `CacheRepository`：从缓存获取只读聚合或快照数据，接口为 `getInCache(id)`，不保证实时一致
* `GetWithLockRepository`：在事务内进行悲观锁查询，默认有切面`TransactionCheckAspect`，`getWithLockOrThrow(id)` 要求使用 `SELECT ... FOR UPDATE`
* `CommonRepository`：组合接口，聚合 `AddRepository`、`UpdateRepository`、`GetRepository`，适用于大多数聚合根仓储