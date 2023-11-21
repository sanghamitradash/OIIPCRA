package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalProgressValueDto {


    private Double totalLengthOfCanalAsPerEstimate;
    private Double lengthOfCanalImproved;
    private Integer noOfCdStructuresRepared;
    private Integer noOfCdStructuresToBeRepared;

    private Double totalLengthOfCad;
    private Double CadConstructed;

    private Integer noOfOutletConstructed;

}
