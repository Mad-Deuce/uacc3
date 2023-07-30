package dms.model;

import lombok.Data;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
public class PDFileModel {

    private Integer HEADER_LENGTH = 42;
    private Integer BODY_LENGTH = 135;

    private MetaData metaData;
    private List<DevModel> dContent;
    private List<DevObjModel> pContent;

    public PDFileModel(List<String> fileContent) {
        dContent = new LinkedList<>();
        pContent = new LinkedList<>();
        fileContent.forEach(item -> {
            if (item.length() == HEADER_LENGTH) metaData = new MetaData(item);
            if (item.length() == BODY_LENGTH) {
                if (metaData.type.equals("D")) {
                    dContent.add(new DevModel(item));
                } else if (metaData.type.equals("P")) {
                    pContent.add(new DevObjModel(item));
                } else {
                    throw new RuntimeException("Not required meta data");
                }
            }
        });
    }

    public void increaseNotProcessedRecordsQuantity(Integer quantity) {
        metaData.notProcessedRecordsQuantity = metaData.notProcessedRecordsQuantity + quantity;
    }

    @Data
    public class MetaData {

        private String version;
        private String sCode;
        private Timestamp timestamp;
        private String objectCode;
        private Integer recordsQuantity;
        private Integer notProcessedRecordsQuantity = 0;
        private String name;
        private String type;

        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

        MetaData(String header) {
            if (header.length() != HEADER_LENGTH) return;

            version = header.substring(35, 42);
            sCode = header.substring(34, 35);
            timestamp = Timestamp.valueOf(LocalDateTime.parse(header.substring(22, 34), formatter));
            objectCode = header.substring(18, 22);
            recordsQuantity = Integer.parseInt(header.substring(12, 18).trim());

            name = header.substring(0, 12);
            switch (header.substring(0, 1)) {
                case "p" -> type = "P";
                case "d" -> type = "D";
                default -> type = null;
            }
        }
    }
}