package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentMenuInfo {

    private Integer value;
    private String label;
    private Integer parentId;
    private String module;
    private Boolean isDefault;
    private List<HierarchyMenuInfo> children;

}
