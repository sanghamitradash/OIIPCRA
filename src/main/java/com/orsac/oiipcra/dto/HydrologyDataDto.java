package com.orsac.oiipcra.dto;
import com.orsac.oiipcra.entities.CcaRestoreEntity;
import com.orsac.oiipcra.entities.CropDetailsEntity;
import com.orsac.oiipcra.entities.PageIndexEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class HydrologyDataDto {

    private String districtName;
    private String blockName;
    private String divisionName;
    private String nameOfTheMip;
    private Integer projectId;

    private Integer id;
    private Integer tankId;
    private String nameOfStream;
    private Double maxFlood;
    private Double averageYearlyRainfall;
    private Double dependableRainfall;
    private Double averageMonsoonRainfall;
    private Double depMansoonRainfall;

    private Double effectiveRainFall;
    private Double monsoonRainfallDistbn;
    private Double shiftingOfMonsoon;

    private Double liveStorage;
    private Double irrigationRequirement;

    private Double waterSpreadArea;

    private String maxSpillingMonthName;

    private Double max5DaySpillingQnty;

    private Double minTempDegreeC;

    private Double maxTempDegreeC;


    private Double relhumidityRainy;

    private Double windSpeedKmDay;

    private Double relhumidityWinter;

    private Double sunshineHoursDay;

    private Double radiationKjDay;

    private Double averageEtoMm;
    private Double irrWaterReqKhariffHaMm;

    private Double irrWaterReqRabiHaM;

    private Double dependableYieldMcm;

    private Double irrigationWaerSupplyMm;

    private String classOfIrrigation;

    private Double extension_of_rabi_ayacut_ha;
    private String gwtDataAvlFromYear;

    private String gwtDatAvlUptoYear;

    private Double gwLevelInYearPastM;

    private Double gwLevelInYearPresentM;

    private String gwLevelTrendName;

    private Double annualGwPotentialMcm;

    private Double annualGwRechargeByProjectComp;

    private Double gwProposedToBeExtractedForIrrn;

    private Double utilisationOfGwRecharge;

    private Double volOfGwSupplementationRequired;

    private Double maxVolOf5DayGwDrawalRequired;

    private Double rateOfMaxDrawalOfGwDay;
    private Double noOfPumpsRequiredHpRating;
    private Double drawlCapacityOfPumps;

    private Boolean active;

    private Integer createdBy;

    private String createdOn;

    private Integer updatedBy;

    private Date updatedOn;

    private Integer divisionId;

    private Integer blockId;

    private Integer districtId;

    private Integer maxSpillingMonth;

    private Integer gwLevelTrend;

    private List<CropDetailsEntity> cropDetailsEntities;
    private List<CcaRestoreEntity> ccaRestoreEntities;
    private PageIndexEntity pageIndexEntities;
}
