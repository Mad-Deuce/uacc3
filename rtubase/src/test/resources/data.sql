insert into public.users (id, name, password, subdivision, permit_code)
VALUES (1, 'admin', '$2a$10$jvqf8vDKFzd3ebz4z7Y0tuwGNcLhfsRAc.LvUJJJzyGnPM6qLWjZC', '', '');
insert into public.users (id, name, password, subdivision, permit_code)
VALUES (2, 'user_viewer', '$2a$10$jvqf8vDKFzd3ebz4z7Y0tuwGNcLhfsRAc.LvUJJJzyGnPM6qLWjZC', '101', '1011');
insert into public.users (id, name, password, subdivision, permit_code)
VALUES (3, 'user_operator', '$2a$10$jvqf8vDKFzd3ebz4z7Y0tuwGNcLhfsRAc.LvUJJJzyGnPM6qLWjZC', '101', '1011');

insert into public.roles(id, name)
VALUES (1, 'ROLE_ADMIN');
insert into public.roles(id, name)
VALUES (2, 'ROLE_VIEWER');
insert into public.roles(id, name)
VALUES (3, 'ROLE_OPERATOR');

insert into public.users_roles(user_id, roles_id)
VALUES (1,1);
insert into public.users_roles(user_id, roles_id)
VALUES (2,2);
insert into public.users_roles(user_id, roles_id)
VALUES (3,3);

insert into drtu.dev (id_obj, devid, num, myear, ps, d_create, id, d_nkip, d_tkip, t_zam, obj_code,
                      ok_send, opcl, tid_pr, tid_rg, scode, detail)
values (null, 10708310, '00000001', '1987', 31, null, 100001, '2023-12-22', '2017-12-22', 60, '1011',
        null, null, null, null, null, 'qwerty');
insert into drtu.dev (id_obj, devid, num, myear, ps, d_create, id, d_nkip, d_tkip, t_zam, obj_code,
                      ok_send, opcl, tid_pr, tid_rg, scode, detail)
values (null, 10102201, '00000002', '1986', 31, null, 100002, '2027-01-01', '2023-01-01', 180, '1011',
        null, null, null, null, null, 'qwerty2');
insert into drtu.dev (id_obj, devid, num, myear, ps, d_create, id, d_nkip, d_tkip, t_zam, obj_code,
                      ok_send, opcl, tid_pr, tid_rg, scode, detail)
values (10110230001100, 10708310, '16022', '1980', 11, null, 1011004615, '2092-12-22', '2017-12-22', 900, '1011023',
        null, null, null, null, null, null);

insert into drtu.dev (id_obj, devid, num, myear, ps, d_create, id, d_nkip, d_tkip, t_zam, obj_code,
                      ok_send, opcl, tid_pr, tid_rg, scode, detail)
values (10110000000001, 10708310, '00000001', '1980', 11, null, 100004, '2092-12-22', '2017-12-22', 900, '1011023',
        null, null, null, null, null, null);
insert into drtu.dev (id_obj, devid, num, myear, ps, d_create, id, d_nkip, d_tkip, t_zam, obj_code,
                      ok_send, opcl, tid_pr, tid_rg, scode, detail)
values (null, 10708310, '00000002', '1987', 31, null, 100005, '2023-12-22', '2018-12-22', 60, '1011',
        null, null, null, null, null, 'qwerty');


insert into drtu.dev_obj (id, obj_code, locate, nplace, nshem, locate_t, region, region_t, ok_send, opcl, scode, detail)
values (10110230001100, '1011023', 'М7', 'К', 'К', 'TR', null, 'NU', null, null, null, null);
insert into drtu.dev_obj (id, obj_code, locate, nplace, nshem, locate_t, region, region_t, ok_send, opcl, scode, detail)
values (10110000000001, '1011023', 'М7', 'К', 'К', 'TR', null, 'NU', null, null, null, null);


insert into drtu.s_dev (id, grid, dtype, mtest, rtime, ttime, narg, ngold, nplat, nalk, name, d_create, plant, scode,
                        tag1, tag2)
values (10708310, 7, 'СТ-4', 600, 0.780, 0.260, 0.000, 0.000, 0.000, 0.000, null, '2007-04-18', null, '+',
        null, null);
insert into drtu.s_dev (id, grid, dtype, mtest, rtime, ttime, narg, ngold, nplat, nalk, name, d_create, plant, scode,
                        tag1, tag2)
values (10102201, 1, 'НМШ2-900', 180, 1.650, 0.200, 2.222, 0.000, 0.000, 0.000, null, '2007-04-18', null, '+',
        null, null);

insert into drtu.s_devgrp (grid, name, opcl)
values (7, 'Трансформатори, реактори', '0');
insert into drtu.s_devgrp (grid, name, opcl)
values (1, 'Реле', '0');

insert into drtu.d_rtu (id, id_rail, name, kod_rtu, kod_did)
values ('1011', '1', 'Слов’янськ', 1, 1);

insert into drtu.d_obj (kod_dor, kod_otd, kod_dist, kod_rtu, kod_obkt, kod_obj, name_obj, id, kind, cls)
values ('1', null, 1, 1, 23, '023', 'ст. Фенольная', '1011023', 'S', 'ST');

insert into drtu.d_dist (id, id_rail, dist, name, code_dist)
values ('101', '1', 'ШЧ-1', 'ШЧ-1 Слов''янськ', 1);

insert into drtu.d_rail (id, name, code)
values ('1', 'Донецька', '1');
