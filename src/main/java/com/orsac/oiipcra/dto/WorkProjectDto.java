package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkProjectDto {

    private Integer id;
    private Integer tenderNoticeId;
    private Integer tankId;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer tenderId;
    private String tankName;
    private Integer projectId;
}
