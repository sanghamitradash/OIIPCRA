package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DraftTenderNoticeDto {

    private Integer id;
    private String bidId;
    private String tenderAmountString;
    private String variesToString;
    private String variesFromString;
    private String nameOfWork;
    private Integer noOfWorks;
    private BigDecimal variesFrom;
    private BigDecimal variesTo;
    private BigDecimal tenderAmount;
    private Date availabilityDocumentFrom;
    private Date availabilityDocumentTo;
    private Date receiptBidFrom;
    private Date receiptBidTo;
    private Date preBidMeetingDate;
    private Date tenderOpeningDate;
    private Date tenderPublicationDate;
    private Date previousDate;
    private Integer memoNumber;
   // private String bidId;
    private Integer tenderNoticeId;


}
