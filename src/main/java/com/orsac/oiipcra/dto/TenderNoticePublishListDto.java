package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderNoticePublishListDto {
    private Integer distId;
    private String toDate;
    private String fromDate;
    private Integer page;
    private Integer size;
    private String sortOrder;
    private String sortBy;
    private Integer type;
    private  Integer length;
    private Integer start;
    private Integer draw;


}
