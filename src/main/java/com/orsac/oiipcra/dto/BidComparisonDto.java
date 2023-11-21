package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidComparisonDto {
    private  Integer id;
    private String bidId;
    private  Integer workSlNo;
    private String workIdentificationCode;
    private String dateOfOpening;
    private String nameOfWork;
    private Integer workId;
    private  String schemeOfFunding;
    private Integer distId;
    private String districtName;
    private  Integer blockId;
    private String blockName;
    private  Double estimatedAmountForTender;
    private Double paperCostSpecified;
    private Double emdToBeDeposited;
    private Integer timeForCompletion;
    private String divisionName;
    private String dateOfPreBidMeeting;
    private String bidType;
    private String tenderPublicationDate;

}
