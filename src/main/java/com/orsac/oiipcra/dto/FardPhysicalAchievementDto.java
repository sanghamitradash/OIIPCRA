package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FardPhysicalAchievementDto {

    private Integer id;
    private String districtName;
    private String blockName;
    private String gpName;
    private String directorate;
    private String schemeName;
    private Integer target;
    private Integer achievement;
    private Integer noOfBeneficiaries;
    private Double achievementPercentage;
    private Integer year;
    private Integer distId;
    private Integer blockId;
    private Integer gpId;
    private Integer yearId;
    private Integer deptId;
    private Integer activityId;
    private Date createdOn;
    private Boolean isActive;
    private Integer schemeId;
    private String activityName;

}
