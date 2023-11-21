package com.orsac.oiipcra.dto;

import lombok.Data;

@Data
public class HierarchyMenuDto {

    private Integer value;
    private String label;
    private Integer parentId;
    private String module;
}
