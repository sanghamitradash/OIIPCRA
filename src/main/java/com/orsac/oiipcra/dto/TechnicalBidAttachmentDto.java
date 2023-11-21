package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalBidAttachmentDto {

    private String nameOfWork;
    private String bidIdentification;
    private String districtName;
    private String workId;
    private String schemeOfFunding;
    private String emdToBeDeposited;
    private String timeForCompletion;
    private Integer workSlNoInTcn;
    private Double creditFacilityRequired;
    private String charTenderAmount;
    private String charTenderOpeningDate;
    private Double creditFacilityAvailable;
    private Double charAnnualTurnover;

    private String agency;
    private String ValueAtWorkCompleted;
    private String finYrName;
    private String similarWorkValue;
    private String valueOfWorkCompleted;
    private Double amountPercentage;
    private String maxTurnOverYear;
    private String maxTurnover;
    private Double amountOfLiquidAssets;
    private String similarTypeWorkWorkValue;
    private Double paperCostSpecified;
    private Double workCompleted;
    private Double value;

    private String affidavitValidity;
    private String agreedBidValidity;
    private String bidSecuritySubmitted;

    private Integer tNoticeId;
    private String bidId;
    private String techBidDate;
    private Integer workSlNoTcn;
    private String distName;
    private String periodOfCompletion;
    private String tenderAmount;
    private String emdDeposited;
    private String procurementDate;
    private String tenderPublishDate;
    private String bidSubmissionDate;
    private String bidSubmissionDateRevised;
    private String preBidMeetingDate;
    private String newsPaperName;
    private String publishedDate;
    private String bidValidUpto;
    private Double clause3a1;
    private Double clause3a2;
    private Double clause3a3;
    private Double clause3b1;
    private Double clause3b2;
    private Double clause3g;
    private String financialBidOpeingDate;
    private String levelName;
    private String technicalBidApprovalDate;
    private String biderNameClass;
    private String agencyName;
    private String transactionType;
    private Boolean isBidderAwarded;
    private Integer awardType;
    private String tenderNotAwardedReason1;
    private String tenderNotAwardedReason2;
    private String tenderNotAwardedReason3;
    private String emdAmount;


    private Integer bidderId;
    private String valueAt;
    private String eSignedBid;
    private String eligibility;

    private String liquidAssetQualified;
    private String previousYearWeightage;
    private Double amount;
    private String responsiveOrNot;

}
