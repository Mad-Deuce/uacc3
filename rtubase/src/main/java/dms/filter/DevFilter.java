package dms.filter;

import lombok.Data;

import java.sql.Date;

@Data
public class DevFilter {
    private Long id;

    private Long typeId;
    private String typeName;

    private Integer typeGroupId;
    private String typeGroupName;

    private String number;
    private String releaseYear;
    private Date testDate;
    private Date nextTestDate;
    private Integer replacementPeriod;
    private String statusCode;
    private String statusComment;
    private String detail;

    private String objectId;
    private String objectName;

    private Long placeId;
    private String description;
    private String region;
    private String regionTypeCode;
    private String regionTypeComment;
    private String locate;
    private String locateTypeCode;
    private String locateTypeComment;
    private String placeNumber;
    private String placeDetail;
}