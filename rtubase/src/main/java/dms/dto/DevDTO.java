package dms.dto;


import lombok.Data;

import java.util.Date;


@Data
public class DevDTO {

    private String group;
    private String type;
    private String number;
    private String releaseYear;
    private Date testDate;
    private Date nextTestDate;
    private String statusCode;
    private String deviceDetail;

    private String description;
    private String region;
    private String regionType;
    private String locate;
    private String locateType;
    private String placeNumber;
    private String placeDetail;

    private Long deviceId;
    private Long deviceTypeId;
    private Integer deviceTypeGroupId;
    private Long objectId;
    private Long placeId;

}

