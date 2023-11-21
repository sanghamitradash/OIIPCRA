package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueTrackerDto {

    private Integer id;
    private Integer userId;
    private Integer contractId;
    private Integer issueTypeId;
    private Integer activityId;
    private String remarks;
    private Integer status;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    private Double accuracy;
    private String resolutionRemarks;
    private Integer resolvedBy;
    private Boolean isActive=true;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer tenderId;
    private Integer tankId;
    private String tankName;
    private Integer estimateId;
    private Date issueDate;
    private Integer deptId;
    private Integer resolutionLevel;
    private Boolean permissionRequired;
    private String description;
    private Integer approvedBy;
    private Integer workId;
    private Integer issueCount;
    private Integer resolved;
    private Integer rejected;
    private Integer designationId;
    private Integer resolvedUserId;


    private List<IssueTrackImagesDto> issueTrackImages;
    private IssueDocumentDto issueDocument;
}
