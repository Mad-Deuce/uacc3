package dms.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExplicitDeviceMatcher {
    ID("id","id","id"),

    TYPE_ID("typeId","typeId","type.id"),
    TYPE_NAME("typeName","typeName","type.name"),

    TYPE_GROUP_ID("typeGroupId","typeGroupId","type.group.id"),
    TYPE_GROUP_NAME("typeGroupName","typeGroupName","type.group.name"),

    NUMBER("number","number","number"),
    RELEASE_YEAR("releaseYear","releaseYear","releaseYear"),

    TEST_DATE("testDate","testDate","testDate"),
    TEST_DATE_MIN("testDateMin","testDateMin","testDate"),
    TEST_DATE_MAX("testDateMax","testDateMax","testDate"),

    NEXT_TEST_DATE("nextTestDate","nextTestDate","nextTestDate"),
    NEXT_TEST_DATE_MIN("nextTestDateMin","nextTestDateMin","nextTestDate"),
    NEXT_TEST_DATE_MAX("nextTestDateMax","nextTestDateMax","nextTestDate"),

    REPLACEMENT_PERIOD("replacementPeriod","replacementPeriod","replacementPeriod"),
    STATUS("status","status","status"),
    STATUS_COMMENT("statusComment","statusComment","status.comment"),
    DETAIL("detail","detail","detail"),

    OBJECT_ID("facilityId","facilityId","facility.id"),
    OBJECT_NAME("facilityName","facilityName","facility.getName"),

    LOCATION_ID("locationId","locationId","location.id"),
    DESCRIPTION("description","description","location.description"),
    REGION("region","region","location.region"),
    REGION_TYPE("regionType","regionType","location.regionType"),
    REGION_TYPE_COMMENT("regionTypeComment","regionTypeComment","location.regionType.comment"),
    LOCATE("locate","locate","location.locate"),
    LOCATE_TYPE("locateType","locateType","location.locateType"),
    LOCATE_TYPE_COMMENT("locateTypeComment","locateTypeComment","location.locateType.comment"),
    PLACE_NUMBER("placeNumber","placeNumber","location.placeNumber"),
    PLACE_DETAIL("locationDetail","locationDetail","location.detail"),
    ;
    private final String dtoPropertyName;
    private final String filterPropertyName;
    private final String entityPropertyName;


    
}