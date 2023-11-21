package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.apache.bcel.generic.LineNumberGen;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tank_other_details")
public class TankOtherDetails {

    @Id
    @SequenceGenerator(name = "tank_other_details_sequence", sequenceName = "tank_other_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tank_other_details_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "present_ayacut_khariff")
    private Double presentAyacutKhariff;

    @Column(name = "present_ayacut_rabi")
    private Double presentAyacutRabi;

    @Column(name = "river_basin")
    private Integer riverBasin;

    @Column(name = "agro_climatic_zone")
    private String agroClimaticZone;

    @Column(name = "nearest_village")
    private Integer nearestVillage;

    @Column(name = "population_of_nearest_village")
    private Integer populationOfNearestVillage;

    @Column(name = "project_constructed_year")
    private Integer projectConstructedYear;

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

    @Column(name = "approved_status")
    private Integer approvedStatus;

    @Column(name = "topo_sheet_no")
    private String topoSheetNo;

}
