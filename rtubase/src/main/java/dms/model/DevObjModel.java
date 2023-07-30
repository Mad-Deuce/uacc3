package dms.model;

import lombok.Data;

@Data
public class DevObjModel {
    private Long id;
    private String objCode;
    private String locate;
    private String nPlace;
    private String nShem;
    private String locateT;
    private String region;
    private String regionT;
    private String okSend;
    private String opcl;

    private String sCode;
    private String detail;

    private String objName;

    public DevObjModel(String pString) {
        if (pString.length() != 135) return;
        this.id = Long.parseLong(pString.substring(0, 14).trim());
        this.locateT = pString.substring(14, 16).trim();
        this.locate = pString.substring(16, 36).trim();
        this.regionT = pString.substring(36, 38).trim();
        this.region = pString.substring(38, 58).trim();
        this.nPlace = pString.substring(58, 62).trim();
        this.nShem = pString.substring(62, 90).trim();
        this.objCode = pString.substring(90, 97).trim();
        this.opcl = pString.substring(97, 107).trim();
        this.objName = pString.substring(107).trim();
    }
}
