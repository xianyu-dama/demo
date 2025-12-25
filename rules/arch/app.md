# 应用服务层
## 每个模块的包定义（按照技术组件划分）
- dto（Data Transfer Object）
  - 定义应用服务的输入参数，如果是命令操作，统一以动词开头，Cmd结尾；读操作以Query结尾
  - 统一采用java的record类型，并且添加注解`@Builder(toBuilder = true)`，如果没有record类型，可以用@Getter和@Builder代替，不要有setter
  - 类名以Dto结尾
- view（View Object）：
    - 类名以`View`结尾
  - 定义方法的返回结果
- service
  - 类名统一以`AppService`后缀结尾，如果是查询业务，统一以`QueryAppService`后缀结尾，遵循CQRS原则
  - 通过声明注解：`@Service`、`@Slf4j`、`@Validated`
  - 方法入参采用dto（Data Transfer Object）；返回值引用应用服务层的View
  - 通过JSR-303的规范，使用注解的方式进行参数校验，dto类型参数都需要添加`@Valid`相关校验，比如`@NotNull`、`@Size`、`@Email`等
- assembler
  - 类名统一以`Assembler`后缀结尾，声明成Spring Bean
  - dto和view的转换
  - dto和领域模型转换
  - 只能包含转换逻辑，不能包含业务逻辑，设置默认值属于业务逻辑，由view或者领域模型完成
