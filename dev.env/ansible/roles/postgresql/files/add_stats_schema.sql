create schema IF NOT EXISTS _stats;
alter schema _stats owner to postgres;

create table if not exists _stats.overdue_devs_stats
(
    object_id                  varchar not null,
    stats_date                 date    not null,
    norm_devs_quantity         integer default 0,
    pass_devs_quantity         integer default 0,
    exp_devs_quantity          integer default 0,
    exp_warranty_devs_quantity integer default 0,
    object_name                varchar
);

alter table _stats.overdue_devs_stats
    owner to postgres;

