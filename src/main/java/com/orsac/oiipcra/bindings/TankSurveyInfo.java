package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.locationtech.jts.geom.Geometry;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TankSurveyInfo {

    private int id;

    private int userId;

    private int tankId;

    private int projectId;

    private double catchmentArea;

    private double ccaKharif;

    private double ccaRabi;

    private double waterSpreadArea;

    private double tankWaterLevelMax;

    private double tankWaterLevelMin;

    private double groundWaterLevel;

    private String turbidity;

    private boolean solarPumpInstalled;

    private double aquaticVegetationCover;

    private String statusOfTank;

    private int noOfBeneficiary;

    private boolean rechargeShaftInstallation;

    private int noOfRechargeShaftInstalled;

    private boolean embankment;

    private String usage;

    private boolean trainingConducted;

    private int noOfTrainee;

    private double longitudeSurveyed;

    private double latitudeSurveyed;

    private Double altitude;

    private Double accuracy;

    private boolean surveyedByDept;

    private String progressStatus;

    private int approvedBy;

    private String approvedOn;

    private String surveyedBy;

    private String surveyorImage;

    private String trainingImage;

    private int createdBy;

    private Date createdOn;

    private int updatedBy;

    private Date updatedOn;

    private boolean active;

    private List<TankImages> tankImages;

    private List<RechargeShaftImages> shaftImages;

    private List<TankLocations> tankLocation;

    private int stateId;

    private int districtId ;

    private int blockId ;

    private int gpId ;

    private int villageId;

    private Geometry geom;

    private String capturedDate;

    private String districtName;

    private String blockName;

    private String gpName;

    private String villageName;

    private int roleId;

    private int deptId;

    private int divisionId;

    private int subDivisionId;

    private int sectionId;

    private String divisionName;

    private String subDivisionName;

    private String sectionName;

    private int progressStatusId;

    private boolean mobileTagged;

    private String tankAreaType;

    private boolean isMobileTagged;

    private String tankName;

    private String geojson;

    private String approverName;

    private String approvalStatus;

    private int approvalLevel;

    private Double northDepth;

    private Double northLatitude;

    private Double northLongitude;

    private Double eastDepth;

    private Double eastLatitude;

    private Double eastLongitude;

    private Double westDepth;

    private Double westLatitude;

    private Double westLongitude;

    private Double southDepth;

    private Double southLatitude;

    private Double southLongitude;

    private Double centerDepth;

    private Double centerLatitude;

    private Double centerLongitude;




}
