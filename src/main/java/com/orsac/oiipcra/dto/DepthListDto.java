package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepthListDto {

    private String capture;
    private String uploadFromDate;
    private String uploadToDate;

    private Integer tankId;

    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
}
