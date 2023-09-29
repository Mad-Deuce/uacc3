CREATE OR REPLACE VIEW rtubase.drtu.v$devices_main
AS
SELECT dev.id           AS id,
       s_dev.id         AS type_id,
       s_dev.dtype      AS type_name,
       s_devgrp.grid    AS type_group_id,
       s_devgrp.name    AS type_group_name,
       dev.num          AS number,
       dev.myear        AS release_year,
       dev.d_tkip       AS test_date,
       dev.d_nkip       AS next_test_date,
       dev.t_zam        AS replacement_period,
       dev.ps           AS status,
       dev.detail       AS detail,
       d_rail.id        AS railway_id,
       d_rail.name      AS railway_name,
       d_dist.id        AS subdivision_id,
       d_dist.name      AS subdivision_short_name,
       d_rtu.id         AS rtd_id,
       d_rtu.name       AS rtd_name,
       d_obj.id         AS facility_id,
       d_obj.name_obj   AS facility_name,
       dev_obj.id       AS location_id,
       dev_obj.nshem    AS label,
       dev_obj.region   AS region,
       dev_obj.region_t AS region_type,
       dev_obj.locate   AS locate,
       dev_obj.locate_t AS locate_type,
       dev_obj.nplace   AS place_number,
       dev_obj.detail   AS location_detail
FROM drtu.dev
         LEFT JOIN drtu.d_obj ON dev.obj_code::text = d_obj.id::text
         LEFT JOIN drtu.d_rtu ON substring(dev.obj_code::text, 1, 4) = d_rtu.id::text
         LEFT JOIN drtu.d_dist ON substring(dev.obj_code::text, 1, 3) = d_dist.id::text
         LEFT JOIN drtu.d_rail ON substring(dev.obj_code::text, 1, 1) = d_rail.id::text
         LEFT JOIN drtu.s_dev ON dev.devid = s_dev.id
         LEFT JOIN drtu.s_devgrp ON s_dev.grid = s_devgrp.grid
         LEFT JOIN drtu.dev_obj ON dev.id_obj = dev_obj.id;

ALTER TABLE rtubase.drtu.v$devices_main
    OWNER TO postgres;
