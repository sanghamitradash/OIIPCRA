package com.orsac.oiipcra.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crop_details")
public class CropDetailsEntity {
    @Id
    @SequenceGenerator(name = "crop_details_sequence", sequenceName = "crop_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crop_details_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "hydrology_data_id")
    private Integer hydrologyDataId;
    @Column(name = "crop_type")
    private Integer cropType;
    @Column(name = "crop_area")
    private Double cropArea;
    @Column(name = "water_demand_in_ha")
    private Double waterDemandInHa;
    @Column(name = "water_available_in_ha")
    private Double waterAvailableInHa;

    @Column(name = "is_active")
    private Boolean active;

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




}
