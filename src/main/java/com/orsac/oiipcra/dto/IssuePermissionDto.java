package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssuePermissionDto {

    private Integer id;
    private Date issueDate;
    private String description;
    private Boolean permissionRequired;
    private Integer deptId;
    private String deptName;
    private Integer createdBy;
    private Integer resolutionLevel;
    private String levelName;
    private Integer statusId;
    private String status;
    private Integer projectId;
    private String tankName;
    private Integer tankId;
    private String contractNumber;
    private Integer contractId;
    private Integer tenderId;
    private String bidId;
    private Integer workId;
    private String identificationCode;
    private String code;
    private Integer estimateId;
    private String workName;
    private String issueType;
    private Integer subActivityId;
    private String remarks;
    private String distName;
    private String blockName;
    private Integer distId;
    private Integer blockId;
    private String resolutionRemarks;
    private Integer designationId;
    private Integer resolvedUserId;
    private String  designationName;
    private String userName;



}
