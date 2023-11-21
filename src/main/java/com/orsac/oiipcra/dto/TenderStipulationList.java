package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderStipulationList {


    private Integer userId;
    private Integer tenderId;
    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
}
