package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dpr_information")
public class DprInformationEntity {
    @Id
    @SequenceGenerator(name = "dpr_information_sequence", sequenceName = "dpr_information_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dpr_information_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "observerd_high_flood_level")
    private Double observerdHighFloodLevel;

    @Column(name = "met_station_name")
    private String metStationName;

    @Column(name = "high_land_ha")
    private Double highLandHa;

    @Column(name = "medium_land_ha")
    private Double mediumLandHa;

    @Column(name = "low_land_ha")
    private Double lowLandHa;

    @Column(name = "water_availability_period")
    private Integer waterAvailabilityPeriod;

    @Column(name = "gross_command_area_gca_ha")
    private Double grossCommandAreaGcaHa;

    @Column(name = "existing_khariff_paddy_area_ha")
    private Double existingKhariffPaddyAreaHa;

    @Column(name = "lost_ayacut_ha")
    private Double lostAyacutHa;

    @Column(name = "estd_cost_of_imp_rs")
    private Double estdCostOfImpRs;

    @Column(name = "estimate_for_cada_rs")
    private Double estimateForCadaRs;

    @Column(name = "cost_ha_improvement_rs")
    private Double costHaImprovementRs;

    @Column(name = "cost_of_cada_rs")
    private Double costOfCadaRs;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "approved_status")
    private Integer approvedStatus;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn = new Date(System.currentTimeMillis());

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;
}
