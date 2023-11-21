package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankLocations {

    private String pointNo;
    private double lat;
    private double lon;
    private double alt;
    private double acc;
    private String savetime;
}
