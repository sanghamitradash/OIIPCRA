package com.orsac.oiipcra.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

//import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FinancialBidResultDto {

    private String bid_id;
    private Date financial_bid_opening_date;
    private Integer workSlNoInTcn;
    private String workId;
    private String tenderAmountString;
    private String agency;
    private String licenseClass;
    private String amountQuoted;
    private String rateExcessLess;
    private String bidPosition;
    private Integer max_bid_capacity;
    private String maxBidCapacityString;

    private Double timeForCompletion;
    private Double capacity;

    private String maxCapacity2;

}
