package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdaptFinancialSchemeDto {
    private  Integer id;
    private String schemeName;
    private Integer activityId;

}
