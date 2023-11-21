package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignationDto {

    private Integer id;
    private String name;
    private String departmentName;
    private String userLevelName;
    private String description;
    private  Integer parentId;
    private  Integer subDeptId;
    private  Integer deptId;
    private  boolean active;
    private  Integer createdBy;
    private  String createdOn;
    private  Integer updatedBy;
    private  String updatedOn;
    private  Integer userLevelId;

}
