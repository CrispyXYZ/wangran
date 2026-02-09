DROP DATABASE IF EXISTS `wangran_db`;

CREATE DATABASE `wangran_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE wangran_db;

create table merchants
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
    constraint merchants_pk
        unique (phone_number),
    constraint username
        unique (username)
);

create table users
(
    id              int auto_increment
        primary key,
    username        varchar(50) not null,
    phone_number    varchar(20) not null,
    password_sha256 binary(32)  null,
    constraint username
        unique (username),
    constraint users_pk
        unique (phone_number)
);

