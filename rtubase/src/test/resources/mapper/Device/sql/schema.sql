CREATE SCHEMA IF NOT EXISTS drtu;

create table IF NOT EXISTS drtu_old.d_dist
(
    id        varchar(5) not null
        constraint d_dist_pkey
            primary key,
    id_rail   char,
    dist      varchar(8),
    name      varchar(40),
    code_dist numeric(2)
);

create table IF NOT EXISTS drtu_old.d_obj
(
    kod_dor  char       not null,
    kod_otd  char,
    kod_dist numeric(2) not null,
    kod_rtu  numeric(1) not null,
    kod_obkt numeric(3) not null,
    kod_obj  varchar(3),
    name_obj varchar(50),
    id       varchar(7) not null
        constraint d_obj_p
            primary key,
    kind     char       default 'S'::character varying,
    cls      varchar(2) default 'ST'::character varying,
    constraint d_obj_u
        unique (kod_dor, kod_dist, kod_rtu, kod_obkt)
);

create index IF NOT EXISTS d_obj_ind_cls
    on drtu_old.d_obj (cls);

create index IF NOT EXISTS d_obj_ind_kind
    on drtu_old.d_obj (kind);

create table IF NOT EXISTS drtu_old.d_rail
(
    id   char not null
        constraint d_rail_prima
            primary key,
    name varchar(40),
    code char
);

create table IF NOT EXISTS drtu_old.d_rtu
(
    id      varchar(5) not null
        constraint d_rtu_pkey
            primary key,
    id_rail char,
    name    varchar(40),
    kod_rtu numeric(1),
    kod_did numeric(2)
);

create table IF NOT EXISTS drtu_old.s_locate
(
    id     varchar(2) not null
        constraint s_locate_pkey
            primary key,
    locate varchar(7) default '_______'::character varying,
    name   varchar(50)
);

create table IF NOT EXISTS drtu_old.dev
(
    id_obj   numeric(14),
    devid    numeric(10),
    num      varchar(10),
    myear    varchar(4),
    ps       char(2) default '00'::character varying,
    d_create date,
    id       numeric(10) not null
        constraint dev_p
            primary key,
    d_nkip   date,
    d_tkip   date,
    t_zam    numeric(5),
    obj_code varchar(10),
    ok_send  char    default '0'::character varying,
    opcl     char    default '0'::character varying,
    tid_pr   varchar(4),
    tid_rg   varchar(4),
    scode    char    default 'N'::character varying,
    detail   varchar(160)
);

create index IF NOT EXISTS dev_i_d_nkip
    on drtu_old.dev (d_nkip);

create index IF NOT EXISTS dev_i_obj_code
    on drtu_old.dev (obj_code);

create index IF NOT EXISTS dev_ind_d_tkip
    on drtu_old.dev (d_tkip);

create index IF NOT EXISTS dev_ind_devid
    on drtu_old.dev (devid);

create unique index IF NOT EXISTS dev_ind_id_obj
    on drtu_old.dev (id_obj);

create index IF NOT EXISTS dev_ind_num
    on drtu_old.dev (num);

create index IF NOT EXISTS dev_ind_oksend
    on drtu_old.dev (ok_send);

create table IF NOT EXISTS drtu_old.dev_obj
(
    id       numeric(14) not null
        constraint dev_obj_p
            primary key,
    obj_code varchar(10),
    locate   varchar(50),
    nplace   varchar(4),
    nshem    varchar(50),
    locate_t char(2) default 'PR'::character varying,
    region   varchar(50),
    region_t char(2) default 'ZZ'::character varying,
    ok_send  char    default '0'::character varying,
    opcl     char    default '0'::character varying,
    scode    char    default 'N'::character varying,
    detail   varchar(160)
);

create index IF NOT EXISTS dev_obj_i_obj_code
    on drtu_old.dev_obj (obj_code);

create index IF NOT EXISTS dev_obj_ind_nplace
    on drtu_old.dev_obj (region, locate, nplace);

create index IF NOT EXISTS dev_obj_ind_oksend
    on drtu_old.dev_obj (ok_send);



create table IF NOT EXISTS drtu_old.dev_test
(
    cnt      numeric(10),
    id       numeric(10),
    devid    numeric(10),
    num      varchar(10),
    dtype    varchar(20),
    num_p    varchar(10),
    dtype_p  varchar(20),
    obj_code varchar(10),
    locate   varchar(50),
    nplace   varchar(4),
    erc      char(2)
);

