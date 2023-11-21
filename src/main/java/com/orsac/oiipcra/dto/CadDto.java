package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.CadLocationEntity;
import com.orsac.oiipcra.entities.FeederLocationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.locationtech.jts.geom.Geometry;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CadDto {

    private Integer id;
    private Integer tankId;
    private Date startDate;
    private Date endDate;
    private Integer surveyedBy;
    private String surveyorImage;
    private Geometry geom;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Boolean isActive=true;
    private String surveyedName;
    private Integer designationId;
    private String designationName;

    private String tankName;
    private Integer projectId;
    private Integer distId;
    private String distName;
    private Integer blockId;
    private String blockName;
    private Integer gpId;
    private String grampanchayatName;
    private Integer villageId;
    private String villageName;
    private Double latitude;
    private Double longitude;
    private String category;
    private Double catchmentAreaSqkm;
    private Double certifiedAyacutKharifHa;
    private Double certifiedAyacutRabiHa;
    private Double lengthOfDamWeirInM;

    private String startDateString;
    private String endDateString;
    private String geomm;

    private List<CadLocationEntity> cadLocation;
}

