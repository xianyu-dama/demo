package com.xianyu.component.ddd.event.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xianyu.component.ddd.event.typehandler.ContentTypeHandler;
import com.xianyu.component.mybatis.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author xian_yu_da_ma
 * @since 2023-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("domain_event")
public class DomainEventPo extends BasePo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String topic;

    private String tag;

    private String bizId;

    private String key;

    @TableField(typeHandler = ContentTypeHandler.class)
    private String content;

    private Boolean sent;

    private String msgId;

}