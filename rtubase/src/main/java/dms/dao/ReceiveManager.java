package dms.dao;

import dms.model.DevModel;
import dms.model.DevObjModel;
import dms.model.PDFileModel;
import dms.standing.data.model.DObjModel;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class ReceiveManager {

    @PersistenceContext
    private EntityManager em;

    final String RTU_T = "1000";

    public final String DRTU_SCHEMA_TEMP_NAME = "temp_drtu";

    //    todo - need to refactor with
    @Transactional
    public void saveFileContent(List<String> fileContent) {
        if (!isTemporaryDrtuSchemaExists()) throw new RuntimeException("Temporary Drtu Schema Not Exists");
        try {
            PDFileModel pdFile = new PDFileModel(fileContent);
            if (isFileVersionActual(pdFile.getMetaData().getVersion())) {
                if (pdFile.getMetaData().getType().equals("D")) {
                    deleteRowsFromDevByObjCode(pdFile.getMetaData().getObjectCode());
                    isDeviceTypeExists(pdFile);
                    isLocationFree(pdFile);
                    upsertDevice(pdFile);
                    upsertDevTrans(pdFile);
                } else if (pdFile.getMetaData().getType().equals("P")) {
                    deleteRowsFromDevObjByObjCode(pdFile.getMetaData().getObjectCode());
                    HashSet<DObjModel> facilitySet = upsertLocation(pdFile);
                    upsertFacility(facilitySet);
                    upsertDevTrans(pdFile);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTemporaryDrtuSchemaExists() {
        String queryString = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '%s'";
        queryString = String.format(queryString, DRTU_SCHEMA_TEMP_NAME);
        List<String> schemaNameList = em.createNativeQuery(queryString).getResultList();
        return !schemaNameList.isEmpty();
    }

    private boolean isFileVersionActual(String version) {
        String queryString = "SELECT value_c FROM dock.val WHERE name = 'VERSION'";
        List<?> result = em.createNativeQuery(queryString).getResultList();
        return (result.size() > 0 && result.get(0).equals(version));
    }

    private void deleteRowsFromDevByObjCode(String objCode) throws Exception {
        String queryString;
        if (objCode.length() != 4) throw new Exception("dms: Parameter Length is wrong");
        if (objCode.charAt(3) == '0') {
            queryString = "DELETE FROM %s WHERE SUBSTR(obj_code, 1, 3 ) = '%s'";
            queryString = String.format(queryString,
                    DRTU_SCHEMA_TEMP_NAME + ".dev",
                    objCode.substring(0, 3));
        } else {
            queryString = "DELETE FROM %s WHERE SUBSTR(obj_code, 1, 4 ) = '%s'";
            queryString = String.format(queryString,
                    DRTU_SCHEMA_TEMP_NAME + ".dev",
                    objCode.substring(0, 4));
        }
        em.createNativeQuery(queryString).executeUpdate();

    }

    private void deleteRowsFromDevObjByObjCode(String objCode) throws Exception {
        String queryString;
        if (objCode.length() != 4) throw new Exception("dms: Parameter Length is wrong");
        if (objCode.charAt(3) == '0') {
            queryString = "DELETE FROM %s WHERE SUBSTR(obj_code, 1, 3 ) = '%s'";
            queryString = String.format(queryString,
                    DRTU_SCHEMA_TEMP_NAME + ".dev_obj",
                    objCode.substring(0, 3));
        } else {
            queryString = "DELETE FROM %s WHERE SUBSTR(obj_code, 1, 4 ) = '%s'";
            queryString = String.format(queryString,
                    DRTU_SCHEMA_TEMP_NAME + ".dev_obj",
                    objCode.substring(0, 4));
        }
        em.createNativeQuery(queryString).executeUpdate();
    }

    private void isDeviceTypeExists(PDFileModel pdFile) {
        String queryString = "SELECT id FROM %s";
        queryString = String.format(queryString,
                DRTU_SCHEMA_TEMP_NAME + ".s_dev");
        List<DevModel> removingItems = new ArrayList<>();
        List typeIdList = em.createNativeQuery(queryString)
                .setHint("org.hibernate.fetchSize", "2000")
                .getResultList();
        HashSet hs = new HashSet(typeIdList);
        pdFile.getDContent().forEach(item -> {
            if (!hs.contains(BigDecimal.valueOf(item.getDevId()))) {
                removingItems.add(item);
            }
        });
        pdFile.increaseNotProcessedRecordsQuantity(removingItems.size());
        removingItems.forEach(item -> pdFile.getDContent().remove(item));
    }

    private void isLocationFree(PDFileModel pdFile) {
        String queryString = "SELECT id_obj FROM %s WHERE id_obj IS NOT NULL";
        queryString = String.format(queryString,
                DRTU_SCHEMA_TEMP_NAME + ".dev");
        List<DevModel> removingItems = new ArrayList<>();
        List idObjList = em.createNativeQuery(queryString)
                .setHint("org.hibernate.fetchSize", "2000")
                .getResultList();
        HashSet hs = new HashSet(idObjList);
        pdFile.getDContent().forEach(item -> {
            if (item.getIdObj() != null && hs.contains(BigDecimal.valueOf(item.getIdObj()))) {
                removingItems.add(item);
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
                String queryString = "insert into %s ( " +
                        " ID, DEVID, NUM, MYEAR, D_TKIP, " +
                        " D_NKIP, T_ZAM, ID_OBJ, OBJ_CODE, PS, " +
                        " OPCL, TID_PR, TID_RG, SCODE " +
                        " ) " +
                        " values (?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?) " +
                        " on conflict (ID) do  update set " +
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
                queryString = String.format(queryString, DRTU_SCHEMA_TEMP_NAME + ".dev");
                pstmt = connection.prepareStatement(queryString);
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

    //todo - change drtu_2023_07_07
    private HashSet<DObjModel> upsertLocation(PDFileModel pdFile) {
        HashSet<DObjModel> facilitySet = new HashSet<>();
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            PreparedStatement pstmt = null;
            try {
                String queryString = "insert into %s ( " +
                        " ID, LOCATE_T, LOCATE, REGION_T, REGION, " +
                        " NPLACE, NSHEM, OBJ_CODE, OPCL, SCODE " +
                        " ) " +
                        " values (?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?, ?) " +
                        " on conflict (ID) do  update set " +
                        "    LOCATE_T    = ?, " +
                        "    LOCATE      = ?, " +
                        "    REGION_T    = ?, " +
                        "    REGION   = ?, " +
                        "    NPLACE   = ?, " +
                        "    NSHEM    = ?, " +
                        "    OBJ_CODE   = ?, " +
                        "    OPCL = ?, " +
                        "    SCODE    = ? ";
                queryString = String.format(queryString, DRTU_SCHEMA_TEMP_NAME + ".dev_obj");
                pstmt = connection.prepareStatement(queryString);
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

    //todo - change drtu_2023_07_07
    private void upsertFacility(HashSet<DObjModel> facilitySet) {
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            PreparedStatement pstmt = null;
            try {
                String queryString = "INSERT INTO %s ( " +
                        " ID, KIND, CLS, NAME_OBJ, KOD_DOR, " +
                        " KOD_DIST, KOD_RTU, KOD_OBKT, KOD_OBJ " +
                        " ) " +
                        " VALUES (?, ?, ?, ?, ?, " +
                        " ?, ?, ?, ?) " +
                        " ON CONFLICT (ID) DO  NOTHING ";
                queryString = String.format(queryString, DRTU_SCHEMA_TEMP_NAME + ".d_obj");
                pstmt = connection.prepareStatement(queryString);
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

    //todo - change drtu_2023_07_07
    private void upsertDevTrans(PDFileModel pdFile) {
        PDFileModel.MetaData metaData = pdFile.getMetaData();
        String queryString = "INSERT INTO %s (NAME, FTYPE, PS, STNUM, DATE_CREATE, NAME_T, STNUM_T, " +
                " RTU_T, DATE_T, TIME_T) " +
                " VALUES ( " +
                " UPPER ('%s'), " +
                " UPPER ('%s'), " +
                " UPPER ('%s'), " +
                " %s, " +
                " '%s', " +
                " UPPER('%s'), " +
                " %s, " +
                " '%s', " +
                " '%s', " +
                " '%s' ) " +
                " ON CONFLICT (name) DO UPDATE SET " +
                " NAME_T = UPPER ('%s'), " +
                " STNUM_T = %s, " +
                " RTU_T = '%s', " +
                " DATE_T = '%s', " +
                " TIME_T = '%s' ";
        queryString = String.format(queryString,
                DRTU_SCHEMA_TEMP_NAME + ".dev_trans",
                metaData.getName(),
                metaData.getType(),
                "R",
                metaData.getRecordsQuantity(),
                metaData.getTimestamp().toLocalDateTime().toLocalDate(),
                "t" + metaData.getName(),
                metaData.getRecordsQuantity() - metaData.getNotProcessedRecordsQuantity(),
                RTU_T,
                new Date(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                "t" + metaData.getName(),
                metaData.getRecordsQuantity() - metaData.getNotProcessedRecordsQuantity(),
                RTU_T,
                new Date(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis())
        );
        em.createNativeQuery(queryString).executeUpdate();
    }


    //        todo - must be moved in other class
    public List<String> getReceivedFileNameList(String schemaName) {
        String queryString = String.format("SELECT name FROM %s.dev_trans", schemaName);
        return em.createNativeQuery(queryString).getResultList();
    }

    //        todo - must be moved in other class
    public void createDevicesMainView() {
        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.doWork(connection ->
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/CreateDeviceMainView.sql"))
        );
        session.close();
    }
}
