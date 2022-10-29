package dms.property.name.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DevPropertyNameMapping {
    ID("id","id","id"),

    TYPE_ID("typeId","typeId","sDev.id"),
    TYPE_NAME("typeName","typeName","sDev.dtype"),

    TYPE_GROUP_ID("typeGroupId","typeGroupId","sDev.grid.grid"),
    TYPE_GROUP_NAME("typeGroupName","typeGroupName","sDev.grid.name"),

    NUMBER("number","number","num"),
    RELEASE_YEAR("releaseYear","releaseYear","myear"),

    TEST_DATE("testDate","testDate","dTkip"),
    TEST_DATE_MIN("testDateMin","testDateMin","dTkip"),
    TEST_DATE_MAX("testDateMax","testDateMax","dTkip"),

    NEXT_TEST_DATE("nextTestDate","nextTestDate","dNkip"),
    NEXT_TEST_DATE_MIN("nextTestDateMin","nextTestDateMin","dNkip"),
    NEXT_TEST_DATE_MAX("nextTestDateMax","nextTestDateMax","dNkip"),

    REPLACEMENT_PERIOD("replacementPeriod","replacementPeriod","tZam"),
    STATUS("statusCode","statusCode","status"),
    STATUS_CODE("statusCode","statusCode","status.name"),
    STATUS_COMMENT("statusComment","statusComment","status.comm"),
    DETAIL("detail","detail","detail"),

    OBJECT_ID("objectId","objectId","dObjRtu.id"),
    OBJECT_NAME("objectName","objectName",""),

    PLACE_ID("placeId","placeId","devObj.id"),
    DESCRIPTION("description","description","devObj.nshem"),
    REGION("region","region","devObj.region"),
    REGION_TYPE_CODE("regionTypeCode","regionTypeCode","devObj.regionType.name"),
    REGION_TYPE_COMMENT("regionTypeComment","regionTypeComment","devObj.regionType.comm"),
    LOCATE("locate","locate","devObj.locate"),
    LOCATE_TYPE_CODE("locateTypeCode","locateTypeCode","devObj.locateType.name"),
    LOCATE_TYPE_COMMENT("locateTypeComment","locateTypeComment","devObj.locateType.comm"),
    PLACE_NUMBER("placeNumber","placeNumber","devObj.nplace"),
    PLACE_DETAIL("placeDetail","placeDetail","devObj.detail"),
    ;
    private final String dtoPropertyName;
    private final String filterPropertyName;
    private final String entityPropertyName;


    
}
