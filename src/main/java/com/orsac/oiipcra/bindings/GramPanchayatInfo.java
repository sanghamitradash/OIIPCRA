package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GramPanchayatInfo {

    private int distId;
    private String  districtName;
    private int blockId;
    private String blockName;
    private int gpId;
    private String gpName;
}
