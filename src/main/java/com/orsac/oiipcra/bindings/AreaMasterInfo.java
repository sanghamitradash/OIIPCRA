package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaMasterInfo {

    private Integer id;
    private String area_type;
    private String master_table;
    private Integer parent_id;
}
