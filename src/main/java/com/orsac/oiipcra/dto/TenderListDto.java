package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Named;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderListDto {

    private Integer userId;
    private Integer tenderId;
    private Integer contractId;
    private Integer tankId;
    private Integer invoiceId;
    private Integer expenditureId;
    private Integer estimateId;
    private Integer activityId;
    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;

}
