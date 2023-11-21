package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShlcMeetingProceedingsDto {

    private Integer id;
    private Integer shlcMeetingId;
    private String memorandumForDiscussion;
    private String detailsForDiscussion;
    private String committeeProceedings;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer userId;
}
