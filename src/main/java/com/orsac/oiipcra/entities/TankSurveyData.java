package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "tank_survey_data")
public class TankSurveyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="tank_id")
    private Integer tankId;

    @Column(name="project_id")
    private Integer projectId;;

    @Column(name="catchment_area_sqkm")
    private double catchmentArea;

    @Column(name="cca_kharif_ha")
    private double ccaKharif;

    @Column(name="cca_rabi_ha")
    private double ccaRabi;

    @Column(name = "water_spread_area_ha")
    private double waterSpreadArea;

    @Column(name="tank_water_level_max_meter")
    private double tankWaterLevelMax;

    @Column(name="tank_water_level_min_meter")
    private double tankWaterLevelMin;

    @Column(name="ground_water_level_meter")
    private double groundWaterLevel;

    @Column(name="turbidity")
    private String turbidity;

    @Column(name="solar_pump_installed")
    private boolean solarPumpInstalled;

    @Column(name="aquatic_vegetation_cover_percent")
    private double aquaticVegetationCover;

    @Column(name="status_of_tank")
    private String statusOfTank;

    @Column(name="no_of_beneficiary")
    private Integer noOfBeneficiary;

    @Column(name="recharge_staff_installation")
    private boolean rechargeShaftInstallation;

    @Column(name="no_of_recharge_staff_installed")
    private Integer noOfRechargeShaftInstalled;

    @Column(name = "embankment")
    private boolean embankment;

    @Column(name="usage")
    private String usage;

    @Column(name="training_conducted")
    private boolean trainingConducted;

    @Column(name="no_of_trainee")
    private Integer noOfTrainee;

    @Column(name="longitude_surveyed")
    private Double longitudeSurveyed;

    @Column(name="latitude_surveyed")
    private Double latitudeSurveyed;

    @Column(name="altitude")
    private Double altitude;

    @Column(name="accuracy")
    private Double accuracy;

    @Column(name="surveyed_by_dept")
    private boolean surveyedByDept;

    @Column(name="progress_status")
    private String progressStatus;

    @Column(name="approved_by")
    private Integer approvedBy;

    @Column(name="approved_on")
    private Date approvedOn;

    @Column(name="created_by")
    private Integer createdBy;

    @Column(name="created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name="updated_by")
    private Integer updatedBy;

    @Column(name="updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name="surveyor_image")
    private String surveyorImage;

    @Column(name="training_image")
    private String trainingImage;

    @Column(name="state_id")
    private Integer stateId;

    @Column(name="district_id")
    private Integer districtId ;

    @Column(name="block_id")
    private Integer blockId ;

    @Column(name="gp_id")
    private Integer gpId ;

    @Column(name="village_id")
    private Integer villageId;

    @Column(name = "geom")
    private Geometry geom;

    @Column(name="division_id")
    private Integer divisionId;

    @Column(name="sub_division_id")
    private Integer subDivisionId;

    @Column(name="section_id")
    private Integer sectionId;

    @Column(name="progress_status_id")
    private Integer progressStatusId;

    @Column(name="is_mobile_tagged")
    private boolean mobileTagged;

    @Column(name="approval_level")
    private Integer approvalLevel;

    @Column(name = "north_depth")
    private Double northDepth;

    @Column(name = "north_latitude")
    private Double northLatitude;

    @Column(name ="north_longitude")
    private Double northLongitude;

    @Column(name ="east_depth")
    private Double eastDepth;

    @Column(name ="east_latitude")
    private Double eastLatitude;

    @Column(name ="east_longitude")
    private Double eastLongitude;

    @Column(name = "west_depth")
    private Double westDepth;

    @Column(name = "west_latitude")
    private Double westLatitude;

    @Column(name ="west_longitude")
    private Double westLongitude;

    @Column(name = "south_depth")
    private Double southDepth;

    @Column(name = "south_latitude")
    private Double southLatitude;

    @Column(name = "south_longitude")
    private Double southLongitude;

    @Column(name ="center_depth")
    private Double centerDepth;

    @Column(name = "center_latitude")
    private Double centerLatitude;

    @Column(name = "center_longitude")
    private Double centerLongitude;


}
