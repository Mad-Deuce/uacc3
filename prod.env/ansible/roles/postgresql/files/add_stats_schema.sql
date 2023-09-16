create schema IF NOT EXISTS _stats;
alter schema _stats owner to postgres;

create table if not exists overdue_devs_stats
(
    object_id                  varchar not null,
    stats_date                 date    not null,
    norm_devs_quantity         integer default 0,
    pass_devs_quantity         integer default 0,
    exp_devs_quantity          integer default 0,
    exp_warranty_devs_quantity integer default 0,
    object_name                varchar
);

comment on column overdue_devs_stats.norm_devs_quantity is 'Normal Devices Quantity';

comment on column overdue_devs_stats.pass_devs_quantity is 'Passive Devices Quantity';

comment on column overdue_devs_stats.exp_devs_quantity is 'Expired Devices Quantity';

comment on column overdue_devs_stats.exp_warranty_devs_quantity is 'Expired Warranty Devices Quantity';

alter table overdue_devs_stats
    owner to postgres;

