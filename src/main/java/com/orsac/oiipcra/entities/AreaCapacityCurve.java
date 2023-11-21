package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "area_capacity_curve")
public class AreaCapacityCurve {

    @Id
    @SequenceGenerator(name = "area_capacity_curve_sequence", sequenceName = "area_capacity_curve_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "area_capacity_curve_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "lowest_res_contour_m")
    private Double lowestResContourM;

    @Column(name = "contour_interval_in_m")
    private Double contourIntervalInM;

    @Column(name = "contour_level_in_m")
    private Double contourLevelInM;

    @Column(name = "squared_area_in_sqm")
    private Double squaredAreaInSqm;

    @Column(name="is_active",nullable = false)
    private Boolean isActive=true;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "dam_weir_details")
    private Integer damWeirDetails;

}
