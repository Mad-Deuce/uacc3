package dms.entity;

import dms.entity.id.OverdueDevsStatsEntityId;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
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
    private Long normDevsQuantity;

    @Basic
    @Column(name = "pass_devs_quantity", nullable = false, columnDefinition = "int4")
    private Long passDevsQuantity;

    @Basic
    @Column(name = "exp_devs_quantity", nullable = false, columnDefinition = "int4")
    private Long expDevsQuantity;

    @Basic
    @Column(name = "exp_warranty_devs_quantity", nullable = false, columnDefinition = "int4")
    private Long expWarrantyDevsQuantity;

//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Id
//    @Column(name = "id", nullable = false)
//    private long id;

}
