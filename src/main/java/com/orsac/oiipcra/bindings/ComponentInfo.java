package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentInfo {

    private String name;
    private String code;
    private Integer levelId;
    private Integer approvalId;
    private Integer statusId;

}
