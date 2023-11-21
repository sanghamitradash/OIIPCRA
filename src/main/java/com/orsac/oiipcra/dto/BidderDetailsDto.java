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
import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidderDetailsDto {
    private Integer id;

    private Integer tenderId;

    private String workId;

    private Integer agencyId;
    private String agencyName;

    private Boolean licenseValidity;

    private Boolean affidavitValidity;

    private Double paperCostAmount;
    private Integer paperCostSubmissionType;

    private Boolean isPaperCostValid;

    private String emdBankName;

    private Double emdAmount;


    private String emdDepositType;

    private Boolean isEmdValid;
    
    private String emdValidity;

    private String paperCostValidity;

    private String affidavitValid;
    private Boolean completionWorkValueQualified;


    private Boolean turnOverQualified;


    private Boolean liquidAssetQualified;

    private Boolean isBidQualified;

    private Double maxBidCapacity;

    private Double workInHand;

    private Integer bidResult;

    private Integer awardType;

    private Integer bidFinalRank;

    private Boolean active;

    private Integer createdBy;

    private Date createdOn;

    private Integer updatedBy;

    private Date updatedOn;
    private Integer bidderCategoryId;

    private Boolean isBidAwarded;

    private Integer flagId;

    private String bidId;
    private String biddingType;
    private String tenderOpeningdateChar;
    private String nameOfWork;
    private String projectName;
    private Integer workSlNoInTcn;
    private Double tenderAmount;
    private Double paperCost;
    private Double emdToDeposit;
    private Integer timeOfCompletion;
    private String licenseClass;
    private String licenseExpiring;
    private String gstIn;
    private BigDecimal contactNo;
    private String distName;
    private String divName;
    private String blockName;
    private String bidderCategory;
    private Double paperCostSubmit;
    private String licenseName;
    private String tenderAmountChar;
    private Integer bidderId;
    private Date technicalBidOpeningDate;
    private Double annualTurnover;
    private Double similarWorkValue;

    private Boolean isActive ;
    private String liquidAssetQualifiedString;
    private Boolean isCreditQualified;
    private BigDecimal tenderAmtBigDeci;
    private BigDecimal tenderAmtforLiquidTopCalculation;
}
