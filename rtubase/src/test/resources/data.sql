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
