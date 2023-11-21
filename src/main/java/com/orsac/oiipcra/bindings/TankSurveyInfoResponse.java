package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankSurveyInfoResponse {

    private Integer id;

    private Integer tankId;

    private Integer projectId;

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

    private Integer noOfBeneficiary;

    private boolean rechargeShaftInstallation;

    private Integer noOfRechargeShaftInstalled;

    private boolean embankment;

    private String usage;

    private boolean trainingConducted;

    private Integer noOfTrainee;

    private double longitudeSurveyed;

    private double latitudeSurveyed;

    private Double altitude;

    private Double accuracy;

    private boolean surveyedByDept;

    private String progressStatus;

    private Integer approvedBy;

    private String approvedOn;

    private String surveyedBy;

    private String surveyorImage;

    private String trainingImage;

    private Integer createdBy;

    private Date createdOn;

    private Integer updatedBy;

    private Date updatedOn;

    private boolean active;

    private List<TankImages> tankImages;

    private List<RechargeShaftImages> shaftImages;

    private List<TankLocations> tankLocation;

    private Integer stateId;

    private Integer districtId ;

    private Integer blockId ;

    private Integer gpId ;

    private Integer villageId;

    private Geometry geom;

    private String capturedDate;

    private String districtName;

    private String blockName;

    private String gpName;

    private String villageName;

    private Integer roleId;

    private Integer deptId;

    private Integer divisionId;

    private Integer subDivisionId;

    private Integer sectionId;

    private String divisionName;

    private String subDivisionName;

    private String sectionName;

    private Integer progressStatusId;

    private boolean mobileTagged;

    private String tankAreaType;

    private boolean isMobileTagged;

    private String tankName;

    private String geojson;

    private String approverName;

    private String approvalStatus;

    private Integer surveyBy;
    private String surveyorName;
}
