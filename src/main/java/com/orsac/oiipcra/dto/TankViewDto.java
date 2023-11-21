package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankViewDto {

    private int id;

    private int tank_id;

    private int project_id;

    private double ground_water_level_meter;

    private String status_of_tank;

    private boolean surveyed_by_dept;

    private String progress_status;

    private Date created_on;

    private int state_id;

    private int district_id;

    private int block_id;

    private int gp_id;

    private int village_id;

    private int progress_status_id;

    private int division_id;

    private int sub_division_id;

    private int section_id;


}
