package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockBoundaryDto {

    private Integer dist_id;
    private String district_name;
    private Integer block_id;
    private String block_name;
    private String geojson;

}
