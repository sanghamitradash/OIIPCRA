package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.TenderResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialBidMasterDto {

    private Integer id;
    private Integer tenderId;
    private Integer workId;
    private Integer bidderId;
    private Double amountQuoted;
    private Double amountPercentage;
    private Double additionalPerformanceSecRequired;
    private Double additionalSubmitted;
    private Double balanceApsRequired;
    private Date reviewTechBidDate;
    private Date reviewFinBidDate;
    private Double workInHand;
    private Double balanceBidCapacity;
    private Boolean active;
    private Integer createdBy;
    private Integer updatedBy;
    private Date financialBidOpeningDate;

    private TenderResultDto tenderResult;

}
