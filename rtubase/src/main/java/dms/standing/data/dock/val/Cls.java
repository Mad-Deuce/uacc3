package dms.standing.data.dock.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Cls {
    CLS2("2", "Ukrzaliznytsia", "Укрзалізниця"),
    CLS131("131", "Railways", "Railways"),
    CLS132("132", "Subdivisions", "Subdivisions"),
    CLS133("133", "RTD", "RtdFacilities"),

    CLS2111("2111", "Stations", "Stations"),
    CLS2112("2112", "Stages", "Stages"),

    CLS21111("21111", "Devices", "Station Devices"),
    CLS21112("21112", "Overdue Devices", "Station Devices with Expired Next Test Date"),
    CLS21114("21114", "AVZ", "Station Emergency Recovery Stock"),

    CLS21121("21121", "Devices", "Stage Devices"),
    CLS21122("21122", "Overdue Devices", "Stage Devices with Expired Next Test Date"),

    CLS21151("21151", "OBF RTD", "RTD Revolving Fund"),
    CLS21152("21152", "AVZ RTD", "RTD Emergency Recovery Stock"),
    ;
    private final String id;
    private final String name;
    private final String comment;

}
