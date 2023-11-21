package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkProgressViewDto {

    private Integer tankId;
    private Integer bidId;
    private Integer workId;
}
