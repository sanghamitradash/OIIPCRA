package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderInfo {
    private Integer id;
    private String bidId;
    private Date approvalForProcurementDate;
    private Date technicalBidOpeningDate;
    private Date technicalBidOpeningDateRevised;
    private Date bidSubmissionDate;
    private Date bidSubmissionDateRevised;
    private Date financialBidOpeningDate;
    private String nameOfWork;
    private Integer preBidMeetingType;
    private String meetingType;
    private String preBidMeetingDate;
    private Date tenderPublicationDate;
    private Date publicationPeriodUpto;
    /*private Date dateOfFirstCorrigendum;
    private Date dateOfSecondCorrigendum;*/
    private Integer tenderTypeId;
    private String tenderType;
    private Integer tenderLevelId;
    private String tenderlLevel;
    private Integer finyrId;
    private String financialYear;
    private Date tenderOpeningDate;
    private Date dateOfTenderNotice;
    private Integer estimateId;
    private Integer tenderStatus;
    private String status;
    private Integer meetingLocation;
    private String location;
    private Integer nextId;
    private Integer activityId;
    private String techBidDate;
    private Integer contractId;
    private String ContractNumber;
    private Integer tenderNoticeId;
    private String  workIdentificationCode;


   /* private Integer biddingType;
    private Integer typeOfWork;
    private Integer workSlNoInTcn;
    private String workIdentificationCode;
    private String name_of_work;
    private Integer distId;
    private Integer blockId;
    private Double tenderAmount;
    private Double paperCost;
    private Double emdToBeDeposited;
    private Double timeForCompletion;
    private Integer contactNo;
    private Integer tender_level_id;

    private Integer workId;
    private Double similarWorkValue;
    private Double similarWorkCompletion;
    private Double annualFinancialTurnover;
    private Double previousYrWeightage;
    private Double creditLinesAmount;
    private Double bidCapacityTurnover;
    private Double completionOfWorkValueTarget;
    private Double turnoverTarget;
    private Double liquidAssetTarget;*/

}
