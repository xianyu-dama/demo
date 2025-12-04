CREATE TABLE IF NOT EXISTS "order"
(
    order_id         BIGINT                              NOT NULL
    CONSTRAINT "PRIMARY_order"
    PRIMARY KEY,
    order_status     TEXT                                NOT NULL,
    currency         TEXT                                NOT NULL,
    exchange_rate    NUMERIC                             NOT NULL,
    should_pay      NUMERIC                             NOT NULL,
    actual_pay      NUMERIC                             NOT NULL,
    email            TEXT                                NOT NULL,
    phone_number     TEXT                                NOT NULL,
    first_name       TEXT                                NOT NULL,
    last_name        TEXT,
    address_line1    TEXT                                NOT NULL,
    address_line2    TEXT,
    country          TEXT                                NOT NULL,
    extension        JSONB     DEFAULT '{}'::JSONB,
    user_id          BIGINT                              NOT NULL,
    add_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item
(
    id           BIGINT                                NOT NULL
    CONSTRAINT "PRIMARY_order_item"
    PRIMARY KEY,
    order_id     BIGINT                                NOT NULL,
    product_id   BIGINT not null,
    order_status TEXT                                  NOT NULL,
    price        NUMERIC                               NOT NULL,
    locked       BOOLEAN,
    add_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS order_item_id_seq;

ALTER TABLE order_item
    ALTER COLUMN id SET DEFAULT nextval('order_item_id_seq'::REGCLASS);

CREATE TABLE IF NOT EXISTS domain_event
(
    id          BIGINT                                NOT NULL
    PRIMARY KEY,
    topic       TEXT                                  NOT NULL,
    tag         TEXT                                  NOT NULL,
    biz_id      TEXT                                  NOT NULL,
    key         TEXT                                  NOT NULL,
    content     JSONB                                 NOT NULL,
    sent        BOOLEAN   DEFAULT FALSE              NOT NULL,
    msg_id      TEXT                                  NOT NULL,
    add_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_topic_tag_biz_id
    UNIQUE (topic, tag, biz_id)
    );

CREATE INDEX IF NOT EXISTS domain_event_sharding_key_index ON domain_event (key);

CREATE SEQUENCE IF NOT EXISTS domain_event_id_seq;

ALTER TABLE domain_event
    ALTER COLUMN id SET DEFAULT nextval('domain_event_id_seq'::REGCLASS);

CREATE TABLE IF NOT EXISTS cart
(
    user_id     BIGINT                              NOT NULL
    PRIMARY KEY,
    version     INTEGER                             NOT NULL,
    add_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_item
(
    id                 BIGSERIAL                             NOT NULL
    CONSTRAINT cart_item_pk
    PRIMARY KEY,
    user_id            BIGINT                                not NULL,
    item_id            TEXT                                  NOT NULL,
    quantity           INTEGER                               NOT NULL,
    delete_timestamp   BIGINT                                NOT NULL,
    add_time           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT cart_item_uk
    UNIQUE (user_id, item_id, delete_timestamp)
    ) ;

-- auto-generated definition
CREATE TABLE IF NOT EXISTS retry_job
(
    id                  bigserial
        primary key,
    "key"        text                                                               not null,
    content             text,
    type                text                                                               not null,
    status              text                                                                   not null,
    current_retry_times integer                                                                    not null,
    max_retry_times     integer                                                                    not null,
    next_execute_time   timestamp(6),
    last_error_msg      text,
    add_time            timestamp(6) default current_timestamp not null,
    update_time         timestamp(6) default current_timestamp not null
);

CREATE UNIQUE INDEX IF NOT EXISTS retry_job_uk_type_key
    ON retry_job ("key", type);

comment on table retry_job is '新版重试任务表';

comment on column retry_job.id is '主键';

comment on column retry_job."key" is '业务唯一ID';

comment on column retry_job.content is '重试内容';

comment on column retry_job.type is '任务类型';

comment on column retry_job.status is '当前状态；0-处理中,1-成功(终态),2-失败(终态)';

comment on column retry_job.current_retry_times is '当前重试次数';

comment on column retry_job.max_retry_times is '最大重试次数';

comment on column retry_job.next_execute_time is '下次执行时间';

comment on column retry_job.last_error_msg is '上次失败原因';

comment on column retry_job.add_time is '添加时间';

comment on column retry_job.update_time is '更新时间';
