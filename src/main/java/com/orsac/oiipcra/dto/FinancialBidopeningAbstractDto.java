package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialBidopeningAbstractDto {
    private String bidId;
    private Date financialBidOpeningDate;
    private String workId;
    private String agency;
    private String licenseClass;
    private BigDecimal tenderAmount;
    private Long bidPrice;
    private Long quoted;
    private Long emdAmount;
    private String remarks;
    private Integer workSlNoInTcn;
    private Date licenseValidity;
    private String tenderAmountString;
    private BigDecimal maxTurnOver;
    private String turnOver;
    private Double creditAvailability;
    private String overallBidValidity;
    private BigDecimal emdDeposited;
    private String emdDepositedString;
    private String validity;
    private Double apsRequired;
    private String awardType;
    private String loaIssue;
    private String bar;
    private String bidP;
    private String sumTm;
    private String sumBidP;
    private String financialValidityWork;
    private  String finalQuoted;
    private String rateExcessLess;
    private String slNo;



}
