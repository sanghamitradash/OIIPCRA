package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrTerminalInfo {

    private String id;
    private String name;
    private Integer levelId;
    private Integer approvalId;
}
