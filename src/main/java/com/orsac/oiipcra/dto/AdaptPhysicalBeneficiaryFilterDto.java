package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AdaptPhysicalBeneficiaryFilterDto {
    private Integer yearId;
    private Integer deptId;
    private Integer districtId;
    private Integer blockId;
    private Integer gpId;
    private Integer schemeId;
    private Integer componentId;
    private Integer page;
    private Integer size;
    private String sortOrder;
    private String sortBy;
    private String schemeName;
    private String componentName;
}
