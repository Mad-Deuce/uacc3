-- noinspection SqlWithoutWhereForFile

delete from drtu_old.dev;
delete from drtu_old.dev_obj;
delete from drtu_old.s_dev;
delete from drtu_old.s_devgrp;
delete from drtu_old.d_rtu;
delete from drtu_old.d_obj;
delete from drtu_old.d_dist;
delete from drtu_old.d_rail;


insert into drtu_old.dev (id_obj, devid, num, myear, ps, d_create, id, d_nkip, d_tkip, t_zam, obj_code,
                      ok_send, opcl, tid_pr, tid_rg, scode, detail)
values (null, 10708310, '00000001', '1987', 31, null, 100001, '2023-12-22', '2017-12-22', 60, '1011',
        null, null, null, null, null, 'qwerty');

insert into drtu_old.s_dev (id, grid, dtype, mtest, rtime, ttime, narg, ngold, nplat, nalk, name, d_create, plant, scode,
                        tag1, tag2)
values (10708310, 7, 'СТ-4', 600, 0.780, 0.260, 0.000, 0.000, 0.000, 0.000, null, '2007-04-18', null, '+',
        null, null);

insert into drtu_old.s_devgrp (grid, name, opcl)
values (7, 'Трансформатори, реактори', '0');

insert into drtu_old.d_rtu (id, id_rail, name, kod_rtu, kod_did)
values ('1011', '1', 'Слов’янськ', 1, 1);

insert into drtu_old.d_dist (id, id_rail, dist, name, code_dist)
values ('101', '1', 'ШЧ-1', 'ШЧ-1 Слов''янськ', 1);

insert into drtu_old.d_rail (id, name, code)
values ('1', 'Донецька', '1');
