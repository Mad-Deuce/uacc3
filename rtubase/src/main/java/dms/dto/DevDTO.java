package dms.dto;


import lombok.Data;

import java.util.Date;


@Data
public class DevDTO {

    private Long id;

    private Long deviceTypeId;
    private Integer deviceTypeGroupId;
    private String group;
    private String type;

    private String number;
    private String releaseYear;
    private Date testDate;
    private Date nextTestDate;
    private String statusCode;
    private String statusDescription;
    private String deviceDetail;

    private String objectId;
    private String objectName;
//    private Object object;

    private Long placeId;
    private String description;
    private String region;
    private String regionTypeCode;
    private String regionTypeDescription;
    private String locate;
    private String locateTypeCode;
    private String locateTypeDescription;
    private String placeNumber;
    private String placeDetail;


}

