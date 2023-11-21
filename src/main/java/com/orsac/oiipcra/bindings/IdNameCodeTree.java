package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdNameCodeTree {
    private Integer id;
    private String name;
    private String code;
}
