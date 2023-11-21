package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.CroppingPattern;
import com.orsac.oiipcra.entities.DemographicDetails;
import com.orsac.oiipcra.entities.HistoricalDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankOtherDetailsDto {

    private Integer id;
    private Integer tankId;
    private Double presentAyacutKhariff;
    private Double presentAyacutRabi;
    private Integer riverBasin;
    private String agroClimaticZone;
    private Integer nearestVillage;
    private Integer populationOfNearestVillage;
    private Integer projectConstructedYear;

    private String deptDistName;
    private String miDivisionName;
    private String deptBlockName;
    private String deptGpName;
    private String nameOfTheMip;
    private Integer projectId;

    private String category;
    private Long latitude;
    private Long longitude;
    private String nearestVillageName;
    private Long designedCcaKharifHa;
    private Long designedCcaRabiHa;
    private String type;
    private String riverbasinName;
    private String topoSheetNo;
    private Long catchmentAreaSqkm;

    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer approvedStatus;
    private String finYrName;

    private List<CroppingPattern> croppingPatternList;
    private HistoricalDetails historicalDetails;
    private DemographicDetails demographicDetails;
}
