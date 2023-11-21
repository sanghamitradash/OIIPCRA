package com.orsac.oiipcra.bindings;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StipulationInfo {

    private Integer id;
    private Integer bidId;
    private Date technicalBidOpeningDate;
    private Date financialBidOpeningDate;
    private String nameOfWork;
    // private Integer workId;
    private Double similarWorkValue;
    private Double similarWorkCompletion;
    private Double annualFinancialTurnover;
    private Double previousYrWeightage;
    private Double creditLinesAmount;
    private Double bidCapacityTurnover;
    private Double completionOfWorkValueTarget;
    private Double turnoverTarget;
    private Double liquidAssetTarget;
    private Integer nextId;
    private Integer previousId;

}
