package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdaptUnitDto {
    private Integer id;
    private String adaptUnitName;
    private Integer unitId;
}
