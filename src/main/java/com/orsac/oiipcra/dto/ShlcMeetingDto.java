package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShlcMeetingDto {

    private Integer id;
    private Integer meetingSerialNo;
    private String meetingSequenceNo;
    private Date dateOfMeeting;
    private String proceedingsIssuedLtNc;
    private Date committeeFormedDate;
    private String voteOfThanks;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer userId;
    private Date proceedingsIssuedDate;

    private List<ShlcMeetingMembersDto> shlcMeetingMembersDto;
    private List<ShlcMeetingProceedingsDto> shlcMeetingProceedingsDto;
}
