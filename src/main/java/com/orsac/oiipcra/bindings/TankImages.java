package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankImages {

    private String sno;
    private double lat;
    private double lon;
    private double alt;
    private double acc;
    private String imageName;
    private String savetime;
}
