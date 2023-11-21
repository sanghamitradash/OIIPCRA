package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityImageInfo {
    private  Integer imageId;
    private  Integer activityId;
    private String imageName;
    private String   imageLocation;
    private Boolean active;
    private String  saveTime;
}
