package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TechnicalBidAbstractDto {

    private String bid_id;
    private Date technical_bid_opening_date;
    private Integer work_sl_no_in_tcn;
    private String packageId;
    private Double tenderAmount;
    private String agency;
    private String licenseClass;
    private Date license_validity;
    private String license_on_TenderOpening;
    private String emd_validity;
    private String completion_work_value_qualified;
    private Integer annualTurnover;
    private Integer financialTurnover;
    private String financialTurnoverValidity;
    private String credit_facility_availability;
    private String overall_bid_validity;
    private Date tender_opening_date;
    private Integer tenderId;
    private Long similarWorkValue;
    private Long annualFinancialTurnover;
    private String maxTurnOver;
    private String validity;
    private String date;
    private Double turnOver;


}
