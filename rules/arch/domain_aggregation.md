# 领域层的聚合aggregation
- 所在目录：com.xianyu.{context}.context.{module}
- 聚合是一个业务边界，包含一个或多个实体和对象，聚合根是聚合的入口，比如Order关联多个OrderItem
- 聚合继承BaseAggregation，填充对应的泛型
- 业务标识需要实现 EntityId 接口，比如 OrderId，属于值对象
- 聚合类模板
```
/**
 * 聚合<br/>
 * 内部实体所有写的操作，必须通过聚合根调用，所以改成包可见(default)
 * 内部实体所有读操作，可以改成公共(public)
 * Created on : {datetime}
 *
 * @author {author}
 */
@Slf4j
@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@Setter(AccessLevel.PACKAGE)
public class {Aggregation} extends BaseAggregation<{Aggregation}, IdType> {

    private IdType id;

    // 必填声明：@lombok.NonNull
    private Type1 field1;

    // 非必填声明：@jakarta.annotation.Nullable
    private Type2 field2;

    private Type3 field3;


    @Override
    public IdType id() {
        return id;
    }
}

```
- 聚合Builder的模板代码