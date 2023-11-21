package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrievanceDto {

//    private Integer id;
    private Integer griId;
    private String name;
    private String email;
    private Long mobile;
    private String remarks;
    private String nameOfTheMip;
    private String deptDistName;
    private String deptBlockName;
    private String deptGpName;
    private Integer gender;
    private String genderName;
    private String address;
    private Integer projectId;
    private Integer projectCode;
    private Integer tankId;
    private String tankName;
    private Integer status;
    private String statusName;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String subs;
    private String document;
    private String image;
    private Integer grievanceCount;
    private Integer resolved;
    private Integer rejected;

    private Integer designationId;
    private Integer resolvedUserId;
    private String  designationName;
    private String userName;
    private Integer resolutionLevel;
    private String resolutionLevelName;
    private Integer deptId;
    private String deptName;

}
