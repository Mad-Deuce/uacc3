package dms.filter;

import dms.mapper.ExplicitDeviceMatcher;
import lombok.Data;

import java.sql.Date;
import java.util.List;


@Data
public class DeviceFilter {

    private Long id;

    private Long typeId;
    private String typeName;

    private Integer typeGroupId;
    private String typeGroupName;

    private String number;
    private String releaseYear;
    private String releaseYearMin;
    private String releaseYearMax;

    private Date testDate;
    private Date testDateMin;
    private Date testDateMax;
    private Date nextTestDate;
    private Date nextTestDateMin;
    private Date nextTestDateMax;

    private Integer replacementPeriod;
    private Integer replacementPeriodMin;
    private Integer replacementPeriodMax;
    private String status;
    private String statusComment;
    private String detail;

    private String railwayId;
    private String railwayName;
    private String subdivisionId;
    private String subdivisionShortName;

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

    private String clsId;

    private List<ExplicitDeviceMatcher> activeProperties;
}
