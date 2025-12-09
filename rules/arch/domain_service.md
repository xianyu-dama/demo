# 领域服务
  - 入参和出参只能是领域模型或者原生类型
  - 不包含业务逻辑，只负责编排
  - 无状态：内部不包含成员状态
  - 复用的用例都写到领域层，比如：生单、注册用户
  - 承担部分复杂的聚合方法，聚合根无需暴露对应方法，业务逻辑放到到领域服务，比如订单聚合取消，需要其他对象协调，Order.cancel()方法不需要暴露，通过同级别的OrderService.cancel()实现订单取消

```java
/**
 * 聚合服务：领域服务<br/>
 * Created on : {datetime}
 * @author {author}
 */
@Service
@RequiredArgsConstructor
public class {Aggregation}Service {


}
```