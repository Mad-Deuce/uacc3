package dms.service.db;

import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.ReceiveManager;
import dms.dao.SchemaManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SchemaManager sm;
    @Autowired
    private ReceiveManager rm;
    @Autowired
    private TenantIdentifierResolver currentTenant;

    final String INP_DIR_PATH = "rtubase/src/main/resources/pd_files";

    @Override
    public void receivePDFiles() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        List<File> sourceFiles = getFiles();

        for (File file : sourceFiles) {
            List<String> schemaNameList = sm.getSchemaNameList();

            List<String> fileContent = extractGzip(file);
            String fileHeader = fileContent.get(0);
            LocalDate fileDate = LocalDate.parse(fileHeader.substring(22, 30), formatter);

            if (!isLastDayOfMonth(fileDate)) {
                fileDate = toNearestFriday(fileDate);
            }

            String schemaNameSuffix = ("_" + fileDate).replace("-", "_");
            if (schemaNameList.contains(sm.DRTU_SCHEMA_NAME + schemaNameSuffix)) {
                List<String> receivedFileNameList = sm.getReceivedFileNameList(sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
                if (!receivedFileNameList.contains(fileHeader.substring(0, 12).toUpperCase())) {
                    sm.removeSchema(sm.DRTU_SCHEMA_NAME);
                    sm.renameSchema(sm.DRTU_SCHEMA_NAME + schemaNameSuffix, sm.DRTU_SCHEMA_NAME);
                    sm.createDevicesMainView();
                    rm.receivePDFileAlt(fileContent);
                    sm.renameSchema(sm.DRTU_SCHEMA_NAME, sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
                }
            } else {
                sm.removeSchema(sm.DRTU_SCHEMA_NAME);
                sm.removeSchema(sm.DOCK_SCHEMA_NAME);
                sm.restoreEmpty();
                sm.createDevicesMainView();
                rm.receivePDFileAlt(fileContent);
                sm.renameSchema(sm.DRTU_SCHEMA_NAME, sm.DRTU_SCHEMA_NAME + schemaNameSuffix);
            }

            file.delete();
        }
    }

    //    @Override
    @PostConstruct
    @Scheduled(initialDelay = 20000, fixedDelay = 30000)
    public void isPDDirEmpty() throws Exception {
        log.info("------------------check PD dir -----------------");
        if (!getFiles().isEmpty()) receivePDFiles();
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
        String errorMessage = "dms: Directory for P,D Files not accessible";
        File dir = new File(INP_DIR_PATH);
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
            throw new Exception(errorMessage);
        }
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
