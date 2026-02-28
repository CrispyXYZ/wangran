DROP DATABASE IF EXISTS `wangran_db`;

CREATE DATABASE `wangran_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION = 'N' */;

USE wangran_db;

create table merchant
(
    id              int auto_increment
        primary key,
    username        varchar(50)          null,
    merchant_id     varchar(20)          null,
    phone_number    varchar(20)          not null,
    password_sha256 binary(32)           null,
    approval_status tinyint    default 0 not null comment '0=审核中 1=审核通过 2=审核不通过',
    reject_reason   varchar(50)          null,
    deleted         tinyint(1) default 0 not null,
    merchant_id_vc  varchar(20) as (if((`deleted` = 0), `merchant_id`, NULL)),
    phone_number_vc varchar(20) as (if((`deleted` = 0), `phone_number`, NULL)),
    username_vc     varchar(50) as (if((`deleted` = 0), `username`, NULL)),
    constraint merchant_merchant_id_vc_uindex
        unique (merchant_id_vc),
    constraint merchant_phone_number_vc_uindex
        unique (phone_number_vc),
    constraint merchant_username_vc_uindex
        unique (username_vc)
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
    deleted         tinyint(1) default 0 not null,
    event_code_vc   varchar(20) as (if((`deleted` = 0), `event_code`, NULL)),
    constraint event_table_event_code_vc_uindex
        unique (event_code_vc),
    constraint event_table_ibfk_1
        foreign key (merchant_id) references merchant (id)
);

create index merchant_id
    on event_table (merchant_id);

create table organizer
(
    id              int auto_increment
        primary key,
    name            varchar(40)          not null,
    phone_number    varchar(20)          not null,
    address         varchar(40)          not null,
    deleted         tinyint(1) default 0 not null,
    name_vc         varchar(40) as (if((`deleted` = 0), `name`, NULL)),
    phone_number_vc varchar(20) as (if((`deleted` = 0), `phone_number`, NULL)),
    address_vc      varchar(40) as (if((`deleted` = 0), `address`, NULL)),
    constraint organizer_address_vc_uindex
        unique (address_vc),
    constraint organizer_name_vc_uindex
        unique (name_vc),
    constraint organizer_phone_number_vc_uindex
        unique (phone_number_vc)
);

create table organizer_event
(
    organizer_id int                  not null,
    event_id     int                  not null,
    deleted      tinyint(1) default 0 not null,
    id           int auto_increment
        primary key,
    constraint organizer_event_event_table_id_fk
        foreign key (event_id) references event_table (id),
    constraint organizer_event_organizer_id_fk
        foreign key (organizer_id) references organizer (id)
);

create table user_table
(
    id              int auto_increment
        primary key,
    username        varchar(50)          not null,
    phone_number    varchar(20)          not null,
    password_sha256 binary(32)           null,
    deleted         tinyint(1) default 0 not null,
    username_vc     varchar(50) as (if((`deleted` = 0), `username`, NULL)),
    phone_number_vc varchar(20) as (if((`deleted` = 0), `phone_number`, NULL)),
    constraint user_table_phone_number_vc_uindex
        unique (phone_number_vc),
    constraint user_table_username_vc_uindex
        unique (username_vc)
);

create table user_event
(
    ticket_code    varchar(20)          not null,
    create_time    datetime             not null,
    refunded       tinyint(1) default 0 not null,
    user_id        int                  not null,
    event_id       int                  not null,
    deleted        tinyint(1) default 0 not null,
    id             int auto_increment
        primary key,
    ticket_code_vc varchar(20) as (if((`deleted` = 0), `ticket_code`, NULL)),
    constraint user_event_ticket_code_vc_uindex
        unique (ticket_code_vc),
    constraint user_event_event_table_id_fk
        foreign key (event_id) references event_table (id),
    constraint user_event_user_table_id_fk
        foreign key (user_id) references user_table (id)
);

