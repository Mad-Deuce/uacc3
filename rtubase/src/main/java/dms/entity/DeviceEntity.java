package dms.entity;


import dms.standing.data.converter.StatusConverter;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.FacilityEntity;
import dms.standing.data.entity.DeviceTypeEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;


@Setter
@Getter
@EqualsAndHashCode(of = {"type", "number", "releaseYear"})
@ToString(of = {"type", "number", "releaseYear"})
@NoArgsConstructor
@Entity
@Table(name = "dev", schema = "drtu", catalog = "rtubase")
public class DeviceEntity implements Serializable {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_obj", referencedColumnName = "id", columnDefinition = "NUMERIC(14,0)")
    private DeviceLocationEntity location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devid", referencedColumnName = "id")
    private DeviceTypeEntity type;

    @Basic
    @Column(name = "num", length = 10)
    private String number;

    @Basic
    @Column(name = "myear", length = 4)
    private String releaseYear;

    @Convert(converter = StatusConverter.class)
    @Column(name = "ps", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private Status status;

    @Basic
    @Column(name = "d_create")
    private Date createDate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC(10,0)")
    @Setter
    private Long id;

    @Basic
    @Column(name = "d_nkip")
    private Date nextTestDate;

    @Basic
    @Column(name = "d_tkip")
    private Date testDate;

    @Basic
    @Column(name = "t_zam", columnDefinition = "NUMERIC(5,0)")
    private Integer replacementPeriod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "obj_code")
    private FacilityEntity facility;

    @Basic
    @Column(name = "ok_send", length = -1, columnDefinition = "BPCHAR")
    private String okSend;

    @Basic
    @Column(name = "opcl", length = -1, columnDefinition = "BPCHAR")
    private String opcl;

    @Basic
    @Column(name = "tid_pr", length = 4)
    private String tidPr;

    @Basic
    @Column(name = "tid_rg", length = 4)
    private String tidRg;

    @Basic
    @Column(name = "scode", length = -1, columnDefinition = "BPCHAR")
    private String scode;

    @Basic
    @Column(name = "detail", length = 160)
    private String detail;
}
