package com.xianyu.component.ddd.event.persistence;

import java.time.LocalDateTime;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.Serialized;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(name = "domain_event")
public interface DomainEventPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    String topic();

    @Key
    String tag();

    @Key
    String bizId();

    @Column(name = "\"key\"")
    String key();

    @Serialized
    String content();

    boolean sent();

    String msgId();

    @Column
    LocalDateTime addTime();

    @Column
    LocalDateTime updateTime();

}
