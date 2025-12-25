# 领域层的factory工厂
- 所在目录：com.xianyu.{context}.context.{module}.factory
- 工厂直接通过create()构建领域对象，在方法内部进行业务一致性校验

```java

/**
 * 聚合工厂：返回聚合Builder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class {Aggregation}Factory {

    // private final Collection<Validator> validators;

    public {Aggregation}Builder create{Aggregation}Builder() {
        return new {Aggregation}Builder(validators);
    }

}


/**
 * 聚合构建器{Aggregation}Builder，继承lombok的Builder<br/>
 * Created on : {datetime}
 * @author {author}
 */
public class {Aggregation}Builder extends {Aggregation}.{Aggregation}Builder<{Aggregation}, {Aggregation}Builder> {

    /*@Override
    public {Aggregation}Builder field1(Type1 field1) {
        // 对field1进行校验
        super.field1(field1);
        return self();
    }*/

    protected {Aggregation}Builder self() {
        return this;
    }

    /**
     * 聚合根构造的过程保证不变性
     * @return
     */
    @Override
    public {Aggregation} build() {

        {Aggregation} aggregation = new {Aggregation}(this);

        // 聚合业务一致性检查

        return aggregation;
    }
}

```