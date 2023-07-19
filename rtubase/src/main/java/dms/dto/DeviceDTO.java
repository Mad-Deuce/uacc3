package dms.dto;


import dms.export.ExportInfo;
import dms.mapper.ExplicitDeviceMatcher;
import dms.standing.data.dock.val.ReplacementType;
import dms.validation.group.OnDeviceCreate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Data
public class DeviceDTO {

    @Null(groups = OnDeviceCreate.class)
    private Long id;

    @Min(groups = OnDeviceCreate.class, value = 10000000)
    private Long typeId;
    @ExportInfo(reportId = 1, position = 2, title = "Тип")
    private String typeName;

    private Integer typeGroupId;
    private String typeGroupName;

    @NotBlank(groups = OnDeviceCreate.class, message = "BLYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    @ExportInfo(reportId = 1, position = 3, title = "Номер")
    private String number;
    @Size(groups = OnDeviceCreate.class, min = 4, max = 4)
    @ExportInfo(reportId = 1, position = 4, title = "Рік виготовлення")
    private String releaseYear;
    private String releaseYearMin;
    private String releaseYearMax;

    @ExportInfo(reportId = 1, position = 5, title = "Дата перевірки")
    private Date testDate;
    private Date testDateMin;
    private Date testDateMax;
    @ExportInfo(reportId = 1, position = 6, title = "Дата наступної перевірки")
    private Date nextTestDate;
    private Date nextTestDateMin;
    private Date nextTestDateMax;

    private Date extraNextTestDate;

    private Integer replacementPeriod;
    private Integer replacementPeriodMin;
    private Integer replacementPeriodMax;

    private ReplacementType replacementType;

    private String status;
    @ExportInfo(reportId = 1, position = 8, title = "Статус")
    private String statusComment;
    @ExportInfo(reportId = 1, position = 9, title = "Коментар")
    private String detail;

    private String railwayId;
    @ExportInfo(reportId = 1, position = 0, title = "Залізниця")
    private String railwayName;
    private String subdivisionId;
    @ExportInfo(reportId = 1, position = 1, title = "Підрозділ")
    private String subdivisionShortName;

    private String rtdId;
    private String rtdName;

    @Size(groups = OnDeviceCreate.class, min = 4, max = 4)
    private String facilityId;
    @ExportInfo(reportId = 1, position = 7, title = "Об'єкт")
    private String facilityName;

    private Long locationId;
    @ExportInfo(reportId = 1, position = 15, title = "Найменування по схемі")
    private String description;
    @ExportInfo(reportId = 1, position = 11, title = "Розташування")
    private String region;
    private String regionType;
    @ExportInfo(reportId = 1, position = 10, title = "Категорія розташування")
    private String regionTypeComment;
    @ExportInfo(reportId = 1, position = 13, title = "Розташування 2")
    private String locate;
    private String locateType;
    @ExportInfo(reportId = 1, position = 12, title = "Категорія розташування 2")
    private String locateTypeComment;
    @ExportInfo(reportId = 1, position = 14, title = "Місце")
    private String placeNumber;
    @ExportInfo(reportId = 1, position = 16, title = "Коментар до місця")
    private String locationDetail;

    private String clsId;

    private List<ExplicitDeviceMatcher> activeProperties;

    private String opcl;
    private String tid_pr;
    private String tid_rg;
    private String scode;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO dto = (DeviceDTO) o;
        return typeId.equals(dto.typeId) && number.equals(dto.number) && releaseYear.equals(dto.releaseYear) && testDate.equals(dto.testDate) && nextTestDate.equals(dto.nextTestDate) && replacementPeriod.equals(dto.replacementPeriod) && status.equals(dto.status) && Objects.equals(detail, dto.detail) && facilityId.equals(dto.facilityId) && Objects.equals(locationId, dto.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeId, number, releaseYear, testDate, nextTestDate, replacementPeriod, status, detail, facilityId, locationId);
    }

    public static DeviceDTO stringify(String str) {
        DeviceDTO result = new DeviceDTO();
        if (str.length() == 135) {
            result.setId(Long.parseLong(str.substring(0, 10).trim()));                              //10    trim
            result.setTypeId(Long.parseLong(str.substring(10, 20).trim()));                         //10    trim
            result.setNumber(str.substring(20, 30).trim());                                         //10    trim
            result.setTypeName(str.substring(30, 50).trim());                                       //20    trim
            result.setReleaseYear(str.substring(50, 54).trim());                                    //4

            result.setTestDate(strToDate(str.substring(54, 62)));                        //8
            result.setNextTestDate(strToDate(str.substring(62, 70)));                        //8


//            result.setTestDate(Date.valueOf(str.substring(58, 62) + "-"
//                    + str.substring(56, 58) + "-" + str.substring(54, 56)));                        //8
//            result.setNextTestDate(Date.valueOf(str.substring(66, 70) + "-"
//                    + str.substring(64, 66) + "-" + str.substring(62, 64)));                        //8

            result.setReplacementPeriod(Integer.parseInt(str.substring(70, 75).trim()));            //5     trim

            if (str.substring(75, 89).trim().length() > 0) {
                result.setLocationId(Long.parseLong(str.substring(75, 89).trim()));                 //14    "" to null
            }

            result.setFacilityId((str.substring(89, 96).trim()));                                   //7     trim
            result.setStatus((str.substring(96, 98).trim()));                                       //2     trim
            result.setOpcl((str.substring(98, 99).trim()));                                         //1     OPCL not used

            if (str.substring(99, 103).trim().length() > 0) {
                result.setTid_pr((str.substring(99, 103).trim()));                                  //4     TID_PR not used "" to null
            }
            if (str.substring(103, 107).trim().length() > 0) {
                result.setTid_rg((str.substring(103, 107).trim()));                                 //4     TID_RG not used "" to null

            }

            result.setScode("F");                                                                   //1     SCODE not used  "" to null
            return result;
        } else return null;
    }

    private static Date strToDate(String str) {
        if (str.trim().length() != 8) return null;
        try {
            return Date.valueOf(str.substring(4) + "-"
                    + str.substring(2, 4) + "-" + str.substring(0, 2));
        } catch (Exception e) {
            return null;
        }

    }

}

