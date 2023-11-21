package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalBidDto {

    private Integer id;
    private String bidId;
    private Date technicalBidOpeningDate;
    private Integer workSlNoInTcn;
    private String workId;
    private Double tenderAmount;
    private String agencyName;
    private String licenseClass;

}
