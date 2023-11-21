package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureListDto {

    private Integer expenditureType;
    private Integer userId;
    private Integer contractId;
    private Integer agencyId;
    private Integer tenderId;
    private Integer tenderNoticeId;
    private Integer componentId;
    private Integer subComponentId;
    private Integer activityId;
    private Integer subActivityId;
    private Integer estimateId;
    private Integer tankId;
    private Integer distId;
    private Integer divisionId;

    private Integer invoiceId;
    private Integer statusId;
    private Integer levelId;

    private Integer issueId;
    private Integer workType;

    private String uploadFromDate;
    private String uploadToDate;

    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;

}
