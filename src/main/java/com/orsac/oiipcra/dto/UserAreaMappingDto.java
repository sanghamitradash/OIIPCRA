package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAreaMappingDto {

    private int id;

    private int user_id;

    private Boolean isActive;

    private Integer createdBy;

    private Date createdOn;

    private Integer updatedBy;

    private Date updatedOn;

    private Integer state_id;

    private Integer district_id;

    private Integer block_id;

    private Integer gp_id;

    private Integer village_id;

    private Integer division_id;

    private Integer sub_division_id;

    private Integer section_id;
}
