package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreBidClarificationsDto {

    private Integer id;
    private Integer meetingProceedingId;
    private String  preBidClarificationsSoughtByBidders;
    private String  preBidClarificationsDisposedWithReply;
}
