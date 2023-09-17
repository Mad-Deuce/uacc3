INSERT INTO roles(id, name, status)
VALUES (1, 'ROLE_ADMIN', 'ACTIVE'),
 (2, 'ROLE_VIEWER', 'ACTIVE'),
 (3, 'ROLE_OPERATOR', 'ACTIVE');

INSERT INTO users(id, name, password, subdivision,permit_code,role_id)
VALUES (1, 'admin', '$2a$10$jvqf8vDKFzd3ebz4z7Y0tuwGNcLhfsRAc.LvUJJJzyGnPM6qLWjZC',null,'',1);

INSERT INTO users_roles(user_id, roles_id)
VALUES (1, 1);



