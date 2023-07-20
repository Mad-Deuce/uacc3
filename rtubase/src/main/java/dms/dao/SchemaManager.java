package dms.dao;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import dms.dto.DeviceDTO;
import dms.utils.CompressUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SchemaManager {

    @PersistenceContext
    private EntityManager em;

    final String RTU_T = "1000";

    @Transactional
    public void createSchema(String schemaName) {
        em.createNativeQuery(
                        "CREATE SCHEMA IF NOT EXISTS " +
                                schemaName +
                                " AUTHORIZATION postgres")
                .executeUpdate();
    }

    @Transactional
    public void renameSchema(String oldName, String newName) {
        em.createNativeQuery(
                        "ALTER SCHEMA " +
                                oldName +
                                " RENAME TO " +
                                newName)
                .executeUpdate();
    }

    @Transactional
    public void removeSchema(String schemaName) {
        em.createNativeQuery(
                        "DROP SCHEMA " +
                                schemaName +
                                " CASCADE")
                .executeUpdate();
    }

    public void restoreEmpty() {
        String command = "pg_restore -U postgres -w -d rtubase " +
                "/vagrant/ansible/roles/postgresql/files/d20200602.backup";

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession("postgres", "localhost", 2222);
            session.setPassword("postgres");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            channel.setOutputStream(responseStream);

            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    log.info(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    log.info("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ignore) {
                }
            }


            while (channel.isConnected()) {
                Thread.sleep(100);
            }

//            String responseString = new String(responseStream.toByteArray());
//            System.out.println(responseString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }


    }

    private List<String> readFileToList(String path) throws IOException {
        List<String> result = Collections.emptyList();

        File file = new File(path);
        log.info("exists: " + file.exists());
        log.info("is dir: " + file.isDirectory());
        log.info("is file: " + file.isFile());
        log.info("can read: " + file.canRead());

        if (file.exists() && file.isFile() && file.canRead()) {
            result = Files.readAllLines(Paths.get(path), Charset.forName("windows-1251"));
        }
        return result;
    }

    @Transactional
    public void receiveDFile() throws Exception {


        String compressedFilePath = "rtubase/src/main/resources/pd_files/d1110714.001";
        String extractedFilePath = "rtubase/src/main/resources/pd_files/d1110714";

        File compressedFile = new File(compressedFilePath);
        if (!compressedFile.exists() || !compressedFile.isFile() || !compressedFile.canRead()) {
            throw new Exception("dms: Compressed D File not accessible");
        }

        CompressUtils.extractGzip(compressedFilePath, extractedFilePath);

        File extractedFile = new File(extractedFilePath);
        if (!extractedFile.exists() || !extractedFile.isFile() || !extractedFile.canRead()) {
            throw new Exception("dms: Extracted D File not accessible");
        }

        List<DeviceDTO> deviceDTOList = new ArrayList<>();
        List<String> strRowList = readFileToList(extractedFilePath);

        String headerRow = strRowList.get(0);
        if (!isFileVersionActual(headerRow.substring(35, 42))) {
            throw new Exception("dms: D File Version is wrong");
        }

        strRowList.forEach(item -> deviceDTOList.add(DeviceDTO.stringify(item)));

        clearDataSourceDevTable(headerRow.substring(1, 5));

        List<DeviceDTO> unreceivedDevList = addDeviceRecord(deviceDTOList);
        log.info("dms: Unreceived Objects count: " + unreceivedDevList.size());

        int stNum = deviceDTOList.size() - 1;
        int stNumT = deviceDTOList.size() - unreceivedDevList.size() - 1;
        if (updateDataSourceDevTransTable(headerRow.substring(0, 12), stNumT) == 0) {
            insertToDataSourceDevTransTable(headerRow.substring(0, 12), stNum, stNumT);
        }
        log.info("END DATE -------" + new Date(System.currentTimeMillis()));
        log.info("END TIME -------" + new Timestamp(System.currentTimeMillis()));

        unreceivedDevList.forEach(item -> {
            log.info("----UN id" + item.getId());
        });

    }

    private int updateDataSourceDevTransTable(String name, int stNumT) {
        return em.createNativeQuery(
                        " update drtu.dev_trans set " +
                                " NAME_T = UPPER (:name_t), " +
                                " STNUM_T = :stnum_t, " +
                                " RTU_T = :rtu_t, " +
                                " DATE_T = :date_t, " +
                                " TIME_T = :time_t " +
                                " where NAME = UPPER (:name) "
                ).unwrap(org.hibernate.query.NativeQuery.class)
                .setParameter("name_t", "t" + name)
                .setParameter("stnum_t", stNumT)
                .setParameter("rtu_t", RTU_T)
                .setParameter("date_t", new Date(System.currentTimeMillis()))
                .setParameter("time_t", new Timestamp(System.currentTimeMillis()))
                .setParameter("name", name)
                .executeUpdate();
    }

    private int insertToDataSourceDevTransTable(String name, int stNum, int stNumT) {
        return em.createNativeQuery(
                        "insert into drtu.dev_trans (name, ftype, ps, stnum, date_create, name_t, stnum_t, rtu_t, date_t, time_t) values ( " +
                                " UPPER (:name), " +
                                " UPPER (:ftype), " +
                                " UPPER (:ps), " +
                                " :stnum, " +
                                " :date_t, " +
                                " UPPER(:name_t), " +
                                " :stnum_t, " +
                                " :rtu_t, " +
                                " :date_t, " +
                                " :time_t ) "

                ).unwrap(org.hibernate.query.NativeQuery.class)
                .setParameter("name", name)
                .setParameter("ftype", name.substring(0, 1))
                .setParameter("ps", "R")
                .setParameter("stnum", stNum)
                .setParameter("date_t", new Date(System.currentTimeMillis()))
                .setParameter("name_t", "t" + name)
                .setParameter("stnum_t", stNumT)
                .setParameter("rtu_t", RTU_T)
                .setParameter("time_t", new Timestamp(System.currentTimeMillis()))
                .executeUpdate();
    }

    private boolean isFileVersionActual(String version) {
        List<?> result = em.createNativeQuery(
                        "select value_c from dock.val where name = 'VERSION'")
                .getResultList();
        return (result.size() > 0 && result.get(0).equals(version));
    }

    private void clearDataSourceDevTable(String objCode) throws Exception {
        if (objCode.length() != 4) throw new Exception("dms: Parameter Length is wrong");
        if (objCode.charAt(3) == '0') {
            em.createNativeQuery(
                            "delete from drtu.dev where SUBSTR(obj_code, 1, 3 ) = :objCode"
                    )
                    .setParameter("objCode", objCode.substring(0, 3))
                    .executeUpdate();
        } else {
            em.createNativeQuery(
                            "delete from drtu.dev where SUBSTR(obj_code, 1, 4) = :objCode"
                    )
                    .setParameter("objCode", objCode)
                    .executeUpdate();
        }

    }

    private boolean isDeviceTypeExists(String typeId) {
        if (typeId == null || typeId.trim().equals("null") || typeId.trim().equals("")) return false;
        return em.createNativeQuery(
                        "select 'X' from drtu.s_dev where id = TO_NUMBER (:typeId, '9999999999')"
                )
                .setParameter("typeId", typeId.trim())
                .getResultList().size() > 0;
    }

    private boolean isLocationFree(String locationId) {
        if (locationId == null || locationId.trim().equals("null") || locationId.trim().equals("")) return true;
        return em.createNativeQuery(
                        "select 'X' from drtu.dev where id_obj = TO_NUMBER(:locationId, '99999999999999')"
                )
                .setParameter("locationId", locationId.trim())
                .getResultList().size() < 1;
    }

    private List<DeviceDTO> addDeviceRecord(List<DeviceDTO> deviceDTOList) {
        List<DeviceDTO> unreceivedDevList = new ArrayList<>();
        deviceDTOList.stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    if (!isDeviceTypeExists(String.valueOf(item.getTypeId()))) {
                        unreceivedDevList.add(item);
                        log.warn("device type not exist");
                    } else if (!isLocationFree(String.valueOf(item.getLocationId()))) {
                        unreceivedDevList.add(item);
                        log.warn("Location not free");
                    } else if (updateDevice(item) < 1) {
                        insertDevice(item);
                    }
                });
        return unreceivedDevList;
    }


    private Integer updateDevice(DeviceDTO deviceDTO) {
        return em.createNativeQuery(
                        "update DRTU.DEV " +
                                "set DEVID    = :devid, " +
                                "    NUM      = :num, " +
                                "    MYEAR    = :myear, " +
                                "    D_TKIP   = :dtkip, " +
                                "    D_NKIP   = :dnkip, " +
                                "    T_ZAM    = :tzam, " +
                                "    ID_OBJ   = :idobj, " +
                                "    OBJ_CODE = :objcode, " +
                                "    PS       = :ps, " +
                                "    OPCL     = :opcl, " +
                                "    TID_PR   = :tidpr, " +
                                "    TID_RG   = :tidrg, " +
                                "    SCODE    = :scode " +
                                "where ID = :id  "
                ).unwrap(org.hibernate.query.NativeQuery.class)
                .setParameter("devid", deviceDTO.getTypeId())
                .setParameter("num", deviceDTO.getNumber())
                .setParameter("myear", deviceDTO.getReleaseYear())
                .setParameter("dtkip", deviceDTO.getTestDate(), DateType.INSTANCE)
                .setParameter("dnkip", deviceDTO.getNextTestDate(), DateType.INSTANCE)
                .setParameter("tzam", deviceDTO.getReplacementPeriod())
                .setParameter("idobj", deviceDTO.getLocationId(), LongType.INSTANCE)
                .setParameter("objcode", deviceDTO.getFacilityId())
                .setParameter("ps", deviceDTO.getStatus())
                .setParameter("opcl", deviceDTO.getOpcl())
                .setParameter("tidpr", deviceDTO.getTid_pr())
                .setParameter("tidrg", deviceDTO.getTid_rg())
                .setParameter("scode", deviceDTO.getScode())
                .setParameter("id", deviceDTO.getId())
                .executeUpdate();
    }

    private Integer insertDevice(DeviceDTO deviceDTO) {
        return em.createNativeQuery(
                        "insert into DRTU.DEV (ID, DEVID, NUM, MYEAR, D_TKIP, D_NKIP, T_ZAM, ID_OBJ, " +
                                "OBJ_CODE, PS, OPCL, TID_PR, TID_RG, SCODE) " +
                                "values ( " +
                                "   :id, " +
                                "   :devid, " +
                                "   :num, " +
                                "   :myear, " +
                                "   :dtkip, " +
                                "   :dnkip, " +
                                "   :tzam, " +
                                "   :idobj, " +
                                "   :objcode, " +
                                "   :ps, " +
                                "   :opcl, " +
                                "   :tidpr, " +
                                "   :tidrg, " +
                                "   :scode " +
                                ")"
                ).unwrap(org.hibernate.query.NativeQuery.class)
                .setParameter("devid", deviceDTO.getTypeId())
                .setParameter("num", deviceDTO.getNumber())
                .setParameter("myear", deviceDTO.getReleaseYear())
                .setParameter("dtkip", deviceDTO.getTestDate(), DateType.INSTANCE)
                .setParameter("dnkip", deviceDTO.getNextTestDate(), DateType.INSTANCE)
                .setParameter("tzam", deviceDTO.getReplacementPeriod())
                .setParameter("idobj", deviceDTO.getLocationId(), LongType.INSTANCE)
                .setParameter("objcode", deviceDTO.getFacilityId())
                .setParameter("ps", deviceDTO.getStatus())
                .setParameter("opcl", deviceDTO.getOpcl())
                .setParameter("tidpr", deviceDTO.getTid_pr())
                .setParameter("tidrg", deviceDTO.getTid_rg())
                .setParameter("scode", deviceDTO.getScode())
                .setParameter("id", deviceDTO.getId())
                .executeUpdate();


    }


}
