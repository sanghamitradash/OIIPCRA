package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityEstimateTankMappingDto {
    private Integer estimateId;
    private Integer  id;
    private Integer districtId;
    private String districtName;
    private Integer blockId;
    private String blockName;
    private Integer tankId;
    private String tankName;
    private String projectId;
    private Integer divisionId;
    private String divisionName;
    private Double tankWiseAmount;

    private String nameOfWork;
    private Integer tProjectId;


}
