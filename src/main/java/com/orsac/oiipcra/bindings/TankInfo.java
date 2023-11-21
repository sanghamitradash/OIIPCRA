package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankInfo {
    private Integer id;
    private Integer projectId;
    private Integer tankId;
    private String tankName;
    private String nameOfTheMip;
    private Integer distId;
    private String deptDistName;
    private Integer blockId;
    private String deptBlockName;
    private Integer gpId;
    private String deptGpName;
    private Integer villageId;
    private String villageName;
    private double latitude;
    private double longitude;
    private String  category;
    private Integer categoryId;
    private String type;
    private Integer typeId;
    private double catchmentAreaSqkm;
    private double designedCcaKharifHa;
    private double designedCcaRabiHa;
    private double certifiedAyacutKharifHa;
    private double certifiedAyacutRabiHa;
    private String riverBasin;
    private Integer  miDivisionId;
    private String miDivisionName;
    private Double waterSurfaceAreaHa ;
    private Double heightOfDamWeirInM;
    private Double lengthOfDamWeirInM ;
    private String typeOfDamWeir ;
    private String remarks ;
    private String revisedStartDate ;
    private String  revisedEndDate ;
    private Integer noOfBeneficiaries ;
    private Integer mipId;
    private String geojson;
    private Integer subDivisionId;
    private String subDivisionName;
    private Integer sectionId;
    private String sectionName;
    private Integer tankCount;
    private Integer circleId;
    private String circleName;
    private String sourceOfFunding;
    private String nwbisId;

    private boolean proposedToBeDropped;
    private boolean isDropped;
    private boolean civilWorkCompleted;
    private boolean fpoAdded;






 }
