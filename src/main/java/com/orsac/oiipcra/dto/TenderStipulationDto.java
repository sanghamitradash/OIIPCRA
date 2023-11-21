package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderStipulationDto {

    private Integer id;
    private Integer bidId;
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
    private Integer createdBy;
    private Integer nextId;
    private Integer flagId;




}
