package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftNoticeAnnexureADto {

    private String workIdentificationCode;
    private Integer workId;
    private Integer workSlNOTcn;
    private Double emd;
    private String distName;
    private String blockName;
    private BigDecimal tenderAmount;
    private BigDecimal tenderAmount2;
    private Double bidSecurity;
    private Double similarWorkValue;
    private Double turnover;
    private Double liquidAssetTarget;
//    private Double timeForCompletion;
    private Integer timeForCompletion;
    private String nameOfTheMip;
    private String biddingType;
    private String workType;
    private String nameOfWork;



}
