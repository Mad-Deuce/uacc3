package dms.service.schema;

import dms.dao.ReceiveManager;
import dms.dao.SchemaManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class SchemaServiceImpl implements SchemaService {

    @Autowired
    private SchemaManager sm;
    @Autowired
    private ReceiveManager rm;

    final String INP_DIR_PATH = "rtubase/src/main/resources/pd_files";
    final String EXTRACT_DIR_PATH = "rtubase/src/main/resources/pd_files/extracted";
    final String RENAMED_DIR_PATH = "rtubase/src/main/resources/pd_files/renamed";
    final String SUCCESS_DIR_PATH = "rtubase/src/main/resources/pd_files/success";
    final String ERROR_DIR_PATH = "rtubase/src/main/resources/pd_files/error";

    @Override
    public void updateDBByPDFiles() throws Exception {

        List<File> sourceFiles = getFiles();
        List<File> extractedFiles = extractGzip(sourceFiles);
        List<File> renamedFiles = renameFiles(extractedFiles);
        HashMap<String, List<File>> groupedFiles = groupFileByDate(renamedFiles);

        log.info("------------------------------------------------");
        //manually check valid pdFiles name
        //read a files in spec Dir by matching
        //WARN: don`t mix files in spec Dir from different year
        //group files by date
        //if files quantity is zero do nothing and return
        //Проверить количество файов и их сигнатуру 2 файла p, d на одно ШЧ с одной датой,
        // сравнить с предшествующей схемой и выдать уведомление о возможности обновления
        //create abs group by filename (substr 6-8) and get group names as suffix to schema name
        //convert group name to valid date suffix: last working date in month or nearest to friday(friday -2,+4)
        //get all exists schema in DB by matcher    exSch
        //for each by group
        //check if schema already exists by group name - file in this group not processed - return
        //create  schema with name "drtu"+suffix
        //restore DB from Empty Backup
        //receive pdfiles in group

        //send notification to UI about exists freshest data
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

    private List<File> extractGzip(List<File> sourceFileList) throws Exception {
        List<File> result = new ArrayList<>();
        if (sourceFileList.isEmpty()) return result;
        String errorMessage = "dms: Directory for Extracted Files not accessible";
        File targetDir = new File(EXTRACT_DIR_PATH);
        if (!targetDir.exists()) {
            if (!targetDir.mkdir()) throw new Exception(errorMessage);
        }

        for (File file : sourceFileList) {
            File targetFile = new File(EXTRACT_DIR_PATH + "/" + file.getName().substring(0, file.getName().length() - 4));

            byte[] buffer = new byte[1024];
            FileInputStream fileIn = new FileInputStream(file);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            int bytes_read;
            while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytes_read);
            }
            gZIPInputStream.close();
            fileOutputStream.close();
            result.add(targetFile);
        }

        return result;
    }

    private List<File> renameFiles(List<File> fileList) throws IOException {
        //  example: input filename d1011b21 => output filename d1011_2023-11-21
        List<File> result = new ArrayList<>();
        if (fileList.isEmpty()) return result;

        File targetDir = new File(RENAMED_DIR_PATH);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        for (File file : fileList) {
            String header = Files.readAllLines(file.toPath(), Charset.forName("windows-1251")).get(0);
            LocalDate fileDate = LocalDate.parse(header.substring(22, 30), formatter);
            if (!isLastDayOfMonth(fileDate)) {
                fileDate = toNearestFriday(fileDate);
            }
            String nameSuffix = "_" + fileDate;
            String fileName = file.getName();
            String newFileName = fileName.substring(0, 5) + nameSuffix;
            File newFile = new File(RENAMED_DIR_PATH + "/" + newFileName);
            if (file.renameTo(newFile)) {
                result.add(newFile);
                file.delete();
            }
        }

        return result;
    }

    private boolean isLastDayOfMonth(LocalDate inputDate) {
        return inputDate.equals(inputDate.withDayOfMonth(inputDate.getMonth().length(inputDate.isLeapYear())));
    }

    private LocalDate toNearestFriday(LocalDate inputDate) {
        int dayOfWeekNum = inputDate.getDayOfWeek().getValue();
        int offset = (((dayOfWeekNum + 4) % 7) - 2) * -1;
        return inputDate.plusDays(offset);
    }

    private HashMap<String, List<File>> groupFileByDate(List<File> fileList) {
        HashMap<String, List<File>> result = new HashMap<>();
        if (fileList.isEmpty()) return result;
        for (File file : fileList) {
            String key = file.getName().substring(5, 16);//  example: filename d1011_2023-11-21 => key _2023-11-21
            if (!result.containsKey(key)) result.put(key, new ArrayList<>());
            result.get(key).add(file);
        }
        return result;
    }

}
