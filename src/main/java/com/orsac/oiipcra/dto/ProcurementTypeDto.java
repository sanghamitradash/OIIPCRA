package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcurementTypeDto {
    private Integer id;
    private String name;
    private Boolean active;
    private Integer createdBy;
}
