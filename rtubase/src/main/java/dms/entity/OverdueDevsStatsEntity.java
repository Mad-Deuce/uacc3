package dms.entity;

import dms.entity.id.OverdueDevsStatsEntityId;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@IdClass(OverdueDevsStatsEntityId.class)
@Table(name = "overdue_devs_stats", schema = "_stats", catalog = "rtubase")
public class OverdueDevsStatsEntity {

    @Id
    @Basic
    @Column(name = "object_id", nullable = false, length = -1)
    private String objectId;

    @Id
    @Basic
    @Column(name = "stats_date", nullable = false)
    private LocalDate statsDate;

    @Basic
    @Column(name = "object_name", nullable = false, length = -1)
    private String objectName;

    @Basic
    @Column(name = "norm_devs_quantity", nullable = false, columnDefinition = "int4")
    private Long normalDevicesQuantity;

    @Basic
    @Column(name = "pass_devs_quantity", nullable = false, columnDefinition = "int4")
    private Long passiveDevicesQuantity;

    @Basic
    @Column(name = "exp_devs_quantity", nullable = false, columnDefinition = "int4")
    private Long expiredDevicesQuantity;

    @Basic
    @Column(name = "exp_warranty_devs_quantity", nullable = false, columnDefinition = "int4")
    private Long expiredWarrantyDevicesQuantity;

//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Id
//    @Column(name = "id", nullable = false)
//    private long id;


}

