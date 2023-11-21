package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUpperHierarchyInfo {

    private Integer id;
    private Integer parentId;
    private Integer masterHeadId;
    private Boolean is_terminal;
    private String name;
    private String code;
    private String description;
}
