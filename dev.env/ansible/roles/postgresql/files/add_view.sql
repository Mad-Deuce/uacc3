
create or replace view rtubase.drtu.v$devices_main
            (id, type_id, type_name, type_group_id, type_group_name, number, release_year, test_date, next_test_date,
             extra_next_test_date, replacement_period, status, detail, railway_id, railway_name, subdivision_id,
             subdivision_short_name, rtd_id, rtd_name, facility_id, facility_name, location_id, label, region,
             region_type, locate, locate_type, place_number, location_detail)
as
SELECT dev.id,
       s_dev.id                                       AS type_id,
       s_dev.dtype                                    AS type_name,
       s_devgrp.grid                                  AS type_group_id,
       s_devgrp.name                                  AS type_group_name,
       dev.num                                        AS number,
       dev.myear                                      AS release_year,
       dev.d_tkip                                     AS test_date,
       dev.d_nkip                                     AS next_test_date,
       drtu.zam10(dev.d_nkip, dev.t_zam, '1'::"char") AS extra_next_test_date,
       dev.t_zam                                      AS replacement_period,
       dev.ps                                         AS status,
       dev.detail,
       d_rail.id                                      AS railway_id,
       d_rail.name                                    AS railway_name,
       d_dist.id                                      AS subdivision_id,
       d_dist.name                                    AS subdivision_short_name,
       d_rtu.id                                       AS rtd_id,
       d_rtu.name                                     AS rtd_name,
       d_obj.id                                       AS facility_id,
       d_obj.name_obj                                 AS facility_name,
       dev_obj.id                                     AS location_id,
       dev_obj.nshem                                  AS label,
       dev_obj.region,
       dev_obj.region_t                               AS region_type,
       dev_obj.locate,
       dev_obj.locate_t                               AS locate_type,
       dev_obj.nplace                                 AS place_number,
       dev_obj.detail                                 AS location_detail
FROM drtu.dev
         LEFT JOIN drtu.d_obj ON dev.obj_code::text = d_obj.id::text
         LEFT JOIN drtu.d_rtu ON "substring"(dev.obj_code::text, 1, 4) = d_rtu.id::text
         LEFT JOIN drtu.d_dist ON "substring"(dev.obj_code::text, 1, 3) = d_dist.id::text
         LEFT JOIN drtu.d_rail ON "substring"(dev.obj_code::text, 1, 1) = d_rail.id::text
         LEFT JOIN drtu.s_dev ON dev.devid = s_dev.id
         LEFT JOIN drtu.s_devgrp ON s_dev.grid = s_devgrp.grid
         LEFT JOIN drtu.dev_obj ON dev.id_obj = dev_obj.id;
;

alter table rtubase.drtu.v$devices_main
    owner to postgres;
