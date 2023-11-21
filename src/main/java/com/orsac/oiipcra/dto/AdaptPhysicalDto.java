package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.bindings.ActivityUpperHierarchyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdaptPhysicalDto {
    private Integer id;
    private String year;
    private Integer yearId;
    private Integer districtId;
    private String districtName;
    private Integer blockId;
    private String blockName;
    private Integer gpId;
    private String gpName;
    private String directorate;
    private String masterComponent;
    private String schemeName;
    private String componentName;
    private Integer target;
    private Integer achievement;
    private Integer noofBeneficiaries;
    private Integer achievementPercentage;
    private Integer deptId;
    private Integer unitId;
    private String unitName;
    private Integer activityId;
    private String activityName;
    private String code;
    private Integer schemeId ;
    private Integer componentId ;


   private List<ActivityUpperHierarchyInfo> upperHierarchy;
}
