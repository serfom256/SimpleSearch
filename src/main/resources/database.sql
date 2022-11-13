drop table if exists serialized;
create table serialized
(
    id   int primary key not null auto_increment,
    data blob
);


drop table if exists search_config;
create table search_config
(
    id         serial primary key,
    param_name varchar(100),
    param_val  varchar(100)
);
