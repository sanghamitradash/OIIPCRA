package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.FeederLocationEntity;
import com.orsac.oiipcra.entities.InvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeederDto {

    private Integer id;
    private Integer tankId;
    private String tankName;
    private Date startDate;
    private Date endDate;
    private Integer surveyedBy;
    private Date uploadOn;
    private String surveyorImage;
    private String geom;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private boolean isActive;

    private String surveyedName;
    private String designationName;

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

    private List<FeederLocationEntity> feederLocation;

}
