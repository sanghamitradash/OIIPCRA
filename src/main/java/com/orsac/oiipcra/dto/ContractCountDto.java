package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractCountDto {
    private Integer count;
    private Integer statusId;
    private String statusName;
    private Integer districtId;
    private String districtName;
    private Integer blockId;
    private String blockName;
    private Integer departmentId;
    private String departmentName;
}
