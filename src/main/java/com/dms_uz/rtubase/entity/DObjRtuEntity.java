package com.dms_uz.rtubase.entity;


import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DObjRtuEntity {


    @Id
    @GeneratedValue
    private String id;

    private Integer kodRtu;

    public Integer getKodRtu() {
        return kodRtu;
    }

    public void setKodRtu(Integer kodRtu) {
        this.kodRtu = kodRtu;
    }



    public String getId() {
        return id;
    }
}
