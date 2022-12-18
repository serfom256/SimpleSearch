create table if not exists data
(
    id   int primary key not null auto_increment,
    data blob,
    idx  varchar(1000)
);

create table if not exists search_config
(
    id       serial primary key,
    name     varchar(100),
    property varchar(100)
);
