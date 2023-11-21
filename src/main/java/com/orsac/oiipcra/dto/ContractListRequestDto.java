package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractListRequestDto {
    private Integer userId;
    private Integer distId;
    private Integer blockId;
    private Integer divisionId;
//    private Integer subDivisionId;
//    private Integer sectionId;
    private Integer agencyId;
    private Integer statusId;
    private String contractNo;
    private String tenderCode;
    private Integer componentId;
    private Integer subComponentId;

    private Integer typeId;
    private Integer subActivityId;
    private Integer contractId;

    private String uploadFromDate;
    private String uploadToDate;
    private Integer page;

    private Integer size;

    private String sortOrder;

    private String sortBy;
    private Integer activityId;
    private Integer estimateId;
    private Integer tenderId;
    private Integer tenderNoticeId;
    private Integer tankId;

    private Integer invoiceId;
    private Integer expenditureId;




}
