package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderResultDto {

    private Integer id;
    private Integer tenderId;
    private Integer workId;
    private Integer bidderId;
    private Boolean lotteryRequired;
    private Date dateOfLottery;
    private Integer awardType;
    private String acceptanceLetterNo;
    private Integer contractId;
    private Double completionPeriod;
    private Date completionDate;
    private String agreementNo;
    private String legalCase;
    private String remarks;
    private Boolean isActive=true;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Boolean tenderAwarded;
    private Date reviewFinBidDate;
    private String tenderNotAwardedReason;

}
