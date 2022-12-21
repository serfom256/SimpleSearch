create table if not exists data
(
    id   int primary key not null auto_increment,
    data blob,
    idx  varchar(1000) CHARACTER SET utf8mb4
);

create table if not exists search_config
(
    id       serial primary key,
    name     varchar(100),
    property varchar(100)
);

create table if not exists session
(
    uuid varchar(40) primary key,
    status varchar(10),
    create_time time
);

