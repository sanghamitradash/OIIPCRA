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
public class PhysicalProgressConsultancyDto {
    private Integer id;
    private String progressUnit;
    private String targetUnitName;
    private Integer targetUnit;
    private Double targetTime;
    private Double actualTime;
    private Double targetCompletion;
    private Double targetPayment;
    private Double actualCompletion;
    private Double actualPayment;
    private Integer contractId;
    private String contractName;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;
    private Date updatedOn;
    private String progressStatusName;
    private  Integer progressStatusId;
}
