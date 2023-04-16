package dms.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ExplicitDeviceMatcher {
    ID("id", "id", "id"),

    TYPE_ID("typeId", "typeId", "type.id"),
    TYPE_NAME("typeName", "typeName", "type.name"),

    TYPE_GROUP_ID("typeGroupId", "typeGroupId", "type.group.id"),
    TYPE_GROUP_NAME("typeGroupName", "typeGroupName", "type.group.name"),

    NUMBER("number", "number", "number"),
    RELEASE_YEAR("releaseYear", "releaseYear", "releaseYear"),
    RELEASE_YEAR_MIN("releaseYearMin", "releaseYearMin", "releaseYear"),
    RELEASE_YEAR_MAX("releaseYearMax", "releaseYearMax", "releaseYear"),

    TEST_DATE("testDate", "testDate", "testDate"),
    TEST_DATE_MIN("testDateMin", "testDateMin", "testDate"),
    TEST_DATE_MAX("testDateMax", "testDateMax", "testDate"),

    NEXT_TEST_DATE("nextTestDate", "nextTestDate", "nextTestDate"),
    NEXT_TEST_DATE_MIN("nextTestDateMin", "nextTestDateMin", "nextTestDate"),
    NEXT_TEST_DATE_MAX("nextTestDateMax", "nextTestDateMax", "nextTestDate"),

    REPLACEMENT_PERIOD("replacementPeriod", "replacementPeriod", "replacementPeriod"),
    REPLACEMENT_PERIOD_MIN("replacementPeriodMin", "replacementPeriodMin", "replacementPeriod"),
    REPLACEMENT_PERIOD_MAX("replacementPeriodMax", "replacementPeriodMax", "replacementPeriod"),

//    REPLACEMENT_TYPE("replacementType","replacementType","status"),
//    REPLACEMENT_TYPE_COMMENT("replacementTypeComment","replacementTypeComment","status.comment"),

    STATUS("status", "status", "status"),
    STATUS_COMMENT("statusComment", "statusComment", "status.comment"),
    DETAIL("detail", "detail", "detail"),

    RAILWAY_ID("railwayId", "railwayId", "facility.subdivision.railway.id"),
    RAILWAY_NAME("railwayName", "railwayName", "facility.subdivision.railway.name"),
    SUBDIVISION_ID("subdivisionId", "subdivisionId", "facility.subdivision.id"),
    SUBDIVISION_SHORT_NAME("subdivisionShortName", "subdivisionShortName", "facility.subdivision.shortName"),

    FACILITY_ID("facilityId", "facilityId", "facility.id"),
    FACILITY_NAME("facilityName", "facilityName", "facility.name"),

    LOCATION_ID("locationId", "locationId", "location.id"),
    DESCRIPTION("description", "description", "location.description"),
    REGION("region", "region", "location.region"),
    REGION_TYPE("regionType", "regionType", "location.regionType"),
    REGION_TYPE_COMMENT("regionTypeComment", "regionTypeComment", "location.regionType.comment"),
    LOCATE("locate", "locate", "location.locate"),
    LOCATE_TYPE("locateType", "locateType", "location.locateType"),
    LOCATE_TYPE_COMMENT("locateTypeComment", "locateTypeComment", "location.locateType.comment"),
    PLACE_NUMBER("placeNumber", "placeNumber", "location.placeNumber"),
    PLACE_DETAIL("locationDetail", "locationDetail", "location.detail"),

    CLS_ID("clsId", "clsId", "detail"),
    ;
    private final String dtoPropertyName;
    private final String filterPropertyName;
    private final String entityPropertyName;

    public static ExplicitDeviceMatcher getInstanceByFilterPropertyName(String filterPropertyName) {
        Objects.requireNonNull(filterPropertyName, "NULL arg in ExplicitDeviceMatcher methods");
        return Arrays.stream(ExplicitDeviceMatcher.values())
                .filter(item ->
                        Objects.requireNonNull(item.getFilterPropertyName(), "NULL field in ExplicitDeviceMatcher methods")
                                .equals(filterPropertyName))
                .findFirst().orElseThrow();
    }

    public String getEntityPropertyNameLastPart() {
        String[] bits = this.entityPropertyName.split("\\.");
        return bits[bits.length - 1];
    }

}
