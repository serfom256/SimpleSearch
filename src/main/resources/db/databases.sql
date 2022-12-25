drop table if exists data;
create table if not exists data
(
    id   int primary key not null auto_increment,
    data blob,
    idx  varchar(1000) CHARACTER SET utf8mb4
);

drop table if exists search_config;
create table if not exists search_config
(
    name     varchar(100) primary key,
    property varchar(100)
);

create table if not exists session
(
    uuid        varchar(40) primary key,
    status      varchar(20),
    total       int,
    indexed     int,
    duration    timestamp not null
);
drop table if exists session;

insert into search_config
values ('simplesearch.shards.used', '1');
