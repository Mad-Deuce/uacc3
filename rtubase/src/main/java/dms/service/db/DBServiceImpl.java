package dms.service.db;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.ReceiveManager;
import dms.dao.schema.SchemaDao;
import dms.service.stats.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class DBServiceImpl implements DBService {


    @Autowired
    private StatsService statsService;
    @Autowired
    private SchemaDao sm;
    @Autowired
    private ReceiveManager rm;
    @Autowired
    private TenantIdentifierResolver currentTenant;


    @Value("${dms.upload.path}")
    private String UPLOAD_DIR_PATH_PARTS;

    private File getUploadDir() throws Exception {
        String errorMessage = "dms: Directory for P,D Files not accessible";
        File upload_dir = null;
        List<String> pathParts = Arrays.stream(UPLOAD_DIR_PATH_PARTS.split(",")).toList();
        for (String part : pathParts) {
            if (upload_dir == null) {
                upload_dir = new File(part);
            } else {
                upload_dir = new File(upload_dir, part);
            }
        }

        assert upload_dir != null;
        if (!upload_dir.exists()) {
            upload_dir.mkdirs();
        }

        if (!upload_dir.isDirectory() || !upload_dir.canRead()) {
            throw new Exception(errorMessage);
        }

        return upload_dir;
    }

    //    todo - refactoring this method, with exclude call sm.restoreEmpty()
    private void receivePDFiles(List<File> sourceFiles) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        if (!sm.isSchemaExists(sm.DRTU_SCHEMA_NAME)) {
            throw new Exception("schema: " + sm.DRTU_SCHEMA_NAME + "is not exists");
        }

        for (File file : sourceFiles) {
//            List<String> schemaNameList = sm.getDrtuSchemaNameList();
            List<String> schemaNameList = sm.getSchemaNameListLikeString(sm.DRTU_SCHEMA_NAME + "_%");

            List<String> fileContent = extractGzip(file);
            String fileHeader = fileContent.get(0);
            LocalDate fileDate = LocalDate.parse(fileHeader.substring(22, 30), formatter);

            if (!isLastDayOfMonth(fileDate)) {
                fileDate = toNearestFriday(fileDate);
            }

            String schemaNameSuffix = ("_" + fileDate).replace("-", "_");
            if (schemaNameList.contains(sm.DRTU_SCHEMA_NAME + schemaNameSuffix)) {
                List<String> receivedFileNameList = rm.getReceivedFileNameList(sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
                if (!receivedFileNameList.contains(fileHeader.substring(0, 12).toUpperCase())) {
                    sm.removeSchema(rm.DRTU_SCHEMA_TEMP_NAME);
                    sm.renameSchema(sm.DRTU_SCHEMA_NAME + schemaNameSuffix, rm.DRTU_SCHEMA_TEMP_NAME);
                    rm.receivePDFiles(fileContent);
                    sm.renameSchema(rm.DRTU_SCHEMA_TEMP_NAME, sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
                }
            } else {
                sm.cloneSchema(sm.DRTU_SCHEMA_NAME, rm.DRTU_SCHEMA_TEMP_NAME);
                rm.receivePDFiles(fileContent);
                sm.renameSchema(rm.DRTU_SCHEMA_TEMP_NAME, sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
            }
            currentTenant.setCurrentTenant(sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
            statsService.saveCurrentSchemaOverdueDevsStats();
            file.delete();
        }
    }

    @PostConstruct
    @Scheduled(initialDelay = 20000, fixedDelay = 30000)
    public void isPDDirEmpty() throws Exception {
        log.info("------------------check PD dir -----------------");
        List<File> sourceFiles = getFiles();
        if (!sourceFiles.isEmpty()) receivePDFiles(sourceFiles);
    }

    @Override
    public List<LocalDate> getDatesOfExistingSchemas() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        List<LocalDate> result = new ArrayList<>();
//        List<String> schemaNameList = sm.getDrtuSchemaNameList();
        List<String> schemaNameList = sm.getSchemaNameListLikeString(sm.DRTU_SCHEMA_NAME + "_%");
        schemaNameList.forEach(item -> {
            LocalDate date = LocalDate.parse(item.substring(sm.DRTU_SCHEMA_NAME.length()), formatter);
            result.add(date);
        });
        return result;
    }

    @Override
    public LocalDate getDateOfActiveSchema() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        return LocalDate
                .parse(currentTenant.resolveCurrentTenantIdentifier().substring(sm.DRTU_SCHEMA_NAME.length()), formatter);
    }

    @Override
    public LocalDate setActiveSchemaDate(LocalDate schemaDate) {
        String schemaNameSuffix = ("_" + schemaDate).replace("-", "_");
        List<String> schemaNameList = sm.getSchemaNameListLikeString(sm.DRTU_SCHEMA_NAME + "_%");
        if (schemaNameList.contains(sm.DRTU_SCHEMA_NAME + schemaNameSuffix))
            currentTenant.setCurrentTenant(sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
        return getDateOfActiveSchema();
    }

    private List<File> getFiles() throws Exception {
        FileFilter filter = f -> (f.isFile()
                && f.getName().length() == 12
                && f.getName().matches("[pd]\\d{4}\\w\\d{2}\\.\\d{3}"));
        File dir = getUploadDir();
        return Arrays.stream(Objects.requireNonNull(dir.listFiles(filter))).toList();
    }

    private List<String> extractGzip(File file) throws IOException {

        FileInputStream fileIn = new FileInputStream(file);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

        byte[] bytes = gZIPInputStream.readAllBytes();
        String s = new String(bytes, Charset.forName("windows-1251"));
        String[] ss = s.split("\\r\\n");
        List<String> ls = Arrays.stream(ss).toList();
        gZIPInputStream.close();
        fileIn.close();
        return ls;
    }

    private boolean isLastDayOfMonth(LocalDate inputDate) {
        return inputDate.equals(inputDate.withDayOfMonth(inputDate.getMonth().length(inputDate.isLeapYear())));
    }

    private LocalDate toNearestFriday(LocalDate inputDate) {
        int dayOfWeekNum = inputDate.getDayOfWeek().getValue();
        int offset = (((dayOfWeekNum + 4) % 7) - 2) * -1;
        return inputDate.plusDays(offset);
    }

    //        todo - must be moved in other class
    public void restoreEmpty() {
        String command = "pg_restore -U postgres -w -d rtubase " +
                "/vagrant/ansible/roles/postgresql/files/d20230324.backup";

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


}
