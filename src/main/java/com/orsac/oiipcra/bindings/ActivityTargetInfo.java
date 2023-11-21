package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTargetInfo {
    private Integer terminalId;
    private String name;
    private String unitName;
    private String financialYear;
    private Integer unitId;
    private Integer physicalTarget;
    private Double unitCostRs;
    private Double financialTarget;
    private Double contractAmount;

}
