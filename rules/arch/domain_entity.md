# 实体entity
- 独立的对象，拥有自己的生命周期和状态
- 继承BaseEntity，填充对应的泛型
- 业务标识需要实现 EntityId 接口，比如 OrderId，属于值对象

- 声明
```
@Slf4j
@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@Setter(AccessLevel.PACKAGE)
```