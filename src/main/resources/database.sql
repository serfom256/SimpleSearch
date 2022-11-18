drop table if exists data;
create table data
(
    id   int primary key not null auto_increment,
    data blob,
    idx  varchar(1000)
);


drop table if exists search_config;
create table search_config
(
    id         serial primary key,
    param_name varchar(100),
    param_val  varchar(100)
);
