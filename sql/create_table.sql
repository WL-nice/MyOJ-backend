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

-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    answer      text                               null comment '题目答案',
    submitNum   int      default 0                 not null comment '题目提交数',
    acceptedNum int      default 0                 not null comment '题目通过数',
    judgeCase   text                               null comment '判题用例（json 数组）',
    judgeConfig text                               null comment '判题配置（json 对象）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
    ) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '用户代码',
    judgeInfo  text                               null comment '判题信息（json 对象）',
    status     int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
    ) comment '题目提交';
