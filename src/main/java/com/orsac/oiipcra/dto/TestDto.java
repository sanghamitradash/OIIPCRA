package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Named;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {
    private String landformL;
    private String lulc;
    private String lulc1;
    private String tmu;
    private String soilPhase;
    private String soilDepth;
    private String soilDrain;
    private String newSlope;
    private String surfaceTe;
    private String textureCo;
    private Double areaHa;
    private String soilConservationMeasures;

}
