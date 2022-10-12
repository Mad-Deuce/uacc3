package dms.entity.standing.data;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "d_obj", schema = "drtu", catalog = "rtubase")
public class DObjEntity extends DObjRtuEntity {
    @Basic
    @Column(name = "kod_dor", nullable = false, length = -1, columnDefinition = "BPCHAR")
    private String kodDor;

    @Basic
    @Column(name = "kod_otd", length = -1, columnDefinition = "BPCHAR")
    private String kodOtd;

    @Basic
    @Column(name = "kod_dist", nullable = false, precision = 0)
    private BigInteger kodDist;

//    @Basic
//    @Column(name = "kod_rtu", nullable = false, precision = 0)
//    private Integer kodRtu;

    @Basic
    @Column(name = "kod_obkt", nullable = false, columnDefinition = "NUMERIC(3,0)")
    private int kodObkt;

    @Basic
    @Column(name = "kod_obj", nullable = true, length = 3)
    private String kodObj;

    @Basic
    @Column(name = "name_obj", nullable = true, length = 50)
    private String nameObj;

    @Transient
    private String name;
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Id
//    @Column(name = "id", nullable = false, length = 7)
//    private String id;

    @Basic
    @Column(name = "kind", length = -1, columnDefinition = "BPCHAR")
    private String kind;

    @Basic
    @Column(name = "cls", nullable = true, length = 2)
    private String cls;

    public String getKodDor() {
        return kodDor;
    }

    public void setKodDor(String kodDor) {
        this.kodDor = kodDor;
    }

    public String getKodOtd() {
        return kodOtd;
    }

    public void setKodOtd(String kodOtd) {
        this.kodOtd = kodOtd;
    }

    public BigInteger getKodDist() {
        return kodDist;
    }

    public void setKodDist(BigInteger kodDist) {
        this.kodDist = kodDist;
    }

//    public Integer getKodRtu() {
//        return kodRtu;
//    }
//
//    public void setKodRtu(Integer kodRtu) {
//        this.kodRtu = kodRtu;
//    }

    public int getKodObkt() {
        return kodObkt;
    }

    public void setKodObkt(int kodObkt) {
        this.kodObkt = kodObkt;
    }

    public String getKodObj() {
        return kodObj;
    }

    public void setKodObj(String kodObj) {
        this.kodObj = kodObj;
    }

    public String getNameObj() {
        return nameObj;
    }

    public void setNameObj(String nameObj) {
        this.nameObj = nameObj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }


}
