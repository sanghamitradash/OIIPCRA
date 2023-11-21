package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.ActivityEstimateTankMappingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityEstimateAddDto {
    private Integer id;

    private  Boolean activeFlag;
    private Integer createdBy;
    private Date createdOn = new Date(System.currentTimeMillis());
    private Integer updatedBy;
    private Date updatedOn;

    private Integer activityId;
    private  Integer levelId ;
    private Integer statusId ;
    private Integer distId ;
    private Integer blockId ;
    private Integer workType;
    private String approvalOrder ;
    private String nameOfWork;
    private String technicalSanctionNo ;
    private Integer projectId ;
    private Integer procurementType ;
    private String districtZoneIdentification;
    private Date nolOfTorByWb;
    private String approvalRef ;
    private String correspondanceFileNo ;
    private Integer periodOfCompletion ;
    private Date startDate;
    private Date endDate ;
    private Double estimatedAmount;
    private Integer approvedStatus;
    private Integer approvedBy;
    private Date approvalDate;
    private  String documentName;
    private  String documentPath;

    private Integer estimateType;
    private Integer reviewType;
    private Integer marketApproach;
    private String loanCreditNo;
    private Integer procurementDocumentType;
    private String highSeaShRisk;
    private Integer procurementProcess;
    private Integer evaluationOptions;
    List<ActivityEstimateTankMappingEntity> activityEstimateMappingList;
}
