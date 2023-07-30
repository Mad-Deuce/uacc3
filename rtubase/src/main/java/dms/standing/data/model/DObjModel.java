package dms.standing.data.model;


import dms.model.DevObjModel;
import lombok.Data;

import java.util.Objects;

@Data
public class DObjModel {
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

    public DObjModel(DevObjModel devObjModel) {
        this.id = devObjModel.getObjCode();
        this.kodObkt = Integer.parseInt(devObjModel.getObjCode().substring(4, 7));
        if (this.kodObkt < 200) {
            this.kind = "S";
            this.cls = "ST";
        } else {
            this.kind = "P";
            this.cls = "PG";
        }
        this.nameObj = devObjModel.getObjName();
        this.kodDor = devObjModel.getObjCode().substring(0, 1);
        this.kodDist = Integer.parseInt(devObjModel.getObjCode().substring(1, 3));
        this.kodRtu = Integer.parseInt(devObjModel.getObjCode().substring(3, 4));
        this.kodObj = devObjModel.getObjCode().substring(4, 7);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DObjModel dObjModel = (DObjModel) o;
        return id.equals(dObjModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
