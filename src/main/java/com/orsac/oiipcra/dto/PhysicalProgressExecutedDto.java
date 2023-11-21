package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PhysicalProgressExecutedDto {

    private Integer id;
    private String projectId;
    private Integer tankMId;
    private Integer tankId;
    private Double lengthOfCanalImproved;
    private Integer noOfCdStructuresRepared;
    private Double totalLengthOfCad;
    private Integer noOfOutletConstructed;

    private Integer contractId;
    private Integer estimateId;
    private String tankName;
    private String progressStatusName;
    private Integer progressStatusId;
    private Integer progressStatus;
    private Integer plannedId;


}
