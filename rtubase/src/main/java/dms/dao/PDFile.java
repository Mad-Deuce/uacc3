package dms.dao;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Data
public class PDFile {

    private Integer HEADER_LENGTH = 42;
    private Integer BODY_LENGTH = 135;

    private MetaData metaData;
    //    private List<String> content;
//    private List<DeviceDTO> specificContent;
    private List<DRowData> dContent;
    private List<PRowData> pContent;


    public PDFile(List<String> fileContent) {
//        content = new LinkedList<>();
        dContent = new LinkedList<>();
        pContent = new LinkedList<>();
//        specificContent = new LinkedList<>();
        fileContent.forEach(item -> {
            if (item.length() == HEADER_LENGTH) metaData = new MetaData(item);
            if (item.length() == BODY_LENGTH) {
                if (metaData.type.equals("D")) {
//                    specificContent.add(DeviceDTO.stringify(item));
                    dContent.add(new DRowData(item));
                } else if (metaData.type.equals("P")) {
//                    specificContent.add(DeviceDTO.stringify(item));
                    pContent.add(new PRowData(item));
                } else {
                    throw new RuntimeException("Not required meta data");
                }

            }
        });
    }

//    @Deprecated
//    public List<Long> getIdList() {
//        List<Long> idList = new ArrayList<>();
////        specificContent.forEach(item -> idList.add(item.getId()));
//        return idList;
//    }

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

        MetaData(String header) {
            if (header.length() != 42) return;
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

    @Data
    public class DRowData {
        private Long id;
        private Long devId;
        private String num;
        private String typeName;
        private String mYear;
        private Date dTKip;
        private Date dNKip;
        private Integer tZam;
        private Long idObj;
        private String objCode;
        private String ps;
        private String opcl;
        private String tIdPr;
        private String tIdRg;

        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        public DRowData(String dString) {
            if (dString.length() != 135) return;
            this.id = Long.parseLong(dString.substring(0, 10).trim());
            this.devId = Long.parseLong(dString.substring(10, 20).trim());
            this.num = dString.substring(20, 30).trim();
            this.typeName = dString.substring(30, 50).trim();
            this.mYear = dString.substring(50, 54).trim();
            if (!dString.substring(54, 62).trim().equals(""))
                this.dTKip = Date.valueOf(LocalDate.parse(dString.substring(54, 62).trim(), formatter));
            if (!dString.substring(62, 70).trim().equals(""))
                this.dNKip = Date.valueOf(LocalDate.parse(dString.substring(62, 70).trim(), formatter));
            this.tZam = Integer.parseInt(dString.substring(70, 75).trim());
            if (dString.substring(75, 89).trim().length() > 0)
                this.idObj = Long.parseLong(dString.substring(75, 89).trim());
            this.objCode = dString.substring(89, 96).trim();
            this.ps = dString.substring(96, 98).trim();
            this.opcl = dString.substring(98, 99).trim();
            if (dString.substring(99, 103).trim().length() > 0)
                this.tIdPr = dString.substring(99, 103).trim();
            if (dString.substring(103, 107).trim().length() > 0)
                this.tIdRg = dString.substring(103, 107).trim();
        }
    }

    @Data
    public class PRowData {
        private Long id;
        private String locateT;
        private String locate;
        private String regionT;
        private String region;
        private String nPlace;
        private String nShem;
        private String objCode;
        private String opcl;
        private String objName;

        public PRowData(String pString) {
            if (pString.length() != 135) return;
            this.id = Long.parseLong(pString.substring(0, 14).trim());
            this.locateT = pString.substring(14, 16).trim();
            this.locate = pString.substring(16, 36).trim();
            this.regionT = pString.substring(36, 38).trim();
            this.region = pString.substring(38, 58).trim();
            this.nPlace = pString.substring(58, 62).trim();
            this.nShem = pString.substring(62, 90).trim();
            this.objCode = pString.substring(90, 97).trim();
            this.opcl = pString.substring(97, 107).trim();
            this.objName = pString.substring(107).trim();
        }
    }
}