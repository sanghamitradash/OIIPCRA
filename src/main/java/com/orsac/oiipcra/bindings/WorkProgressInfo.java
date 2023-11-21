package com.orsac.oiipcra.bindings;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkProgressInfo {
    private Integer workProgressId;
    private Integer distId;
    private String distName ;
    private Integer blockId;
    private String blockName;
    private Integer tenderId;
    private String bidId;
    private Integer tankId;
    private String tankName;
    private Integer projectId;
    private Integer workId;
    private Integer gpId;
    private String gpName;
    private Integer villageId;
    private String villageName;
    private Integer miDivisionId;
    private String miDivisionName;

}
