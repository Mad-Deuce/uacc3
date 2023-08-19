SELECT  s_dev.dtype,
        line.cnt as "На линии", avzst.cnt as "АВЗ станций", avzrtd.cnt as "АВЗ РТД", obf.cnt as "ОБФ"
FROM drtu_2023_07_28.s_dev
         LEFT JOIN (SELECT dev.devid, count(dev.devid) as cnt
                    FROM drtu_2023_07_28.dev
                    WHERE (dev.ps = '11' or dev.ps = '51' or dev.ps = '52')
                      AND substring(dev.obj_code, 1, 3) = '101'
                    GROUP BY 1) as line
                   ON s_dev.id = line.devid
         LEFT JOIN (SELECT dev.devid, count(dev.devid) as cnt
                    FROM drtu_2023_07_28.dev
                    WHERE (dev.ps = '21')
                      AND substring(dev.obj_code, 1, 3) = '101'
                    GROUP BY 1) as avzst
                   ON s_dev.id = avzst.devid
         LEFT JOIN (SELECT dev.devid, count(dev.devid) as cnt
                    FROM drtu_2023_07_28.dev
                    WHERE (dev.ps = '32')
                      AND substring(dev.obj_code, 1, 3) = '101'
                    GROUP BY 1) as avzrtd
                   ON s_dev.id = avzrtd.devid
         LEFT JOIN (SELECT dev.devid, count(dev.devid) as cnt
                    FROM drtu_2023_07_28.dev
                    WHERE (dev.ps = '31' or dev.ps = '53' or dev.ps = '54' )
                      AND substring(dev.obj_code, 1, 3) = '101'
                    GROUP BY 1) as obf
                   ON s_dev.id = obf.devid

WHERE s_dev.id IN (10100304, 10100301, 10114137, 10100305, 10128264, 10128263, 10100103, 10124627, 10100104, 10100101,
                   10100102, 10100109, 10100105, 10100110, 10100106, 10125439, 10100107, 10100113, 10100111, 10100108,
                   10100112, 10115047, 10100508, 10100503, 10115177, 10100302);

