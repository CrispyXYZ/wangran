DROP DATABASE IF EXISTS `wangran_db`;

CREATE DATABASE `wangran_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION = 'N' */;

USE wangran_db;

create table merchant
(
    id              int auto_increment
        primary key,
    username        varchar(50)       null,
    merchant_id     varchar(20)       null,
    phone_number    varchar(20)       not null,
    password_sha256 binary(32)        null,
    approval_status tinyint default 0 not null comment '0=审核中 1=审核通过 2=审核不通过',
    reject_reason   varchar(50)       null,
    constraint merchant_id
        unique (merchant_id),
    constraint merchant_pk
        unique (phone_number),
    constraint username
        unique (username)
);

create table event_table
(
    id              int auto_increment
        primary key,
    event_code      varchar(20)          not null,
    event_name      varchar(40)          not null,
    event_type      varchar(10)          not null,
    event_time      datetime             not null,
    city            varchar(10)          not null,
    price           decimal(10, 2)       not null,
    stock           int                  not null,
    on_shelf        tinyint(1) default 0 not null,
    sale_start_time datetime             not null,
    sale_end_time   datetime             not null,
    merchant_id     int                  not null,
    constraint event_code
        unique (event_code),
    constraint event_table_ibfk_1
        foreign key (merchant_id) references merchant (id)
);

create index merchant_id
    on event_table (merchant_id);

create table organizer
(
    id           int auto_increment
        primary key,
    name         varchar(40) not null,
    phone_number varchar(20) not null,
    address      varchar(40) not null,
    constraint address
        unique (address),
    constraint name
        unique (name),
    constraint phone_number
        unique (phone_number)
);

create table organizer_event
(
    organizer_id int not null,
    event_id     int not null,
    primary key (organizer_id, event_id),
    constraint organizer_event_ibfk_1
        foreign key (organizer_id) references organizer (id),
    constraint organizer_event_ibfk_2
        foreign key (event_id) references event_table (id)
);

create table user_table
(
    id              int auto_increment
        primary key,
    username        varchar(50) not null,
    phone_number    varchar(20) not null,
    password_sha256 binary(32)  null,
    constraint user_table_pk
        unique (phone_number),
    constraint username
        unique (username)
);

create table user_event
(
    ticket_code varchar(20)          not null,
    create_time datetime             not null,
    refunded    tinyint(1) default 0 not null,
    user_id     int                  not null,
    event_id    int                  not null,
    primary key (user_id, event_id),
    constraint ticket_code
        unique (ticket_code),
    constraint user_event_ibfk_1
        foreign key (user_id) references user_table (id),
    constraint user_event_ibfk_2
        foreign key (event_id) references event_table (id)
);

create index event_id
    on user_event (event_id);


