package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankDto {

    private int id;
    private int state_id;

    private int district_id;

    private int block_id;

    private int gp_id;

    private int village_id;

    private int division_id;

    private int sub_division_id;

    private int section_id;

    private int page;

    private int size;

    String sortOrder;

    String sortBy;



}
