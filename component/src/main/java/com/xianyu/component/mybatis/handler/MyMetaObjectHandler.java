package com.xianyu.component.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xianyu.component.mybatis.BasePo;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 严格填充,只针对非主键的字段,只有该表注解了fill 并且 字段名和字段属性 能匹配到才会进行填充(严格模式填充策略,默认有值不覆盖,如果提供的值为null也不填充)
        strictInsertFill(metaObject, BasePo.Fields.addTime, LocalDateTime.class, LocalDateTime.now());
        strictInsertFill(metaObject, BasePo.Fields.updateTime, LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 严格填充,只针对非主键的字段,只有该表注解了fill 并且 字段名和字段属性 能匹配到才会进行填充(严格模式填充策略,默认有值不覆盖,如果提供的值为null也不填充)
        strictUpdateFill(metaObject, BasePo.Fields.updateTime,  LocalDateTime.class, LocalDateTime.now());
    }
}