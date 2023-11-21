package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hydrology_data")
public class HydrologyDataEntity {
    @Id
    @SequenceGenerator(name = "hydrology_data_sequence", sequenceName = "hydrology_data_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hydrology_data_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "name_of_stream")
    private String nameOfStream;

    @Column(name = "max_flood")
    private Double maxFlood;

    @Column(name = "average_yearly_rainfall")
    private Double averageYearlyRainfall;

    @Column(name = "dependable_rainfall")
    private Double dependableRainfall;

    @Column(name = "average_monsoon_rainfall")
    private Double averageMonsoonRainfall;

    @Column(name = "dep_mansoon_rainfall")
    private Double depMansoonRainfall;

    @Column(name = "effective_rain_fall")

    private Double effectiveRainFall;

    @Column(name = "monsoon_rainfall_distbn")

    private Double monsoonRainfallDistbn;

    @Column(name = "shifting_of_monsoon")

    private Double shiftingOfMonsoon;

    @Column(name = "live_storage")

    private Double liveStorage;

    @Column(name = "irrigation_requirement")

    private Double irrigationRequirement;

    @Column(name = "water_spread_area")
    private Double waterSpreadArea;

    @Column(name = "max_spilling_month")
    private Integer maxSpillingMonth;

    @Column(name = "max_5_day_spilling_qnty")
    private Double max5DaySpillingQnty;

    @Column(name = "min_temp_degree_c")
    private Double minTempDegreeC;
    @Column(name = "max_temp_degree_c")
    private Double maxTempDegreeC;

    @Column(name = "relhumidity_rainy")
    private Double relhumidityRainy;
    @Column(name = "wind_speed_km_day")
    private Double windSpeedKmDay;
    @Column(name = "relhumidity_winter")
    private Double relhumidityWinter;
    @Column(name = "sunshine_hours_day")
    private Double sunshineHoursDay;
    @Column(name = "radiation_kj_day")
    private Double radiationKjDay;
    @Column(name = "average_eto_mm")
    private Double averageEtoMm;





    @Column(name = "irr_water_req_khariff_ha_mm")
    private Double irrWaterReqKhariffHaMm;
    @Column(name = "irr_water_req_rabi_ha_m")
    private Double irrWaterReqRabiHaM;
    @Column(name = "dependable_yield_mcm")
    private Double dependableYieldMcm;
    @Column(name = "irrigation_waer_supply_mm")
    private Double irrigationWaerSupplyMm;
    @Column(name = "class_of_irrigation")
    private String classOfIrrigation;
    @Column(name = "extensionOfRabiAyacutHa")
    private Double extension_of_rabi_ayacut_ha;


    @Column(name = "gwt_data_avl_from_year")
    private String gwtDataAvlFromYear;
    @Column(name = "gwt_dat_avl_upto_year")
    private String gwtDatAvlUptoYear;
    @Column(name = "gw_level_in_year_past_m")
    private Double gwLevelInYearPastM;
    @Column(name = "gw_level_in_year_present_m")
    private Double gwLevelInYearPresentM;
    @Column(name = "gw_level_trend")
    private Integer gwLevelTrend;
    @Column(name = "annual_gw_potential_mcm")
    private Double annualGwPotentialMcm;


    @Column(name = "annual_gw_recharge_by_project_comp")
    private Double annualGwRechargeByProjectComp;
    @Column(name = "gw_proposed_to_be_extracted_for_irrn")
    private Double gwProposedToBeExtractedForIrrn;
    @Column(name = "utilisation_of_gw_recharge")
    private Double utilisationOfGwRecharge;
    @Column(name = "vol_of_gw_supplementation_required")
    private Double volOfGwSupplementationRequired;
    @Column(name = "max_vol_of_5_day_gw_drawal_required")
    private Double maxVolOf5DayGwDrawalRequired;
    @Column(name = "rate_of_max_drawal_of_gw_day")
    private Double rateOfMaxDrawalOfGwDay;

    @Column(name = "no_of_pumps_required_hp_rating")
    private Double noOfPumpsRequiredHpRating;
    @Column(name = "drawl_capacity_of_pumps")
    private Double drawlCapacityOfPumps;


    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "created_by")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;


    @Column(name = "division_id")
    private Integer divisionId;
    @Column(name = "block_id")
    private Integer blockId;
    @Column(name = "district_id")
    private Integer districtId;











}