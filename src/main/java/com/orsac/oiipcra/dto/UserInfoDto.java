package com.orsac.oiipcra.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private Integer userId;
    private String name;
    private Long mobileNumber;
    private String email;
    private Integer userLevelId;
    private String userLevel;
    private Integer deptId;
    private String department;
    private Integer roleId;
    private String roleName;
    private Integer subDepartmentId;
    private String subDepartmentName;
    private Integer desgId;
    private String designation;
    private Integer agencyId;
    private String agency;
    private Boolean surveyor;
    private Boolean active;


}
