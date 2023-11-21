package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderResponse {

    private Integer id;
    private String tenderName;
    private String tenderCode ;
    private Date publicationDate ;
    private String issuingAuthority;
    private String contactOfficer;
    private Integer tenderLevelId ;
    private String tenderLevelName;
    private Integer tenderTypeId ;
    private Integer winningBidId ;
    private String uiid ;
    private double estimatedValue;
    private double gst ;
    private String bidSubmissionAddress;
    private Date lastDateOfReceivingBid;
    private Date bidOpeningDate ;
    private Date technicalEvaluationDate ;
    private Date financialEvaluationDate ;
    private Date tenderResultDate;
    private Integer tenderStatusId ;
    private String tenderStatus;
    private Integer activityId;
    private String activityName;
    private Integer finyrId;
    private String financialYearName;
    private Date contractSigningDate;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
}
