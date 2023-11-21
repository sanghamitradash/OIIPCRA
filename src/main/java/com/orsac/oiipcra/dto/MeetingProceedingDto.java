package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingProceedingDto {

    private Integer id;
    private Integer bidId;
    private Integer meetingType;
    private Integer noOfParticipants;
    private Integer noOfOnlineClarification;
    private Integer noOfClarificationSought;
    private Integer clarificationDisposedOnline;
    private Integer clarificationDisposedInPreBid;
    private Boolean active;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String tender;
    private String meetingTypeName;

    private List<CommitteeMembersDto> committeeMembersDto;
    private List<PreBidClarificationsDto> preBidClarificationsDto;

}
