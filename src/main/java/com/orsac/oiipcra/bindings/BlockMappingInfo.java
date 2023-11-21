package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockMappingInfo {

    private Integer id;
    private String deptDistName;
    private Integer distId;
    private String deptMiDivisionName;
    private Integer divisionId;
    private String deptMiSubDivisionName;
    private Integer subDivisionId;
    private String deptMiSectionName;
    private Integer sectionId;
    private String blockName;
    private Integer blockId;
}
