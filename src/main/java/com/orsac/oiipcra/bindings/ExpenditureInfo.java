package com.orsac.oiipcra.bindings;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureInfo {

    private Integer id;
    private String bidId;
    private String  workId;
    private String invoiceNo;
    private Date invoiceDate;
    private String contractNumber;
    private String agencyName;
    private BigDecimal value;
    private Date paymentDate;
    private String paymentType;
    private String activityName;

    private String code;
    private Integer activityId;
    private Integer contractId;
    private Integer tenderId;
    private Integer tenderNoticeId;
    private BigDecimal contractAmount;
    private String contractAmountChar;
    private String valueChar;
    private String description;

    private Integer tankId;
    private String tankName;


}
