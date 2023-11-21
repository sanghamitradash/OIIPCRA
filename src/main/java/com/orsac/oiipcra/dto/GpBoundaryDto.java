package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GpBoundaryDto {

    private Integer block_id;
    private String block_name;
    private String grampanchayat_name;
    private Integer gp_id;
    private String geojson;
}
