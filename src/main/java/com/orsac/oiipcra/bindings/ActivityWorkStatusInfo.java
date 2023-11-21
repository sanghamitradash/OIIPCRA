package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityWorkStatusInfo {

    private int activityId;
    private int workStatusId;
    private int unitId ;
    private int noOfUnit;
    private int contractId;
    private int deviceId;
    private int userId ;
    private int divisionId ;
    private int subDivisionId ;
    private int sectionId ;
}
