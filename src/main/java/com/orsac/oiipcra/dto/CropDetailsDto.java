package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropDetailsDto {

    private Integer id;
    private Integer hydrologyDataId;

    private Integer cropType;

    private Double cropArea;

    private Double water_demand_in_ha;

    private Double water_available_in_ha;


    private Boolean active;


    private Integer createdBy;

    private Date createdOn;


    private Integer updatedBy;

    private Date updatedOn;

}
