package dms.dao;

import dms.dto.DeviceDTO;
import dms.utils.CompressUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.PreparedStatement;
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
    public void receivePDFile() throws Exception {

        logStr = new StringBuilder("\n");
        logStr.append("dms: Start receiving at: ")
                .append(new Timestamp(System.currentTimeMillis()))
                .append("\n");

        List<File> compressedFiles = getFileList();

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
                    if (pdFile.getMetaData().getType().equals("D")) {
                        clearDevTable(pdFile.getMetaData().getObjectCode());
                        isDeviceTypeExists(pdFile);
                        isLocationFree(pdFile);
                        upsertDevice(pdFile);
                        upsertDevTrans(pdFile);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        logStr.append("dms: End Receiving: ")
                .append(new Timestamp(System.currentTimeMillis()))
                .append("\n");
        log.info(logStr.toString());
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

    private void clearDevTable(String objCode) throws Exception {
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

    private void isDeviceTypeExists(PDFile pdFile) {
        List<DeviceDTO> removingItems = new ArrayList<>();
        List typeIdList = em.createNativeQuery("select id from drtu.s_dev")
                .setHint("org.hibernate.fetchSize", "2000")
                .getResultList();
        HashSet hs = new HashSet(typeIdList);
        pdFile.getSpecificContent().forEach(item -> {
            if (!hs.contains(BigDecimal.valueOf(item.getTypeId()))) {
                removingItems.add(item);
                logStr
                        .append("dms/error: File=")
                        .append(pdFile.getMetaData().getName())
                        .append("---")
                        .append("Type=")
                        .append(item.getTypeName())
                        .append(" not exists in DRTU.S_DEV for device id=")
                        .append(item.getId())
                        .append("---")
                        .append(new Timestamp(System.currentTimeMillis()))
                        .append("\n");
            }
        });
        pdFile.increaseNotProcessedRecordsQuantity(removingItems.size());
        removingItems.forEach(item -> pdFile.getSpecificContent().remove(item));
    }

    private void isLocationFree(PDFile pdFile) {
        List<DeviceDTO> removingItems = new ArrayList<>();
        List idObjList = em.createNativeQuery("select id_obj from drtu.dev where id_obj is not null")
                .setHint("org.hibernate.fetchSize", "2000")
                .getResultList();
        HashSet hs = new HashSet(idObjList);
        pdFile.getSpecificContent().forEach(item -> {
            if (item.getLocationId() != null && hs.contains(BigDecimal.valueOf(item.getLocationId()))) {
                removingItems.add(item);
                logStr
                        .append("dms/error: File=")
                        .append(pdFile.getMetaData().getName())
                        .append("---")
                        .append("Location with Id=")
                        .append(item.getLocationId())
                        .append(" not free for device id=")
                        .append(item.getId())
                        .append("---")
                        .append(new Timestamp(System.currentTimeMillis()))
                        .append("\n");
            }
        });
        pdFile.increaseNotProcessedRecordsQuantity(removingItems.size());
        removingItems.forEach(item -> pdFile.getSpecificContent().remove(item));
    }

    private void upsertDevice(PDFile pdFile) {
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            PreparedStatement pstmt = null;
            try {
                String sqlInsert = "insert into DRTU.DEV ( " +
                        " ID, DEVID, NUM, MYEAR, D_TKIP, " +
                        " D_NKIP, T_ZAM, ID_OBJ, OBJ_CODE, PS, " +
                        " OPCL, TID_PR, TID_RG, SCODE " +
                        " ) " +
                        " values (?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?) " +
                        "on conflict (ID) do  update set " +
                        "    DEVID    = ?, "+
                        "    NUM      = ?, " +
                        "    MYEAR    = ?, " +
                        "    D_TKIP   = ?, " +
                        "    D_NKIP   = ?, " +
                        "    T_ZAM    = ?, " +
                        "    ID_OBJ   = ?, " +
                        "    OBJ_CODE = ?, " +
                        "    PS       = ?, " +
                        "    OPCL     = ?, " +
                        "    TID_PR   = ?, " +
                        "    TID_RG   = ?, " +
                        "    SCODE    = ? "
                        ;
                pstmt = connection.prepareStatement(sqlInsert);
                int i = 0;
                for (DeviceDTO deviceDTO : pdFile.getSpecificContent()) {
                    pstmt.setLong(1, deviceDTO.getId());
                    pstmt.setLong(2, deviceDTO.getTypeId());
                    pstmt.setString(3, deviceDTO.getNumber());
                    pstmt.setString(4, deviceDTO.getReleaseYear());
                    pstmt.setDate(5, deviceDTO.getTestDate());

                    pstmt.setObject(6, deviceDTO.getNextTestDate());
                    pstmt.setInt(7, deviceDTO.getReplacementPeriod());
                    pstmt.setObject(8, deviceDTO.getLocationId());
                    pstmt.setString(9, deviceDTO.getFacilityId());
                    pstmt.setString(10, deviceDTO.getStatus());

                    pstmt.setString(11, deviceDTO.getOpcl());
                    pstmt.setString(12, deviceDTO.getTid_pr());
                    pstmt.setString(13, deviceDTO.getTid_rg());
                    pstmt.setString(14, deviceDTO.getScode());
                    //for update
                    pstmt.setLong(15, deviceDTO.getTypeId());
                    pstmt.setString(16, deviceDTO.getNumber());
                    pstmt.setString(17, deviceDTO.getReleaseYear());
                    pstmt.setDate(18, deviceDTO.getTestDate());

                    pstmt.setObject(19, deviceDTO.getNextTestDate());
                    pstmt.setInt(20, deviceDTO.getReplacementPeriod());
                    pstmt.setObject(21, deviceDTO.getLocationId());
                    pstmt.setString(22, deviceDTO.getFacilityId());
                    pstmt.setString(23, deviceDTO.getStatus());

                    pstmt.setString(24, deviceDTO.getOpcl());
                    pstmt.setString(25, deviceDTO.getTid_pr());
                    pstmt.setString(26, deviceDTO.getTid_rg());
                    pstmt.setString(27, deviceDTO.getScode());
                    pstmt.addBatch();
                    if (i % 1000 == 0) {
                        pstmt.executeBatch();
                    }
                    i++;
                }
                pstmt.executeBatch();
            } finally {
                pstmt.close();
            }
        });
        session.close();

    }

    private void upsertDevTrans(PDFile pdFile) {
        PDFile.MetaData metaData = pdFile.getMetaData();
        em.createNativeQuery(
                        " INSERT INTO drtu.dev_trans (NAME, FTYPE, PS, STNUM, DATE_CREATE, NAME_T, STNUM_T, " +
                                " RTU_T, DATE_T, TIME_T) VALUES ( " +
                                " UPPER (:name), " +
                                " UPPER (:ftype), " +
                                " UPPER (:ps), " +
                                " :stnum, " +
                                " :date_create, " +
                                " UPPER(:name_t), " +
                                " :stnum_t, " +
                                " :rtu_t, " +
                                " :date_t, " +
                                " :time_t ) ON CONFLICT (name) DO" +
                                " UPDATE SET " +
                                " NAME_T = UPPER (:name_t), " +
                                " STNUM_T = :stnum_t, " +
                                " RTU_T = :rtu_t, " +
                                " DATE_T = :date_t, " +
                                " TIME_T = :time_t "
                ).unwrap(org.hibernate.query.NativeQuery.class)
                .setParameter("name", metaData.getName())
                .setParameter("ftype", metaData.getType())
                .setParameter("ps", "R")
                .setParameter("stnum", metaData.getRecordsQuantity())
                .setParameter("date_create", metaData.getTimestamp().toLocalDateTime().toLocalDate())
                .setParameter("date_t", new Date(System.currentTimeMillis()))
                .setParameter("name_t", "t" + metaData.getName())
                .setParameter("stnum_t", metaData.getRecordsQuantity() - metaData.getNotProcessedRecordsQuantity())
                .setParameter("rtu_t", RTU_T)
                .setParameter("time_t", new Timestamp(System.currentTimeMillis()))
                .executeUpdate();
    }

}
