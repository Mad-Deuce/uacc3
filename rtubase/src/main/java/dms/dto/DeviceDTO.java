package dms.dto;


import dms.property.name.constant.DevicePropertyNameMapping;
import lombok.Data;

import java.sql.Date;
import java.util.List;


@Data
public class DeviceDTO {

    private Long id;

    private Long typeId;
    private String typeName;

    private Integer typeGroupId;
    private String typeGroupName;

    private String number;
    private String releaseYear;

    private Date testDate;
    private Date testDateMin;
    private Date testDateMax;
    private Date nextTestDate;
    private Date nextTestDateMin;
    private Date nextTestDateMax;

    private Integer replacementPeriod;
    private String statusName;
    private String statusComment;
    private String detail;

    private String objectId;
    private String objectName;

    private Long locationId;
    private String description;
    private String region;
    private String regionTypeName;
    private String regionTypeComment;
    private String locate;
    private String locateTypeName;
    private String locateTypeComment;
    private String placeNumber;
    private String locationDetail;

    private List<DevicePropertyNameMapping> activeProperties;
}
