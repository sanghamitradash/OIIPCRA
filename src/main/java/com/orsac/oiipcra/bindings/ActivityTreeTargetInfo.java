package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTreeTargetInfo {

    private Integer id;
    private String  name;
    private Integer childId;
    private String type;
    private String mhdName;
    private String finyr_id;
    private Integer physical_target;
    private Double unit_cost_rs;
    private Integer financial_target;

}
