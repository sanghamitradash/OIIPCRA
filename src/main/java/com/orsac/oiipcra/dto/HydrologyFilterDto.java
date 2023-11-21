package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor

public class HydrologyFilterDto {

    private Integer tankId;
    private Integer distId;
    private Integer blockId;
    private Integer divisionId;

    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;


}
