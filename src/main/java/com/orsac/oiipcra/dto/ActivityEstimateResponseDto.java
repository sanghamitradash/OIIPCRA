package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEstimateResponseDto {
    private Integer estimateId;
    private  String  workName;
    private  String startDate;
    private  String endDate;
    private  Double estimateAmount;

    private Integer activityId;
    private String activityName;
    private  Integer levelId ;
    private String levelName;
    private Integer statusId ;
    private String status;
    private Integer distId ;
    private String district;
    private Integer blockId ;
    private String block;
    private Integer workType;
    private  String workTypeName;
    private String approvalOrder ;
    private String technicalSanctionNo ;
    private Integer projectId ;
    private Integer procurementType ;
    private String  procurement;
    private String districtZoneIdentification;
    private String nolOfTorByWb;
    private String approvalRef ;
    private String correspondanceFileNo ;
    private Integer periodOfCompletion ;
    private Integer approvedStatus;
    private String approval;
    private Integer approvedBy;
    private String approvedUserName;
    private Date approvalDate;
    private Integer createdBy;
    private String docName;
    private String docPath;
    private String finalDocPath;
    private Integer estimateTypeId;
    private String estimateType;
    private String districtName;
    private String blockName;
    private  String tankName;

    private Integer reviewType;
    private String reviewTypeName;

    private Integer marketApproach;
    private String marketApproachName;

    private String loanCreditNo;
    private Integer procurementDocumentType;
    private String documentType;
    private String highSeaShRisk;
    private Integer procurementProcess;
    private String procurementTypeName;
    private Integer evaluationOptions;
    private String evaluationOptionName;

    private Integer divisionId ;
    private String divisionName;

}
