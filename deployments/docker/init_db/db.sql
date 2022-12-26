create database simplesearch;
use simplesearch;

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

drop table if exists sessions;
create table if not exists sessions
(
    uuid     varchar(40) primary key,
    status   varchar(20),
    total    int,
    indexed  int,
    duration timestamp not null
);
