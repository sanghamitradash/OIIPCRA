package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderStipulationInfo {

    private Integer id;
    private Integer tenderId;
    private String bidId;
    private Date technicalBidOpeningDate;
    private Date financialBidOpeningDate;
  //  private Integer workId;
    private Double similarWorkValue;
    private Double similarWorkCompletion;
    private Double annualFinancialTurnover;
    private Double previousYrWeightage;
    private Double creditLinesAmount;
    private Double bidCapacityTurnover;
    private Double completionOfWorkValueTarget;
    private Double turnoverTarget;
    private Double liquidAssetTarget;
}
