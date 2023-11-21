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
@Table(name = "soil_details")
public class SoilDetailsEntity {
    @Id
    @SequenceGenerator(name = "catchment_details_sequence", sequenceName = "catchment_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catchment_details_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "soil_type")
    private Integer soilType;

    @Column(name = "soil_type_details")
    private String soilTypeDetails;

    @Column(name = "soil_type_area_sqkm")
    private Double soilTypeAreaSqkm;

    @Column(name = "dpr_information_id")
    private Integer dprInformationId;

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
