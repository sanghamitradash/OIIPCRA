package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrievanceFileDto {

    private Integer id;
    private String name;
    private String email;
    private Long mobile;
    private Integer gender;
    private String address;
    private Integer projectId;
    private Integer tankId;
    private String remarks;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updtedOn;
    private Integer status;
    private String document;
    private String image;
    private Integer resolutionLevel;
    private Integer resolvedUserId;
    private Integer designationId;
    private Integer deptId;
}
