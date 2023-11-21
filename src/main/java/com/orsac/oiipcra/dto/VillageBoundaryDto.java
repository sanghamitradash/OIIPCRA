package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VillageBoundaryDto {

    private Integer dist_id;
    private String district_name;
    private Integer block_id;
    private String block_name;
    private Integer gp_id;
    private String grampanchayat_name;
    private Integer village_id;
    private String revenue_village_name;
    private String geojson;

}
