package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OiipcraWaterSpreadDto {
    private String month;
    private Integer monthId;
    private String year;
    private Integer finYrId;
    private Integer gid;
    private Double area;
}
