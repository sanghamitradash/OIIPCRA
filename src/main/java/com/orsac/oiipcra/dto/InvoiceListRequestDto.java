package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceListRequestDto {

    private Integer userId;
    private Integer workId;
    private Integer workTypeId;
    private Integer contractId;
    private Integer agencyId;
    private Integer bidId;
    private Integer statusId;
    private String uploadFromDate;
    private String uploadToDate;
    private Integer expenditureId;

    private Integer tankId;
    private Integer distId;
    private Integer blockId;
    private Integer divisionId;
    private Integer subdivisionId;
    private Integer sectionId;

    private Integer componentId;
    private Integer subComponentId;
    private Integer activityId;
    private Integer subActivityId;
    private Integer estimateId;
    private Integer tenderId;


    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
}
