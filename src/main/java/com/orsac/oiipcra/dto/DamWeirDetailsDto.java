package com.orsac.oiipcra.dto;


import com.orsac.oiipcra.entities.AreaCapacityCurve;
import com.orsac.oiipcra.entities.CatchmentDetails;
import com.orsac.oiipcra.entities.DamWeirDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DamWeirDetailsDto {

    private Integer id;
    private Integer tankId;
    private String nameOfStream;
    private Integer waterSource;
    private Double dryWeatherFlowRateOfWaterSource;
    private Double maximumHeightOfDamM;
    private Double lengthOfDamM;
    private Double presentTopWidthOf;
    private boolean upstreamPitchingRiprapProvided;
    private String statusOfPitching;
    private boolean downStreamSlopeDrains;
    private String statusOfDownStream;
    private boolean rockToeIfProvided;
    private String statusOfRockToe;
    private boolean vNotchDownStreamOfRockToe;
    private String statusOfVNotch;
    private boolean instrumentationInstalled;
    private String statusOfInstrumentation;
    private Double tblInM;
    private Double frlInM;
    private Double mwlInM;
    private Double dslInM;
    private Double lengthOfDiversion;
    private Double lengthOfOverFlow;
    private Integer typeOfWeirCrest;
    private Integer weirCrestIs;
    private Double crestLevelM;
    private Double maxPondLevel;
    private Double widthOfCrest;
    private Double upStreamAppronLevel;
    private Double downStreamAppronLevel;
    private Double noOfScourVents;
    private Double sizeOfScourVent;
    private Double downStreamAppronLength;
    private Double typeOfConstructionOfBodywall;
    private Integer typeOfConstructionAbutments;
    private String statusOfBodyWall;
    private String statusOfAbutments;
    private String statusOfReturnWalls;
    private String statusOfDown;
    private String statusOfCutOff;
    private String statusOfEndSill;
    private String statusOfSsGates;
    private String statusOfHrGates;
    private Boolean isActive=true;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

    private String nameOfTheMip;
    private String tankWeir;
    private String natureWeir;
    private String status;
    private String waterSourceName;

    private String distName;
    private String divName;
    private Integer projectId;
    private Integer subDivisionId;
    private String subDivName;
    private String blockName;
    private String type;
    private String tankWeirCrestName;
    private String natureOfWeirCrestName;
    private String bodyWallName;
    private String abutmentsName;




    List <AreaCapacityCurve> areaCapacityCurves;


}
