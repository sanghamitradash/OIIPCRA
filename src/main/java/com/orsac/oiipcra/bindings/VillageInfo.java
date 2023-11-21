package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VillageInfo {

    private Integer distId;
    private String districtName;
    private Integer blockId;
    private String blockName;
    private Integer gpId;
    private String gpName;
    private Integer villageId;
    private String villageName;
    private Integer divisionId;
    private String divisionName;
    private Integer subDivisionId;
    private String subDivisionName;
    private Integer sectionId;
    private String sectionName;
}
