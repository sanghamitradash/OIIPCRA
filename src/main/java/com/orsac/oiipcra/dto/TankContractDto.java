package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankContractDto {
    private Integer tankId;
    private Integer blockId;
    private String tankName;
    private String blockName;
    private Integer distId;
    private String districtName;
    private Integer projectId;
}
