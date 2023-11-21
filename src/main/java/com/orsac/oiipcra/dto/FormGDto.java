package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormGDto {

    private Integer finYrId;
    private String finyr;
    private String agencyName;
    private String licenseClass;
    private String gstinNo;
    private String bidId;
    private String bid_workId;
    private Integer id;
    private String issueNo;
    private String district;
    private String scheme;
    private Date tenderOpeningDate;
    private BigDecimal tenderAmount;
    private String validityOfTender;
    private String awarded;
    private Integer bidderId;
    private Integer agencyId;
    private String tenderAmountString;
    private String sumString;
    private String record;
    private String dateString;
}
