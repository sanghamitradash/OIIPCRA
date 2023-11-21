package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEstimateRequestDto {

    private  Integer userId;
    private  Integer activityId;
    private Integer statusId;
    private Integer approvalStatus;
    private String fromDate;
    private String toDate;
    private Integer typeId;
    private Integer workType;
    private Integer procurementType;

    private Integer tankId;
    private Integer distId;
    private Integer expenditureId;
    private Integer invoiceId;

    private Integer contractId;

    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
}
