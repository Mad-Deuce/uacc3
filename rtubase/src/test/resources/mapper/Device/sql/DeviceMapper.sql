-- noinspection SqlWithoutWhereForFile

delete from drtu_old.dev;
delete from drtu_old.dev_obj;
delete from drtu_old.s_dev;
delete from drtu_old.s_devgrp;
delete from drtu_old.d_rtu;
delete from drtu_old.d_obj;
delete from drtu_old.d_dist;
delete from drtu_old.d_rail;

insert into drtu_old.dev_obj (id, obj_code, locate, nplace, nshem, locate_t, region, region_t, ok_send, opcl, scode, detail)
values (10110010000003, '1011001', '131', '96', 'ПР3  ПХС', 'ST', null, 'EC', '2', '0', 'F', null);

insert into drtu_old.s_dev (id, grid, dtype, mtest, rtime, ttime, narg, ngold, nplat, nalk, name, d_create, plant, scode,
                        tag1, tag2)
values (10100101, 1, 'НР1-400', 120, 2.1, 0.17, 11.92, 0.0, 0.00, 0.0, null, '2007-04-18', null, '+',
        null, null);

insert into drtu_old.s_devgrp (grid, name, opcl)
values (1, 'Реле', '0');

insert into drtu_old.d_rtu (id, id_rail, name, kod_rtu, kod_did)
values ('1011', '1', 'Слов’янськ', 1, 1);

insert into drtu_old.d_obj (kod_dor, kod_otd, kod_dist, kod_rtu, kod_obkt, kod_obj, name_obj, id, kind, cls)
values ('1', null, 1, 1, 1, '001', 'ст. Близнецы', '1011001', 'S', 'ST');

insert into drtu_old.d_dist (id, id_rail, dist, name, code_dist)
values ('101', '1', 'ШЧ-1', 'ШЧ-1 Слов''янськ', 1);

insert into drtu_old.d_rail (id, name, code)
values ('1', 'Донецька', '1');
