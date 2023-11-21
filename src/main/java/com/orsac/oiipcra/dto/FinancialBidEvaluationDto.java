package com.orsac.oiipcra.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialBidEvaluationDto {

    private Integer tenderNoticeId;
    private Integer tenderId;
    private String  nameOfWork;
    private String  bidIdentification;
    private String  workId;
    private String  estimatedCost;
    private String  distName;

    private String techBidDate;

    private String agencyName;
    private String bidPrice;
    private Double percentageExcessLess;
    private String excessLess;
    private String excess;
    private String reviewTechBidDate;
    private String finBidOpeningDate;
    private String reviewFinBidDate;

    private Integer awardType;
    private String bidderNameClass;
    private Integer agencyId;

    private String packageId;
    private Double estimated;
    private String awardTypeName;
    private Double amountOfBid;
    private String maxAnnualTurnOver;
    private String mobilisationAdvance;
    private String equipmentAdvance;
    private String performanceSecurity;
    private String addPerfSecurity;
    private String bidValidityUpto;
    private String miDivisionInCharge;
    private String commaWiseWorkId;

    private String balanceBidCapacityString;
    private Double balanceBidCapacity;
    private Double maxBidCapacity;
    private String maxBidCapacityString;
    private String maxBidCapacityPdfValue;
    private String balanceBidCapacityPdfValue;






}
