package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalProgressUpdateDto {
    private Integer id;
    private Integer tankId;
    private Integer contractId;
    private Integer userId;
    private Integer progressStatus;
    private Integer projectId;
    private Integer tankMId;

    private Double totalLengthOfCanalAsPerEstimate;
    private Integer noOfCdStructuresToBeRepared;
    private Double totalLengthOfCad;

    private Double lengthOfCanalImproved;
    private Integer noOfCdStructuresRepared;
    private Double totalLengthOfCadExecuted;
    private Integer noOfOutletConstructed;
}
