package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankCropCycleMasterDto {

    private Integer id;
    private Integer yearId;
    private Double singleCrop;
    private Double doubleCrop;
    private Double tripleCrop;
    private Integer projectId;
    private Double commandAreaHa;
    private Double ciGee;
    private Double ciGis;
    private Double ci4Permanent;
    private Boolean active;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;
    private Date updatedOn;
    private String tankName;
    private String yearName;



}
