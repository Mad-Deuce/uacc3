package dms.service.files;

import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.ReceiveManager;
import dms.dao.schema.SchemaDao;
import dms.service.stats.StatsService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;


@Service
@Slf4j
public class FileReceiveServiceImpl implements FileReceiveService {

    @Autowired
    FilesStorageService filesStorageService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private SchemaDao schemaDao;
    @Autowired
    private ReceiveManager rm;
    @Autowired
    private TenantIdentifierResolver currentTenant;

    @Override
    @PostConstruct
    @Scheduled(initialDelay = 20000, fixedDelay = 30000)
    public void checkUploadDir() throws Exception {
        log.info("------------------check upload dir -----------------");
        List<File> sourceFiles = getFiles();
        if (!sourceFiles.isEmpty()) receivePDFiles(sourceFiles);
    }

    private List<File> getFiles()  {
        FileFilter filter = f -> (f.isFile()
                && f.getName().length() == 12
                && f.getName().matches("[pd]\\d{4}\\w\\d{2}\\.\\d{3}"));
        File uploadDir = filesStorageService.getUploadDir();
        return Arrays.stream(Objects.requireNonNull(uploadDir.listFiles(filter))).toList();
    }

    private void receivePDFiles(List<File> sourceFiles) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        if (!schemaDao.isSchemaExists(schemaDao.DRTU_SCHEMA_NAME)) {
            throw new Exception("schema: " + schemaDao.DRTU_SCHEMA_NAME + " is not exists");
        }

        for (File file : sourceFiles) {
            List<String> schemaNameList = schemaDao.getSchemaNameListLikeString(schemaDao.DRTU_SCHEMA_NAME + "_%");

            List<String> fileContent = extractGzip(file);
            String fileHeader = fileContent.get(0);
            LocalDate fileDate = LocalDate.parse(fileHeader.substring(22, 30), formatter);

            if (!isLastDayOfMonth(fileDate)) {
                fileDate = toNearestFriday(fileDate);
            }

            String schemaNameSuffix = ("_" + fileDate).replace("-", "_");
            if (schemaNameList.contains(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix)) {
                List<String> receivedFileNameList = rm.getReceivedFileNameList(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
                if (!receivedFileNameList.contains(fileHeader.substring(0, 12).toUpperCase())) {
                    schemaDao.removeSchema(rm.DRTU_SCHEMA_TEMP_NAME);
//                    schemaDao.cloneSchema(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix, rm.DRTU_SCHEMA_TEMP_NAME);
                    schemaDao.renameSchema(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix, rm.DRTU_SCHEMA_TEMP_NAME);
                    rm.saveFileContent(fileContent);
//                    schemaDao.removeSchema(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
//                    schemaDao.cloneSchema(rm.DRTU_SCHEMA_TEMP_NAME, schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
//                    schemaDao.removeSchema(rm.DRTU_SCHEMA_TEMP_NAME);
                    schemaDao.renameSchema(rm.DRTU_SCHEMA_TEMP_NAME, schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
                }
            } else {
                schemaDao.cloneSchema(schemaDao.DRTU_SCHEMA_NAME, rm.DRTU_SCHEMA_TEMP_NAME);
                rm.saveFileContent(fileContent);
                schemaDao.cloneSchema(rm.DRTU_SCHEMA_TEMP_NAME, schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
                schemaDao.removeSchema(rm.DRTU_SCHEMA_TEMP_NAME);
//                schemaDao.renameSchema(rm.DRTU_SCHEMA_TEMP_NAME, schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
            }
            currentTenant.setCurrentTenant(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
            statsService.saveCurrentSchemaOverdueDevsStats();
            if (file.delete()) log.info("file: " + file.getName() + " was processed and was removed.");
        }
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
