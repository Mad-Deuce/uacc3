package dms.dao;

import dms.dto.DeviceDTO;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class PDFile {

    private Integer HEADER_LENGTH = 42;
    private Integer BODY_LENGTH = 135;

    private MetaData metaData;
    private List<String> content;
    private List<DeviceDTO> specificContent;

    public PDFile(List<String> fileContent) {
        content = new LinkedList<>();
        specificContent = new LinkedList<>();
        fileContent.forEach(item -> {
            if (item.length() == HEADER_LENGTH) metaData = new MetaData(item);
            if (item.length() == BODY_LENGTH) {
//                content.add(item);
                if (metaData.type.equals("D")) specificContent.add(DeviceDTO.stringify(item));

            }
        });
    }

    public List<Long> getIdList() {
        List<Long> idList = new ArrayList<>();
        specificContent.forEach(item -> idList.add(item.getId()));
        return idList;
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
        private final String name;
        private final String type;

        MetaData(String header) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

            version = header.substring(35, 42);
            sCode = header.substring(34, 35);
            timestamp = Timestamp.valueOf(LocalDateTime.parse(header.substring(22, 34), formatter));
            objectCode = header.substring(18, 22);
            recordsQuantity = Integer.parseInt(header.substring(12, 18).trim());

            name = header.substring(0, 12);
            switch (header.substring(0, 1)) {
                case "p" -> type = "P";
                case "d" -> type = "D";
                default -> type = "";
            }

        }
    }
}