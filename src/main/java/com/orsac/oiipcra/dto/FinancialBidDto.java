package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialBidDto {

    private Integer tenderId;
    private Integer workId;
    private Integer bidderId;
    private Boolean isBidAwarded;
    private String bidId;
    private Date financialBidOpeningDate;
    private Double tenderAmount;
    private String tenderNotAwardedReason;
    private Double timeForCompletion;
    private Double maxBidCapacity;
    private Integer agencyId;
    private String agencyName;
    private String panNo;
    private Date technicalBidOpeningDate;


}
