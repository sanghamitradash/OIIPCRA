package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterDto {
    private Integer serialNo;
    private String structures;
    private String landUse;
    private String landForms;
    private String slopePercentage;
    private String surfaceStructure;
    private String soilDepth;

}
