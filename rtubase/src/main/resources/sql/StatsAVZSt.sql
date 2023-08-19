SELECT d_obj.name_obj, s_dev.dtype, count(*)
FROM drtu_2023_07_28.dev
         LEFT JOIN drtu_2023_07_28.d_obj
                   ON dev.obj_code = d_obj.id
         LEFT JOIN drtu_2023_07_28.s_dev
                   ON dev.devid = s_dev.id
WHERE (dev.ps = '21')
  AND substring(dev.obj_code, 1, 3) = '111'
  AND dev.devid IN (10100304, 10100301, 10114137, 10100305, 10128264, 10128263, 10100103, 10124627, 10100104, 10100101,
                    10100102, 10100109, 10100105, 10100110, 10100106, 10125439, 10100107, 10100113, 10100111, 10100108,
                    10100112, 10115047, 10100508, 10100503, 10115177, 10100302)
GROUP BY 1, 2;
