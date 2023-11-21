package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cropping_pattern")
public class CroppingPattern {

    @Id
    @SequenceGenerator(name = "cropping_pattern_sequence", sequenceName = "cropping_pattern_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cropping_pattern_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "cropping_pattern_type")
    private Integer croppingPatternType;

    @Column(name = "village_id")
    private Integer villageId;

    @Column(name = "ayacut_in_ha")
    private Double ayacutInHa;

    @Column(name = "kh_paddy_ha")
    private Double khPaddyHa;

    @Column(name = "kh_non_paddy_ha")
    private Double khNonPaddyHa;

    @Column(name = "rabi_paddy_ha")
    private Double rabiPaddyHa;

    @Column(name = "rabi_non_paddy_ha")
    private Double rabiNonPaddyHa;

    @Column(name="is_active",nullable = false)
    private Boolean isActive;

    @Column(name = "created_by")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;

    @Column(name = "tank_other_details_id")
    private Integer tankOtherDetailsId;

}
