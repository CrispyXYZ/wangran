-- 声明：此测试SQL由AI生成且未经人工审核
-- 商户表
create table if not exists merchant (
                                        id int auto_increment primary key,
                                        username varchar(50) null,
                                        merchant_code varchar(20) null,
                                        phone_number varchar(20) not null,
                                        password_sha256 binary(32) null,
                                        approval_status tinyint default 0 not null,
                                        reject_reason varchar(50) null,
                                        deleted tinyint default 0 not null,
                                        phone_number_vc varchar(20) generated always as (case when deleted = 0 then phone_number else null end),
                                        username_vc varchar(50) generated always as (case when deleted = 0 then username else null end),
                                        merchant_code_vc varchar(20) generated always as (case when deleted = 0 then merchant_code else null end)
);

-- 唯一约束
create unique index if not exists merchant_phone_number_vc_uindex on merchant(phone_number_vc);
create unique index if not exists merchant_username_vc_uindex on merchant(username_vc);
create unique index if not exists merchant_merchant_code_vc_uindex on merchant(merchant_code_vc);

-- 事件表
create table if not exists event_table (
                                           id int auto_increment primary key,
                                           event_code varchar(20) not null,
                                           event_name varchar(40) not null,
                                           event_type varchar(10) not null,
                                           event_time datetime not null,
                                           city varchar(10) not null,
                                           price decimal(10,2) not null,
                                           stock int not null,
                                           on_shelf tinyint default 0 not null,
                                           sale_start_time datetime not null,
                                           sale_end_time datetime not null,
                                           merchant_id int not null,
                                           deleted tinyint default 0 not null,
                                           event_code_vc varchar(20) generated always as (case when deleted = 0 then event_code else null end),
                                           foreign key (merchant_id) references merchant(id)
);
create unique index if not exists event_table_event_code_vc_uindex on event_table(event_code_vc);

-- 主办方表
create table if not exists organizer (
                                         id int auto_increment primary key,
                                         name varchar(40) not null,
                                         phone_number varchar(20) not null,
                                         address varchar(40) not null,
                                         deleted tinyint default 0 not null,
                                         name_vc varchar(40) generated always as (case when deleted = 0 then name else null end)
);
create unique index if not exists organizer_name_vc_uindex on organizer(name_vc);

-- 主办方-事件关联表
create table if not exists organizer_event (
                                               id int auto_increment primary key,
                                               organizer_id int not null,
                                               event_id int not null,
                                               deleted tinyint default 0 not null,
                                               foreign key (event_id) references event_table(id),
                                               foreign key (organizer_id) references organizer(id)
);

-- 用户表
create table if not exists user_table (
                                          id int auto_increment primary key,
                                          username varchar(50) not null,
                                          phone_number varchar(20) not null,
                                          password_sha256 binary(32) null,
                                          deleted tinyint default 0 not null,
                                          username_vc varchar(50) generated always as (case when deleted = 0 then username else null end),
                                          phone_number_vc varchar(20) generated always as (case when deleted = 0 then phone_number else null end)
);
create unique index if not exists user_table_username_vc_uindex on user_table(username_vc);
create unique index if not exists user_table_phone_number_vc_uindex on user_table(phone_number_vc);

-- 订单表
create table if not exists user_event (
                                          id int auto_increment primary key,
                                          ticket_code varchar(20) not null,
                                          create_time datetime not null,
                                          refunded tinyint default 0 not null,
                                          user_id int not null,
                                          event_id int not null,
                                          deleted tinyint default 0 not null,
                                          ticket_code_vc varchar(20) generated always as (case when deleted = 0 then ticket_code else null end),
                                          foreign key (user_id) references user_table(id),
                                          foreign key (event_id) references event_table(id)
);
create unique index if not exists user_event_ticket_code_vc_uindex on user_event(ticket_code_vc);