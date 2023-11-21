package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeLevelMappingDto {

    private Integer id;
    private Integer tenderNoticeId;
    private Integer distId;
    private String distName;
    private Integer blockId;
    private String blockName;
    private Integer tankId;
    private String tankName;
    private String projectId;
    private boolean active;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

}
