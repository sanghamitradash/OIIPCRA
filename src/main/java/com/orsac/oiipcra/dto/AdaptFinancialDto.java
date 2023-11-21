package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orsac.oiipcra.bindings.ActivityUpperHierarchyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdaptFinancialDto {
    private Integer id;
    private String year;
    private Integer yearId;
    private String districtName;
    private Integer districtId;

    private String directorate;
    private Integer deptId;

    private Double financialAllocationInApp;

    private String schemeName;

    private Double actualFundAllocated;

    private Integer estimateId;
    private Integer activityId;
    private String activityName;

    private Double expenditure;
    private Double percentageAllocated;
    private Integer count;


    private String code;
    private List<ActivityUpperHierarchyInfo> upperHierarchy;


}
