package dms.service.db;

import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.ReceiveManager;
import dms.dao.SchemaManager;
import dms.service.stats.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class DBServiceImpl implements DBService {


    @Autowired
    private StatsService statsService;
    @Autowired
    private SchemaManager sm;
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
            sm.removeSchema(sm.DRTU_SCHEMA_NAME);
            sm.removeSchema(sm.DOCK_SCHEMA_NAME);
            sm.restoreEmpty();
        }

        for (File file : sourceFiles) {
            List<String> schemaNameList = sm.getSchemaNameList();

            List<String> fileContent = extractGzip(file);
            String fileHeader = fileContent.get(0);
            LocalDate fileDate = LocalDate.parse(fileHeader.substring(22, 30), formatter);

            if (!isLastDayOfMonth(fileDate)) {
                fileDate = toNearestFriday(fileDate);
            }

            String schemaNameSuffix = ("_" + fileDate).replace("-", "_");
            //todo - need to refactor this section
            if (schemaNameList.contains(sm.DRTU_SCHEMA_NAME + schemaNameSuffix)) {
                List<String> receivedFileNameList = sm.getReceivedFileNameList(sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
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
        List<String> schemaNameList = sm.getSchemaNameList();
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
        if (sm.getSchemaNameList().contains(sm.DRTU_SCHEMA_NAME + schemaNameSuffix))
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

}
