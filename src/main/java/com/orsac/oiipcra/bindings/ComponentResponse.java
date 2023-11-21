package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentResponse {
    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Integer masterHeadId;
    private boolean terminal;
    private List<ComponentResponse> children;
}
