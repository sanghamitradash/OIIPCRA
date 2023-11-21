package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DraftLetterAwardDto {


    private Integer bidderId;
    private Integer agencyId;
    private String agencyName;
    private String licenseClass;
    private String address;
    private String post;
    private String districtName;
    private Integer pinCode;
    private String workId;
    private String nameOfWork;
    private String district;
    private String projectName;
    private String tenderBidId;
    private Date tenderOpeningDate;
    private Date financialBidOpeningDate;
    private String bidPosition;
    private String division;
    private Long tenderAmount;
    private Long amountQuoted;
    private Double isdToBeDeposited;
    private Long emdAmount;
    private String ee;
    private String otherEe;
    private String userName;

    private String isd;

    private Date userDate;
    private String dateStringForFinBid;
    private String dateStringForTenderOpen;
    private String charTenderAmount;
    private String charAmountQuoted;
    private String charIsdToBeDeposited;
    private String charEmdAmount;
    private Integer memoNumber;
    private Integer memoNumber2;
    private Integer memoNumber3;
    private Integer memoNumber4;

    private Double balance;
    private String designationName;
    private Integer designationId;
    private Integer EeId;

    private Double timeForCompletion;

    private String subDivisionOfficerName;
    private String subDivisionName;
    private String sectionName;
}
