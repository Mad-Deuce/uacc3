package dms.dto;


import dms.property.name.constant.DevPropertyNameConstant;
import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Data
public class DevDTO {

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

    private List<DevPropertyNameConstant> activeProperties;
}

