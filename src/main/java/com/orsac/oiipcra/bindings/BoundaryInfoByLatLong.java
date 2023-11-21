package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoundaryInfoByLatLong {

    private int gid;
    private int dist_id;
    private int block_id;
    private int gp_id;
    private int village_id;
}
