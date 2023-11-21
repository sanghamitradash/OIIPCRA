package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityEstimateListingResponseDto {
    private Integer estimateId;
    private  String  workName;
    private  String startDate;
    private  String endDate;
    private String createdOn;
    private  Double estimateAmount;
    private String status;
    private String approvalStatus;
    private Integer approvalId;
    private Integer createdBy;
    private String estimateType;
    private String approvalDate;
    private  String districtName;
    private String blockName;

    private Integer activityId;
    private String activityName;
    private Integer levelId;
    private String levelName;

    private Integer workTypeId;
    private String workType;

    private String code;
}
