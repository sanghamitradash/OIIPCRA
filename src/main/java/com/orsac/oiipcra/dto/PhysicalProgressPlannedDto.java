package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalProgressPlannedDto {

    private Integer id;
    private String projectId;
    private Integer tankMId;
    private Integer tankId;
    private Integer contractId;
    private Integer estimateId;
    private String tankName;
    private Double totalLengthOfCanalAsPerEstimate;
    private Integer noOfCdStructuresToBeRepared;
    private Double totalLengthOfCad;
    private String progressStatusName;
    private Integer progressStatusId;
    private Integer progressStatus;

}
