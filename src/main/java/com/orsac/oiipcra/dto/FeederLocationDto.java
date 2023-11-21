package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeederLocationDto {

    private Integer id;
    private Integer feederId;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double accuracy;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private boolean isActive;
}
