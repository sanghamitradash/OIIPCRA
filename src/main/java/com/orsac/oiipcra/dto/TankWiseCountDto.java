package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankWiseCountDto {

    private Double estimateAmount;
    private Double expenditureAmount;
    private Double contractAmount;
}
