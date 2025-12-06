# 需求分析
* 理解对应模块的命令系统用例（引起系统发生状态变化的用例），提取名词，分析对应的领域模型，具体包括：aggregation、entity、value object（值对象）、字段，聚合或者实体的业务标识、模型关系，采用简洁的puml类图表示，要求如下
  - 聚合根（aggregation root）使用`<aggregation>`标记
    - 需要根据业务需求分析出聚合边界
    - 保证业务的不变式
  - 实体（entity）
    - 使用`<entity>`标记
  - 值对象（value object）使用`<value>`标记
  - 所有业务标识统一用自定义类型
  - 所有领域模型的字段都需要有注释，说明含义
  - 聚合和内部实体的生命周期一致，采用uml的组合（Composition）表示
  - 不同生命周期的实体之间采用uml的关联（Association）表示，通过业务标识关联