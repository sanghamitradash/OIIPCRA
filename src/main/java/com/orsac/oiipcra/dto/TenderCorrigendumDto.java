package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderCorrigendumDto {
    private Integer id;
    private Integer tenderId;
    private Date technicalBidOpeningDate ;
    private Date bidSubmissionDate;
    private Date financialBidOpeningDate;
    private String nameOfWork;
    private Integer preBidMeetingType;
    private String preBidMeetingDate;
    private Date tenderPublicationDate ;
    private Date publicationPeriodUpto;
    private Boolean active;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer tenderType ;
    private Integer tenderLevelId;
    private Integer finyrId;
    private Date tenderOpeningDate;
    private Date dateOfTenderNotice;
    private Integer estimateId;
    private Integer tenderStatus;
    private Integer meetingLocation;

}
