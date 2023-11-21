package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialBidInfo {
    private Integer id;
    private Double tenderAmount;
    private Double amountQuoted;
    private Double amountPercentage;
    private Double additionalPerformanceSecRequired;
    private Double additionalSubmitted;
    private Double balanceApsRequired;
    private Double maxBidCapacity;
    private Double workInHand;
    private Double balanceBidCapacity;
    private Date reviewTechBidDate;
    private Date reviewFinBidDate;
    private Boolean isBidAwarded;
    private String tenderNotAwardedReason;
    private String agreementNo;
    private String legalCase;
    private Date financialBidOpeningDate;
    private Boolean lotteryRequired;
    private Date dateOfLottery;
    private Integer awardTypeId;
    private String awardType;
    private String acceptanceLetterNo;
    private Double completionPeriod;
    private Double timeForCompletion;
    private String remarks;
    private Integer flagId;
    private String financialBidOpening;
    private String awardedTypeName;
    private String bidId;
    private String notice;
    private String agencyName;
    private Boolean tenderAwarded;
    private String reviewBidDate;
    private Date technicalBidOpeningDate;

}
