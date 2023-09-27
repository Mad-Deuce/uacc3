create table if not exists  public.roles
(
    id      bigint  not null
        constraint roles_pk
            primary key,
    name    varchar not null,
    created timestamp default now(),
    status  varchar   default 'ACTIVE'::character varying,
    updated timestamp default now()
);

alter table public.roles
    owner to postgres;

create unique index roles_name_uindex
    on roles (name);

create unique index roles_id_uindex
    on roles (id);

create table if not exists public.users
(
    id          bigserial
        constraint users_pk
            primary key,
    name        varchar,
    password    varchar,
    subdivision varchar,
    permit_code varchar   default '9999'::character varying,
    created     timestamp default now(),
    status      varchar   default 'ACTIVE'::character varying,
    updated     timestamp default now(),
    role_id     bigint
);

alter table public.users
    owner to postgres;

create unique index users_id_uindex
    on users (id);

create unique index users_name_uindex
    on users (name);

create table if not exists public.users_roles
(
    user_id  bigint,
    roles_id bigint
);

alter table public.users_roles
    owner to postgres;

