package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstimateDto {

    private Integer id;
    private Integer activityId;
    private Integer levelId;
    private Integer statusId;
    private Integer workType;
    private String approvalOrder;
    private String nameOfWork;
    private String technicalSanctionNo;
    private Integer projectId;
    private Integer procurementType;
    private String districtZoneIdentification;
    private Date nolOfTorByWb;
    private String approvalRef;
    private String correspondanceFileNo;
    private Integer periodOfCompletion;
    private Date startDate;
    private Date endDate;
    private Double estimatedAmount;
    private Integer approvedStatus;
    private Integer approvedBy;
    private Date approvalDate;
    private String documentName;
    private String documentPath;
    private String estimateType;

}
