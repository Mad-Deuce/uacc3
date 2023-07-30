package dms.dao;

import dms.model.DevModel;
import dms.model.DevObjModel;
import dms.model.PDFileModel;
import dms.standing.data.model.DObjModel;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                PDFileModel pdFile = new PDFileModel(readFileToList(item.getPath()));
                if (isFileVersionActual(pdFile.getMetaData().getVersion())) {
                    if (pdFile.getMetaData().getType().equals("D")) {
                        clearDevTable(pdFile.getMetaData().getObjectCode());
                        isDeviceTypeExists(pdFile);
                        isLocationFree(pdFile);
                        upsertDevice(pdFile);
                        upsertDevTrans(pdFile);
                    } else if (pdFile.getMetaData().getType().equals("P")) {
                        clearDevObjTable(pdFile.getMetaData().getObjectCode());
                        HashSet<DObjModel> facilitySet = upsertLocation(pdFile);
                        upsertFacility(facilitySet);
                        upsertDevTrans(pdFile);
                    }
                    item.delete();
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

    @Transactional
    public void receivePDFile(List<File> fileList) {

        fileList.forEach(item -> {
            try {
                PDFileModel pdFile = new PDFileModel(readFileToList(item.getPath()));
                if (isFileVersionActual(pdFile.getMetaData().getVersion())) {
                    if (pdFile.getMetaData().getType().equals("D")) {
                        clearDevTable(pdFile.getMetaData().getObjectCode());
                        isDeviceTypeExists(pdFile);
                        isLocationFree(pdFile);
                        upsertDevice(pdFile);
                        upsertDevTrans(pdFile);
                    } else if (pdFile.getMetaData().getType().equals("P")) {
                        clearDevObjTable(pdFile.getMetaData().getObjectCode());
                        HashSet<DObjModel> facilitySet = upsertLocation(pdFile);
                        upsertFacility(facilitySet);
                        upsertDevTrans(pdFile);
                    }
                    item.delete();
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

    public List<File> getFileList() throws Exception {
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


    private HashSet<File> renameFiles(List<File> fileList) throws IOException {
        //  example: input filename d1011b21 => output filename d1011_2023_11_21
        if (fileList.isEmpty()) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        HashSet<File> result = new HashSet<>();
        List<File> unprocessedFile = new ArrayList<>();

        for (File file : fileList) {
            String header = Files.readAllLines(file.toPath(), Charset.forName("windows-1251")).get(0);
            LocalDate fileDate = LocalDate.parse(header.substring(22, 30), formatter);
            if (!isLastDayOfMonth(fileDate)) {
                fileDate = toNearestFriday(fileDate);
            }
            String nameSuffix = "_" + fileDate;
        }

        return result;
    }

    private boolean isLastDayOfMonth(LocalDate inputDate) {
        return inputDate.equals(inputDate.withDayOfMonth(inputDate.getMonth().length(inputDate.isLeapYear())));
    }

    private LocalDate toNearestFriday(LocalDate inputDate) {
        int dayOfWeekNum = inputDate.getDayOfWeek().getValue();
        int offset = ((dayOfWeekNum + 5) % 7) * -1;
        return inputDate.plusDays(offset);
    }


    private static HashMap<String, HashSet<File>> groupFileByDate(HashSet<File> fileList) {
        if (fileList.isEmpty()) return null;
        HashMap<String, HashSet<File>> result = new HashMap<>();
        fileList.forEach(item -> {
            String key = item.getName().substring(5, 16);//  example: filename d1011_2023_11_21 => key _2023_11_21
            if (!result.containsKey(key)) result.put(key, new HashSet<>());
            result.get(key).add(item);
        });
        return result;
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

    private void clearDevObjTable(String objCode) throws Exception {
        if (objCode.length() != 4) throw new Exception("dms: Parameter Length is wrong");
        if (objCode.charAt(3) == '0') {
            em.createNativeQuery(
                            "delete from drtu.dev_obj where SUBSTR(obj_code, 1, 3 ) = :objCode"
                    )
                    .setParameter("objCode", objCode.substring(0, 3))
                    .executeUpdate();
        } else {
            em.createNativeQuery(
                            "delete from drtu.dev_obj where SUBSTR(obj_code, 1, 4) = :objCode"
                    )
                    .setParameter("objCode", objCode)
                    .executeUpdate();
        }

    }

    private void isDeviceTypeExists(PDFileModel pdFile) {
        List<DevModel> removingItems = new ArrayList<>();
        List typeIdList = em.createNativeQuery("select id from drtu.s_dev")
                .setHint("org.hibernate.fetchSize", "2000")
                .getResultList();
        HashSet hs = new HashSet(typeIdList);
        pdFile.getDContent().forEach(item -> {
            if (!hs.contains(BigDecimal.valueOf(item.getDevId()))) {
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
        removingItems.forEach(item -> pdFile.getDContent().remove(item));
    }

    private void isLocationFree(PDFileModel pdFile) {
        List<DevModel> removingItems = new ArrayList<>();
        List idObjList = em.createNativeQuery("select id_obj from drtu.dev where id_obj is not null")
                .setHint("org.hibernate.fetchSize", "2000")
                .getResultList();
        HashSet hs = new HashSet(idObjList);
        pdFile.getDContent().forEach(item -> {
            if (item.getIdObj() != null && hs.contains(BigDecimal.valueOf(item.getIdObj()))) {
                removingItems.add(item);
                logStr
                        .append("dms/error: File=")
                        .append(pdFile.getMetaData().getName())
                        .append("---")
                        .append("Location with Id=")
                        .append(item.getIdObj())
                        .append(" not free for device id=")
                        .append(item.getId())
                        .append("---")
                        .append(new Timestamp(System.currentTimeMillis()))
                        .append("\n");
            }
        });
        pdFile.increaseNotProcessedRecordsQuantity(removingItems.size());
        removingItems.forEach(item -> pdFile.getDContent().remove(item));
    }

    private void upsertDevice(PDFileModel pdFile) {
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
                        "    DEVID    = ?, " +
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
                        "    SCODE    = ? ";
                pstmt = connection.prepareStatement(sqlInsert);
                int i = 0;
                for (DevModel dRowData : pdFile.getDContent()) {
                    pstmt.setLong(1, dRowData.getId());
                    pstmt.setLong(2, dRowData.getDevId());
                    pstmt.setString(3, dRowData.getNum());
                    pstmt.setString(4, dRowData.getMYear());
                    pstmt.setDate(5, dRowData.getDTKip());

                    pstmt.setObject(6, dRowData.getDNKip());
                    pstmt.setInt(7, dRowData.getTZam());
                    pstmt.setObject(8, dRowData.getIdObj());
                    pstmt.setString(9, dRowData.getObjCode());
                    pstmt.setString(10, dRowData.getPs());

                    pstmt.setString(11, dRowData.getOpcl());
                    pstmt.setString(12, dRowData.getTIdPr());
                    pstmt.setString(13, dRowData.getTIdRg());
                    pstmt.setString(14, pdFile.getMetaData().getSCode());
                    //for update
                    pstmt.setLong(15, dRowData.getDevId());
                    pstmt.setString(16, dRowData.getNum());
                    pstmt.setString(17, dRowData.getMYear());
                    pstmt.setDate(18, dRowData.getDTKip());

                    pstmt.setObject(19, dRowData.getDNKip());
                    pstmt.setInt(20, dRowData.getTZam());
                    pstmt.setObject(21, dRowData.getIdObj());
                    pstmt.setString(22, dRowData.getObjCode());
                    pstmt.setString(23, dRowData.getPs());

                    pstmt.setString(24, dRowData.getOpcl());
                    pstmt.setString(25, dRowData.getTIdPr());
                    pstmt.setString(26, dRowData.getTIdRg());
                    pstmt.setString(27, pdFile.getMetaData().getSCode());
                    pstmt.addBatch();
                    if (i % 1000 == 0) {
                        pstmt.executeBatch();
                    }
                    i++;
                }
                pstmt.executeBatch();
            } finally {
                assert pstmt != null;
                pstmt.close();
            }
        });
        session.close();

    }

    private HashSet<DObjModel> upsertLocation(PDFileModel pdFile) {
        HashSet<DObjModel> facilitySet = new HashSet<>();
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            PreparedStatement pstmt = null;
            try {
                String sqlInsert = "insert into DRTU.DEV_OBJ ( " +
                        " ID, LOCATE_T, LOCATE, REGION_T, REGION, " +
                        " NPLACE, NSHEM, OBJ_CODE, OPCL, SCODE " +
                        " ) " +
                        " values (?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?, ?) " +
                        "on conflict (ID) do  update set " +
                        "    LOCATE_T    = ?, " +
                        "    LOCATE      = ?, " +
                        "    REGION_T    = ?, " +
                        "    REGION   = ?, " +
                        "    NPLACE   = ?, " +
                        "    NSHEM    = ?, " +
                        "    OBJ_CODE   = ?, " +
                        "    OPCL = ?, " +
                        "    SCODE    = ? ";
                pstmt = connection.prepareStatement(sqlInsert);
                int i = 0;
                for (DevObjModel pRowData : pdFile.getPContent()) {
                    pstmt.setLong(1, pRowData.getId());
                    pstmt.setString(2, pRowData.getLocateT());
                    pstmt.setString(3, pRowData.getLocate());
                    pstmt.setString(4, pRowData.getRegionT());
                    pstmt.setString(5, pRowData.getRegion());

                    pstmt.setString(6, pRowData.getNPlace());
                    pstmt.setString(7, pRowData.getNShem());
                    pstmt.setString(8, pRowData.getObjCode());
                    pstmt.setString(9, pRowData.getOpcl());
                    pstmt.setString(10, pdFile.getMetaData().getSCode());

                    pstmt.setString(11, pRowData.getLocateT());
                    pstmt.setString(12, pRowData.getLocate());
                    pstmt.setString(13, pRowData.getRegionT());
                    pstmt.setString(14, pRowData.getRegion());

                    pstmt.setString(15, pRowData.getNPlace());
                    pstmt.setString(16, pRowData.getNShem());
                    pstmt.setString(17, pRowData.getObjCode());
                    pstmt.setString(18, pRowData.getOpcl());
                    pstmt.setString(19, pdFile.getMetaData().getSCode());


                    pstmt.addBatch();
                    if (i % 1000 == 0) {
                        pstmt.executeBatch();
                    }
                    i++;

                    facilitySet.add(new DObjModel(pRowData));
                }
                pstmt.executeBatch();
            } finally {
                assert pstmt != null;
                pstmt.close();
            }
        });
        session.close();
        return facilitySet;
    }

    private void upsertFacility(HashSet<DObjModel> facilitySet) {
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            PreparedStatement pstmt = null;
            try {
                String sqlInsert = "insert into DRTU.D_OBJ ( " +
                        " ID, KIND, CLS, NAME_OBJ, KOD_DOR, " +
                        " KOD_DIST, KOD_RTU, KOD_OBKT, KOD_OBJ " +
                        " ) " +
                        " values (?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?) " +
                        "on conflict (ID) do  NOTHING ";
                pstmt = connection.prepareStatement(sqlInsert);
                int i = 0;
                for (DObjModel dObjModel : facilitySet) {
                    pstmt.setString(1, dObjModel.getId());
                    pstmt.setString(2, dObjModel.getKind());
                    pstmt.setString(3, dObjModel.getCls());
                    pstmt.setString(4, dObjModel.getNameObj());
                    pstmt.setString(5, dObjModel.getKodDor());

                    pstmt.setInt(6, dObjModel.getKodDist());
                    pstmt.setInt(7, dObjModel.getKodRtu());
                    pstmt.setInt(8, dObjModel.getKodObkt());
                    pstmt.setString(9, dObjModel.getKodObj());

                    pstmt.addBatch();
                    if (i % 1000 == 0) {
                        pstmt.executeBatch();
                    }
                    i++;

                }
                pstmt.executeBatch();
            } finally {
                assert pstmt != null;
                pstmt.close();
            }
        });
        session.close();
    }

    private void upsertDevTrans(PDFileModel pdFile) {
        PDFileModel.MetaData metaData = pdFile.getMetaData();
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
