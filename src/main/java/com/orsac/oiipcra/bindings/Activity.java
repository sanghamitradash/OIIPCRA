package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Activity {

    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Integer masterHeadId;
    private Boolean isTerminal;
    private Boolean isActive;
    private String type;
    private String description;
    private String deptName;
    private Integer typeId;
}
