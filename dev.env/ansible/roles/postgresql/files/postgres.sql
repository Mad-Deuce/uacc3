ALTER USER postgres WITH PASSWORD 'rtub';

CREATE DATABASE rtubase WITH OWNER=postgres
                              LC_COLLATE='en_US.utf8'
                              LC_CTYPE='en_US.utf8'
                              ENCODING='UTF8'
                              TEMPLATE=template0;

-- RESTORE DATABASE rtubase
-- FROM disk='/vagrant/ansible/roles/postgresql/files/d20200602.backup';
