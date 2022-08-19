package com.dms_uz.rtubase.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DObjRtuEntity {

    @Id
    @GeneratedValue
    private String id;

    private Integer kodRtu;

}
