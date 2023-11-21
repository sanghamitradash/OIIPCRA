package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class workProjectResponse {

    private Integer workId;
    private String bidId;
    private Integer tankId;
    private String tankName;
    private String workName;
    private Boolean active;
    private Integer createdBy;
}
