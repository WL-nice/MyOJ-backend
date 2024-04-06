use `bro-match`;

create table user
(
    id           bigint auto_increment comment '用户id'
        primary key,
    username     varchar(256)                       null comment '用户名',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '手机号',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 null comment '用户状态',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     int      default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户身份',
    acptCode     varchar(512)                       null comment '校验编号',
    tags         varchar(1024)                      null comment '标签列表 [json]'
)
  comment '用户';

 -- 队伍表
create table team
(
    id           bigint auto_increment comment 'id'
        primary key,
    teamName         varchar(256)                     not null comment '队伍名',
    description   varchar(1024)                      null comment '队伍描述',
    expireTime   datetime                            null comment '创建时间',
    userId        bigint                         not null  comment '用户id',
    password varchar(512)                        null comment '密码',
    maxNum   int      default 1                 null comment '队伍成员最大数',
    teamStatus   int      default 0                 null comment '队伍状态',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     int      default 0                 not null comment '是否删除'

)
    comment '队伍';

 -- 用户队伍对应关系
create table user_team
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId        bigint                         not null  comment '用户id',
    teamId        bigint                         not null  comment '队伍id',
    joinTime   datetime                          null comment '加入时间',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     int      default 0                 not null comment '是否删除'

)
    comment '用户-队伍';