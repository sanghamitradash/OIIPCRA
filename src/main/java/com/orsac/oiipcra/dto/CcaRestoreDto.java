package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcaRestoreDto {

    private Integer id;
    private String finYrName;
    private Integer HydrologyDataId;

    private Integer FinYr;

    private Double CcaRestore;

    private Double Expenditure;

    private Double Irr;
    private  Double BenifitCostRatio;

    private Boolean active;


    private Integer createdBy;

    private Date createdOn;


    private Integer updatedBy;

    private Date updatedOn;

}
