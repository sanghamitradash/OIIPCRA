package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInformationDto {

    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Integer masterHeadId;
    private Boolean isTerminal;
    private String description;

}
