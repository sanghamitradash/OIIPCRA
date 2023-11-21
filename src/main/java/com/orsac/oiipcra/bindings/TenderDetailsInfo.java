package com.orsac.oiipcra.bindings;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderDetailsInfo {


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
    private Date preBidMeetingDate;
    private Date tenderPublicationDate;
    private Date publicationPeriodUpto;
    private Integer tenderTypeId;
    private String tenderType;
    private Integer tenderLevelId;
    private String tenderlLevel;
    private Integer finyrId;
    private String financialYear;
    private Date tenderOpeningDate;
    private Date dateOfTenderNotice;
    private Integer estimateId;
    private Integer tenderStatusId;
    private String tenderStatus;
    private Integer meetingLocation;
    private String location;

    private Integer workId;
    private Integer biddingTypeId;
    private String biddingType;
    private Integer typeOfWork;
    private String workType;
    private Integer workSlNoInTcn;
    private String workIdentificationCode;
    private String workName;
    private Double tenderAmount;
    private Double paperCost;
    private Double emdToBeDeposited;
    private Double timeForCompletion;
    private Integer contactNo;
    private Integer circleId;
    private Integer divisionId;
    private String divisionName;
    private Integer eeId;
    private Integer subDivisionId;
    private String subDivisionName;
    private Integer subDivisionOfficer;
    private Integer projectId;
    private String projectName;


}
