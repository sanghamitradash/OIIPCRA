package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankWorkStatusInfo {
    private int id ;
    private int tankId ;
    private int workStatusId ;
    private int unitId ;
    private int noOfUnit ;
    private int contractId ;
    private double longitude ;
    private double latitude;
    private double altitude ;
    private double accuracy;
    private int deviceId ;
    private int userId ;
    private int divisionId ;
    private int subDivisionId ;
    private int sectionId ;
    private int tankBoundary ;
    private int approvalStatus ;
    private int activityId ;
    private boolean isActive;
    private int createdBy ;
    private Date createdOn ;
    private int updatedBy ;
    private Date updatedOn ;

}
