package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrievanceListingDto {

    private Integer userId;
    private Integer distId;
    private Integer blockId;
    private Integer tankId;
    private Integer statusId;
    private Integer invoiceId;
    private Integer expenditureId;
    private String statusName;
    private Integer estimateId;
    private Integer contractId;
    private Integer gpId;
    private Integer progressStatus;


    private String uploadFromDate;
    private String uploadToDate;

    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;

    private Integer issueId;
}
