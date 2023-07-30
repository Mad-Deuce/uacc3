package dms.model;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class DevModel {
    private Long idObj;
    private Long devId;
    private String num;
    private String mYear;
    private String ps;
    private Date dCreate;
    private Long id;
    private Date dNKip;
    private Date dTKip;
    private Integer tZam;
    private String objCode;
    private String okSend;
    private String opcl;
    private String tIdPr;
    private String tIdRg;

    private String sCode;
    private String detail;

    private String typeName;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

    public DevModel(String dString) {
        if (dString.length() != 135) return;
        this.id = Long.parseLong(dString.substring(0, 10).trim());
        this.devId = Long.parseLong(dString.substring(10, 20).trim());
        this.num = dString.substring(20, 30).trim();
        this.typeName = dString.substring(30, 50).trim();
        this.mYear = dString.substring(50, 54).trim();
        if (!dString.substring(54, 62).trim().equals(""))
            this.dTKip = Date.valueOf(LocalDate.parse(dString.substring(54, 62).trim(), formatter));
        if (!dString.substring(62, 70).trim().equals(""))
            this.dNKip = Date.valueOf(LocalDate.parse(dString.substring(62, 70).trim(), formatter));
        this.tZam = Integer.parseInt(dString.substring(70, 75).trim());
        if (dString.substring(75, 89).trim().length() > 0)
            this.idObj = Long.parseLong(dString.substring(75, 89).trim());
        this.objCode = dString.substring(89, 96).trim();
        this.ps = dString.substring(96, 98).trim();
        this.opcl = dString.substring(98, 99).trim();
        if (dString.substring(99, 103).trim().length() > 0)
            this.tIdPr = dString.substring(99, 103).trim();
        if (dString.substring(103, 107).trim().length() > 0)
            this.tIdRg = dString.substring(103, 107).trim();
    }
}


