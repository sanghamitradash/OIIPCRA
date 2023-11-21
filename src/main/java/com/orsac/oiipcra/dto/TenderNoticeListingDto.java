package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderNoticeListingDto {
    private Integer tenderId;
    private Integer page;
    private Integer size;
    private String sortOrder;
    private String sortBy;
}
