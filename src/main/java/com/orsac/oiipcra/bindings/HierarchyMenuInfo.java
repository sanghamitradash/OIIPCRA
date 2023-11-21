package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HierarchyMenuInfo {

    private Integer value;
    private String label;
    private Integer parentId;
    private String module;
    private Boolean isDefault;

}
