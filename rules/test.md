# 测试类规范
- 基于JUnit5+mockito
- 单元测试类统一以`UnitTest`后缀结尾
- 集成测试类统一以`IntegrationTest`后缀结尾，继承`BaseIntegrationTest`，所有集成测试的mockbean定义在`SharedMocks`，会污染整个容器上下文
- 每个测试方法，用中文注解org.junit.jupiter.api.DisplayName说明场景
- 每个测试方法，遵循`given-when-then`的规范
- 测试类必须是public的，不然cucumber找不到
- 如果通过verify检查，需要断言对应的入参