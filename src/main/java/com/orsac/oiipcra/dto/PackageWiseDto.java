package com.orsac.oiipcra.dto;


import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageWiseDto {

    private String projectName;
    private Integer projectId;
    private String nameOfWork;
    private String districtName;
    private String blockName;
    private String packageId;
    private String charTenderOpeningDate;
    private String bidId;
    private String charTenderAmount;
    private String charFinancialTurnover;

    private String agency;
    private String licenseValidity;
    private String similarWorkValue;
    private String maxTurnOver;
    private Double creditFacilityAvailable;
    private String bidValidity;
    private Double amountPercentage;
    private String charAmountQuoted;
    private String similarWorkValueReq;
    private Double creditFacilityRequired;
    private Double percentageCompleted;

    private String bidSecurity;

    private Integer tankId;

}
