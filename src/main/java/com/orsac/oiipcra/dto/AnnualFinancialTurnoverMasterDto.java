package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnualFinancialTurnoverMasterDto {
    private Integer id;
    private Integer bidderId;
    private Integer agencyId;
    private Integer finyrId;
    private String financialYearName;
    private Double value;
    private Boolean maximum;
    private Boolean active;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;
    private Date updatedOn;
    private Double maxBidCapacity;

    private Double equivalent;
    private Double turnOverRequired;
    private String turnOverMaximumYear;
    private Double turnOverMaximumValue;
}
