package dms.property.name.constant;

import dms.entity.DevObjEntity;
import dms.standing.data.converter.StatusConverter;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.DObjRtuEntity;
import dms.standing.data.entity.SDevEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@AllArgsConstructor
public enum DevPropertyNameConstant {
    ID("id","id"),

    TYPE_ID("typeId","sDev.id"),
    TYPE_NAME("typeName","sDev.dtype"),

    TYPE_GROUP_ID("typeGroupId","sDev.grid.grid"),
    TYPE_GROUP_NAME("typeGroupName","sDev.grid.name"),

    NUMBER("number","num"),
    RELEASE_YEAR("releaseYear","myear"),
    TEST_DATE("testDate","dTkip"),
    NEXT_TEST_DATE("nextTestDate","dNkip"),
    REPLACEMENT_PERIOD("replacementPeriod","tZam"),
    STATUS("statusCode","status"),
    STATUS_CODE("statusCode","status.name"),
    STATUS_COMMENT("statusComment","status.comm"),
    DETAIL("detail","detail"),

    OBJECT_ID("objectId","dObjRtu.id"),
    OBJECT_NAME("objectName",""),

    PLACE_ID("placeId","devObj.id"),
    DESCRIPTION("description","devObj.nshem"),
    REGION("region","devObj.region"),
    REGION_TYPE_CODE("regionTypeCode","devObj.regionType.name"),
    REGION_TYPE_COMMENT("regionTypeComment","devObj.regionType.comm"),
    LOCATE("locate","devObj.locate"),
    LOCATE_TYPE_CODE("locateTypeCode","devObj.locateType.name"),
    LOCATE_TYPE_COMMENT("locateTypeComment","devObj.locateType.comm"),
    PLACE_NUMBER("placeNumber","devObj.nplace"),
    PLACE_DETAIL("placeDetail","devObj.detail"),
    ;
    private final String dtoPropertyName;
    private final String entityPropertyName;


    
}
