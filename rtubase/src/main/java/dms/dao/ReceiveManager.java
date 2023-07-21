package dms.dao;

import dms.dto.DeviceDTO;
import dms.utils.CompressUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component
public class ReceiveManager {

    @PersistenceContext
    private EntityManager em;

    final String RTU_T = "1000";
    final String DIR_PATH = "rtubase/src/main/resources/pd_files";

    StringBuilder logStr = new StringBuilder("\n");

    @Transactional
    public void receiveDFile() throws Exception {

        logStr.append("dms: Start receiving at: ")
                .append(new Timestamp(System.currentTimeMillis()))
                .append("\n");

        List<File> compressedFiles = getFileList();
        //Extracting files
        List<File> extractedFiles = new ArrayList<>();
        compressedFiles.forEach(item -> {
            try {
                extractedFiles.add(CompressUtils.extractGzip(item));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        extractedFiles.forEach(item -> {
            try {
                PDFile pdFile = new PDFile(readFileToList(item.getPath()));
                if (isFileVersionActual(pdFile.getMetaData().getVersion())) {
                    log.info("version good");
                    clearDataSourceDevTable(pdFile.getMetaData().getObjectCode());

                    Timestamp startTs = new Timestamp(System.currentTimeMillis());
//                    pdFile.getSpecificContent().forEach(deviceDTO -> {
//                        log.info("1----" + new Timestamp(System.currentTimeMillis()));
                    isDeviceTypeExists(pdFile);
//                        log.info("2----" + new Timestamp(System.currentTimeMillis()));
//                    });
                    Timestamp endS = new Timestamp(System.currentTimeMillis());
                    log.info("1----" + startTs);
                    log.info("2----" + endS);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


//        List<DeviceDTO> unreceivedDevList = addDeviceRecord(deviceDTOList);
//        log.info("dms: Unreceived Objects count: " + unreceivedDevList.size());
//        logStr.append("dms: Unreceived Objects count: ")
//                .append(unreceivedDevList.size())
//                .append("\n");
//
//        int stNum = deviceDTOList.size() - 1;
//        int stNumT = deviceDTOList.size() - unreceivedDevList.size() - 1;
//        if (updateDataSourceDevTransTable(headerRow.substring(0, 12), stNumT) == 0) {
//            insertToDataSourceDevTransTable(headerRow.substring(0, 12), stNum, stNumT);
//        }
//        log.info("END DATE -------" + new Date(System.currentTimeMillis()));
//        log.info("END TIME -------" + new Timestamp(System.currentTimeMillis()));
//
//        logStr.append("dms: End Receiving: ")
//                .append(new Timestamp(System.currentTimeMillis()))
//                .append("\n");
//
//        log.info(logStr.toString());
    }

    private List<File> getFileList() throws Exception {
        FileFilter filter = f -> (f.isFile()
                && f.getName().length() == 12
                && f.getName().matches("[pd]\\d{4}\\w\\d{2}\\.\\d{3}"));
        String errorMessage = "dms: Directory for P,D Files not accessible";
        File dir = new File(DIR_PATH);
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
            logStr.append("Error: ")
                    .append(errorMessage)
                    .append("\n");
            log.error(logStr.toString());
            throw new Exception(errorMessage);
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles(filter))).toList();
    }

    private List<String> readFileToList(String path) throws IOException {
        List<String> result = Collections.emptyList();
        File file = new File(path);
        if (file.exists() && file.isFile() && file.canRead()) {
            result = Files.readAllLines(Paths.get(path), Charset.forName("windows-1251"));
        }
        return result;
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

    private boolean isDeviceTypeExists(String typeId) {
        if (typeId == null || typeId.trim().equals("null") || typeId.trim().equals("")) return false;
        return em.createNativeQuery(
                        "select 'X' from drtu.s_dev where id = TO_NUMBER (:typeId, '9999999999')"

                )
                .setParameter("typeId", typeId.trim())
                .getResultList().size() > 0;
    }

    private void isDeviceTypeExists(PDFile pdFile) {
        Query query = em.createNativeQuery("select id from drtu.s_dev");

        List typeIdList = query
                .setHint("org.hibernate.fetchSize", "2000")
                .getResultList();

        HashSet hs = new HashSet(typeIdList);
        pdFile.getSpecificContent().forEach(item -> {
            if (!hs.contains(BigDecimal.valueOf(item.getTypeId()))) log.error("fffffffffffffffffff");
        });
        log.warn("device type not exist");
    }

    private boolean isLocationFree(String locationId) {
        if (locationId == null || locationId.trim().equals("null") || locationId.trim().equals("")) return true;
        return em.createNativeQuery(
                        "select 'X' from drtu.dev where id_obj = TO_NUMBER(:locationId, '99999999999999')"
                )
                .setParameter("locationId", locationId.trim())
                .getResultList().size() < 1;
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
}