create table IF NOT EXISTS drtu_old.dev_trail
(
    id       numeric(10) not null
        constraint dev_trail_pkey
            primary key,
    iddev    numeric(10),
    d_trail  date,
    id_obj   numeric(14),
    docnum   varchar(20),
    textid   varchar(2),
    text     varchar(160),
    ps       char(2),
    obj_code varchar(10)
);

create table IF NOT EXISTS drtu_old.dev_trans
(
    name        char(12) not null
        constraint dev_trans_pkey
            primary key,
    ftype       char,
    ps          char,
    stnum       numeric(6),
    date_create date,
    name_t      char(13),
    stnum_t     numeric(6),
    rtu_t       char(4),
    date_t      date,
    time_t      timestamp
);

create table IF NOT EXISTS drtu_old.dev_zam
(
    id          numeric(10) not null
        constraint dev_zam_pkey
            primary key,
    obj_code    varchar(10),
    d_zam       date,
    id_obj      numeric(14),
    iddev_o     numeric(10),
    iddev_n     numeric(10),
    date_create date
);

create index IF NOT EXISTS ind_dev_zam
    on drtu_old.dev_zam (obj_code);

create table IF NOT EXISTS drtu_old.pers
(
    num      varchar(4)  not null,
    pos      char(2),
    name     varchar(80),
    obj_code varchar(10) not null,
    constraint pers_p
        primary key (obj_code, num)
);

create table IF NOT EXISTS drtu_old.pribory
(
    metka      varchar(5),
    kod_dor    varchar(5),
    kod_otd    varchar(5),
    kod_dist   varchar(5),
    kod_rtu    varchar(5),
    kod_obkt   varchar(5),
    tip_pr     varchar(50),
    nom_pr     varchar(50),
    dt_vv      varchar(50),
    kod_obj    varchar(50),
    stativ     varchar(50),
    nom_mest   varchar(50),
    naznach    varchar(50),
    t_zam      varchar(50),
    d_sl_prov  varchar(50),
    d_ps_prov  varchar(50),
    tab_nom_rg varchar(50),
    tab_nom_pm varchar(50),
    dt_ps_izm  varchar(50),
    ps         varchar(2),
    obj_code   varchar(7),
    id_rtu     varchar(4)
);

create index IF NOT EXISTS ind_pribory_id_rtu
    on drtu_old.pribory (id_rtu);

create index IF NOT EXISTS ind_pribory_obj_code
    on drtu_old.pribory (obj_code);

create table IF NOT EXISTS drtu_old.s_calendar
(
    myear varchar(4)
);

create table IF NOT EXISTS drtu_old.s_dev
(
    id       numeric(10) not null
        constraint s_dev_pkey
            primary key,
    grid     numeric(10),
    dtype    varchar(20),
    mtest    numeric(3)    default 0,
    rtime    numeric(8, 3) default 0,
    ttime    numeric(8, 3) default 0,
    narg     numeric(8, 4) default 0,
    ngold    numeric(8, 4) default 0,
    nplat    numeric(8, 4) default 0,
    nalk     numeric(8, 4) default 0,
    name     varchar(160),
    d_create date,
    plant    varchar(160),
    scode    char          default '-'::character varying,
    tag1     varchar(160),
    tag2     varchar(160)
);

create index IF NOT EXISTS s_dev_ind_scode
    on drtu_old.s_dev (scode);

create index IF NOT EXISTS s_dev_ind_type
    on drtu_old.s_dev (dtype);

create table IF NOT EXISTS drtu_old.s_dev_new
(
    id       numeric(10) not null
        constraint s_dev_new_pkey
            primary key,
    grid     numeric(10),
    dtype    varchar(20),
    mtest    numeric(3)    default 0,
    rtime    numeric(8, 3) default 0,
    ttime    numeric(8, 3) default 0,
    narg     numeric(8, 4) default 0,
    ngold    numeric(8, 4) default 0,
    nplat    numeric(8, 4) default 0,
    nalk     numeric(8, 4) default 0,
    name     varchar(160),
    d_create date,
    plant    varchar(160)
);

create table IF NOT EXISTS drtu_old.s_devgrp
(
    grid numeric(10) not null
        constraint s_devgrp_pkey
            primary key,
    name varchar(160),
    opcl char default 'O'::character varying
);

create sequence IF NOT EXISTS drtu_old.seq_devid
    start with 300000
    maxvalue 999999;

