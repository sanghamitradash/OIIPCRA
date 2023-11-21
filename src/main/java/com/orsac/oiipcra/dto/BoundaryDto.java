package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoundaryDto {

    private Integer dist_id;

    private String district_name;

    private Integer state_id;

    private String state_name;

    private Integer block_id;

    private String block_name;

    private Integer gp_id;

    private String grampanchayat_name;

    private Integer village_id;

    private String revenue_village_name;

    private Integer section_id;

    private String mi_section_name;

    private Integer mi_sub_division_id;

    private String mi_sub_division_name;

}
