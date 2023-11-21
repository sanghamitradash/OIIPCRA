package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageWiseBiddersDto {

    private String bidId;
    private String biddingType;
    private String tenderOpeningdateChar;
    private String nameOfWork;
    private String workId;
    private String projectName;
    private Integer workSlNoInTcn;
    private Double tenderAmount;
    private Double paperCost;
    private Double emdToDeposit;
    private Integer timeOfCompletion;
    private String licenseClass;
    private String licenseExpiring;
    private String gstIn;
    private BigDecimal contactNo;
    private String distName;
    private String divName;
    private String blockName;
    private String bidderCategory;
    private Double paperCostSubmit;
    private String licenseName;
    private String tenderAmountChar;
    private Integer bidderId;
    private Date technicalBidOpeningDate;
    private String packageId;
    private Double similarWorkValue;
    private Double creditFacilityRequied;
}
