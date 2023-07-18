package dms.dao;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import dms.dto.DeviceDTO;
import dms.utils.CompressUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class SchemaManager {

    @PersistenceContext
    private EntityManager em;

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
        List<String> rowList = Collections.emptyList();

        File file = new File(path);
        log.info("exists: " + file.exists());
        log.info("is dir: " + file.isDirectory());
        log.info("is file: " + file.isFile());
        log.info("can read: " + file.canRead());

        if (file.exists() && file.isFile() && file.canRead()) {
            rowList = Files.readAllLines(Paths.get(path), Charset.forName("windows-1251"));
        }
        return rowList;
    }

    public void unzipFile() {
        String fileZip = "rtubase/src/main/resources/pd_files/d1110714.001";

        File fz = new File(fileZip);
        log.info("exists: " + String.valueOf(fz.exists()));
        log.info("is dir: " + String.valueOf(fz.isDirectory()));
        log.info("is file: " + String.valueOf(fz.isFile()));
        log.info("can read: " + String.valueOf(fz.canRead()));

        File destDir = new File("rtubase/src/main/resources/pd_files");
        log.info("exists: " + String.valueOf(destDir.exists()));
        log.info("is dir: " + String.valueOf(destDir.isDirectory()));
        log.info("is file: " + String.valueOf(destDir.isFile()));
        log.info("can read: " + String.valueOf(destDir.canRead()));

        byte[] buffer = new byte[1024];

        try {
            FileInputStream fis = new FileInputStream(fz);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                // ...
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public void extractGzip(String sourceFilePath, String destinationFilePath) throws Exception {

        byte[] buffer = new byte[1024];
        FileInputStream fileIn = new FileInputStream(sourceFilePath);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
        FileOutputStream fileOutputStream = new FileOutputStream(destinationFilePath);
        int bytes_read;
        while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, bytes_read);
        }
        gZIPInputStream.close();
        fileOutputStream.close();
    }

    @Transactional
    public void restoreDevFromDFiles() throws Exception {
        String compressedFilePath = "rtubase/src/main/resources/pd_files/d1110714.001";
        String extractedFilePath = "rtubase/src/main/resources/pd_files/d1110714";

        CompressUtils.extractGzip(compressedFilePath, extractedFilePath);

        File extractedFile = new File(extractedFilePath);
        if (!extractedFile.exists() || !extractedFile.isFile() || !extractedFile.canRead()) {
            log.error("dms: file not accessible");
            return;
        }

        List<DeviceDTO> deviceDTOList = new ArrayList<>();
        List<String> strRowList = readFileToList(extractedFilePath);

        String headerRow = strRowList.get(0);
        if (!checkVersion(headerRow.substring(35, 42))) {
            log.error("dms: version wrong");
            return;
        }

        strRowList.forEach(item -> {
            deviceDTOList.add(DeviceDTO.stringify(item));
        });

        removeDevice(headerRow.substring(1, 5));
        addDeviceRecord(deviceDTOList);
    }

    private boolean checkVersion(String version) {
        return em.createNativeQuery(
                        "select  value_c  from dock.val where name = 'VERSION'")
                .getSingleResult().equals(version);
    }


    private void removeDevice(String objCode) {
        if (objCode.charAt(3) == '0') {
            em.createNativeQuery(
                            "delete from drtu.dev where SUBSTR(obj_code , 1 , 3 ) =  '" +
                                    objCode +
                                    "'")
                    .executeUpdate();
        } else {
            em.createNativeQuery(
                            "delete from drtu.dev where SUBSTR(obj_code , 1 , 4 ) =  '" +
                                    objCode +
                                    "'")
                    .executeUpdate();
        }

    }

    private boolean isDeviceTypeExists(String typeId) {
        return em.createNativeQuery(
                        "select 'X' from drtu.s_dev where id = TO_NUMBER (  '" +
                                typeId +
                                "' , '9999999999' ) ")
                .getSingleResult().equals("X");
    }

    private boolean isLocationFree(String locationId) {
        if (locationId == null || locationId.equals("")) return true;
        return !em.createNativeQuery(
                        "'X' from drtu.dev where id_obj = TO_NUMBER('" +
                                locationId +
                                "', '99999999999999')")
                .getSingleResult().equals("X");
    }

    private void addDeviceRecord(List<DeviceDTO> deviceDTOList) {
        deviceDTOList.forEach(item -> {
            if (!isDeviceTypeExists(String.valueOf(item.getTypeId()))) {
                log.warn("device type not exist");
                return;
            }
            if (!isLocationFree(String.valueOf(item.getLocationId()))) {
                log.warn("Location not free");
                return;
            }
            if (updateDevice(item) < 1) {
                insertDevice(item);
            }
        });
    }

    private Integer updateDevice(DeviceDTO deviceDTO) {
        return em.createNativeQuery(
                "update drtu.dev " +
                        "set devid = TO_NUMBER (  '" +
                        deviceDTO.getTypeId() +
                        "' , '9999999999' ) , NUM  =  '" +
                        deviceDTO.getNumber() +
                        "' , myear  =  '" +
                        deviceDTO.getReleaseYear() +
                        "' , d_tkip  = TO_DATE (  " +
                        deviceDTO.getTestDate() +
                        " , 'DDMMYYYY' ) , d_nkip  = TO_DATE (  " +
                        deviceDTO.getNextTestDate() +
                        " , 'DDMMYYYY' ) , t_zam  =  " +
                        deviceDTO.getReplacementPeriod() +
                        " , id_obj  = TO_NUMBER (  null , '99999999999999' ) , obj_code  =  '" +
                        deviceDTO.getFacilityId().substring(0, 4) +
                        "' , ps  =  '" +
                        deviceDTO.getStatus() +
                        "' , opcl  =  '" +
                        deviceDTO.getOpcl() +
                        "' , tid_pr  =  " +
                        deviceDTO.getTid_pr() +
                        " , tid_rg  =  " +
                        deviceDTO.getTid_rg() +
                        " , scode  =  '" +
                        deviceDTO.getScode() +
                        "'  where id = TO_NUMBER (  '" +
                        deviceDTO.getId() +
                        "' , '9999999999' )").executeUpdate();
    }

    private Integer insertDevice(DeviceDTO deviceDTO) {
        return em.createNativeQuery(
                "insert into drtu.dev (ID, DEVID, NUM, MYEAR, D_TKIP, D_NKIP, T_ZAM, ID_OBJ, OBJ_CODE, PS, " +
                        "OPCL, TID_PR, TID_RG, SCODE) values (TO_NUMBER (  '" +
                        deviceDTO.getId() +
                        "' , '9999999999' ) , TO_NUMBER (  '" +
                        deviceDTO.getTypeId() +
                        "' , '9999999999' ) ,  '" +
                        deviceDTO.getNumber() +
                        "' ,  '" +
                        deviceDTO.getReleaseYear() +
                        "' , TO_DATE (  " +
                        deviceDTO.getTestDate() +
                        " , 'DDMMYYYY' ) , TO_DATE (  " +
                        deviceDTO.getNextTestDate() +
                        " , 'DDMMYYYY' ) ,  " +
                        deviceDTO.getReplacementPeriod() +
                        " , TO_NUMBER (  " +
                        deviceDTO.getLocationId() +
                        " , '99999999999999' ) ,  '" +
                        deviceDTO.getFacilityId() +
                        "' ,  '" +
                        deviceDTO.getStatus() +
                        "' ,  '" +
                        deviceDTO.getOpcl() +
                        "' ,  " +
                        deviceDTO.getTid_pr() +
                        " ,  " +
                        deviceDTO.getTid_rg() +
                        " ,  '" +
                        deviceDTO.getScode() +
                        "' )").executeUpdate();


    }


}
