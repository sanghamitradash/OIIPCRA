package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOfBidsDto {

    private Integer agencyId;
    private String agency;
    private String licenseClass;
    private String gstInNo;
    private Long contactNo;
    private String contact;
    private Date licenseExpiring;
    private String bidId;
    private Date tenderOpeningDate;
    private Double maxTurnOver;
    private String packageId;
    private Double tenderAmount;
    private String bidValidity;
    private BigDecimal amountQuoted;
    private Double amountPercentage;
    private Double similarWorkValue;
    private BigDecimal annualTurnover;
    private Double creditFacilityRequired;
    private String tenderBidId;
    private Double creditFacilityAvailable;


    private String charTenderOpeningDate;
    private String charLicenseExpiring;
    private String charAmountQuoted;
    private String charTenderAmount;
    private String charAnnualTurnover;
    private String charCreditFacilityRequired;
}
