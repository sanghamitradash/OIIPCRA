package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkProgressDto {

    private Integer userId;
    private Integer componentId;
    private Integer subComponentId;
    private Integer activityId;
    private Integer subActivityId;
    private Integer tenderId;
    private Integer tankId;
    private Integer contractId;
    private Integer estimateId;
    private Integer invoiceId;
    private Integer expenditureId;

    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
}
