package com.orsac.oiipcra.bindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orsac.oiipcra.dto.TenderStipulationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderRequest {

   /* private Integer id;
    private String tenderName;
    private String code ;
    private Date publicationDate ;
    private String issuingAuthority;
    private String contactOfficer;
    private int tenderLevelId ;
    private int tenderTypeId ;
    private int winningBidId ;
    private String uiid ;
    private double estimatedValue;
    private double gst ;
    private String bidSubmissionAddress;
    private Date lastDateOfReceivingBid;
    private Date bidOpeningDate ;
    private Date technicalEvaluationDate ;
    private Date financialEvaluationDate ;
    private Date tenderResultDate;
    private int tenderStatusId ;
    private int activityId ;
    private int finyrId;
    private Date contractSigningDate;
    private int tenderId;
    private int distId ;
    private int blockId ;
    private int gpId ;
    private int villageId;
    private int divisionId;
    private int subDivisionId;
    private int sectionId;
    private int tankId;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;*/

    private Integer id;
    private String bidId;
    private Date approvalForProcurementDate ;
    private Date technicalBidOpeningDate ;
    private Date technicalBidOpeningDateRevised;
    private Date bidSubmissionDate;
    private Date bidSubmissionDateRevised;
    private Date financialBidOpeningDate;
    private String nameOfWork;
    private Integer preBidMeetingType;
    private Date preBidMeetingDate;
    private Date tenderPublicationDate ;
    private Date publicationPeriodUpto;
    //private Integer tenderId;
    //private Date dateOfFirstCorrigendum;
    //private Date dateOfSecondCorrigendum;
    private Integer tenderType;
    private Integer finyrId;
    private Integer tenderLevelId;
    private Date contractSigningDate;
    private Date tenderOpeningDate;
    private Date dateOfTenderNotice;
    private Integer estimateId;
    private Integer tenderStatus;
    private Integer activityId ;
    private Integer distId ;
    private Integer blockId ;
    private Integer gpId ;
    private Integer villageId;
    private Integer divisionId;
    private Integer subDivisionId;
    private Integer sectionId;
    private Integer meetingLocation;
    private Integer approvedStatus;
    private String approvedRemarks;
    private Integer approvedBy;
    private Date approvedOn;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Boolean active;
    private List<TenderPublishedInfo> tenderPublishedInfo;
    private TenderStipulationDto stipulation;
}
