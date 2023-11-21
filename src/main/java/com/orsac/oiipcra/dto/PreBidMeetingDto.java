package com.orsac.oiipcra.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreBidMeetingDto {

    private Integer id;
    private Integer meetingProceedingId;
    private String  preBidClarificationsSoughtByBidders;
    private String  preBidClarificationsDisposedWithReply;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Boolean isActive;

    private Integer noOfParticipants;
    private Integer noOfOnlineClarification;
    private Integer noOfClarificationSought;
    private Integer clarificationDisposedOnline;
    private Integer clarificationDisposedInPreBid;
    private String bidId;
    private String technicalBidOpeningDate;
    private String preBidMeetingDate;

}
