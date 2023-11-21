package com.orsac.oiipcra.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    private Integer id;

    private String name;

    private String email;

    private Long mobileNumber;

    private Integer designationId;

    private Integer roleId;

    private Integer departmentId;

    private String password;

    private Integer areaId;

    private Integer userLevelId;

    private Integer masterAreaId;

    private Boolean isActive;

    private Boolean isSurveyor;

    private Integer createdBy;

    private Date createdOn;

    private Integer updatedBy;

    private Date updatedOn;
    private String departmentName;
    private String designationName;

}
