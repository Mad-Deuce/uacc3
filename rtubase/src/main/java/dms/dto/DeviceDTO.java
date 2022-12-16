package dms.dto;


import dms.mapper.ExplicitDeviceMatcher;
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
    private String status;
    private String statusComment;
    private String detail;

    private String facilityId;
    private String facilityName;

    private Long locationId;
    private String description;
    private String region;
    private String regionType;
    private String regionTypeComment;
    private String locate;
    private String locateType;
    private String locateTypeComment;
    private String placeNumber;
    private String locationDetail;

    private List<ExplicitDeviceMatcher> activeProperties;
}

