package dms.standing.data.dto;

import dms.dao.PDFile;
import lombok.Data;

import java.util.Objects;

@Data
public class DObjDTO {
    private String kodDor;
    private String kodOtd;
    private Integer kodDist;
    private Integer kodRtu;
    private Integer kodObkt;
    private String kodObj;
    private String nameObj;
    private String id;
    private String kind;
    private String cls;

    public DObjDTO(PDFile.PRowData pRowData) {
        this.id = pRowData.getObjCode();
        this.kodObkt = Integer.parseInt(pRowData.getObjCode().substring(4, 7));
        if (this.kodObkt < 200) {
            this.kind = "S";
            this.cls = "ST";
        } else {
            this.kind = "P";
            this.cls = "PG";
        }
        this.nameObj = pRowData.getObjName();
        this.kodDor = pRowData.getObjCode().substring(0, 1);
        this.kodDist = Integer.parseInt(pRowData.getObjCode().substring(1, 3));
        this.kodRtu = Integer.parseInt(pRowData.getObjCode().substring(3, 4));
        this.kodObj = pRowData.getObjCode().substring(4, 7);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DObjDTO dObjDTO = (DObjDTO) o;
        return id.equals(dObjDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
