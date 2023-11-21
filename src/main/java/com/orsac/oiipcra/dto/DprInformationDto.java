package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orsac.oiipcra.entities.CatchmentDetails;
import com.orsac.oiipcra.entities.PaniPanchayatDetailsEntity;
import com.orsac.oiipcra.entities.SoilDetailsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DprInformationDto {

    private Integer id;
    private Integer tankId;
    private Double observerdHighFloodLevel;
    private String metStationName;
    private Double highLandHa;
    private Double mediumLandHa;
    private Double lowLandHa;
    private Integer waterAvailabilityPeriod;
    private Double grossCommandAreaGcaHa;
    private Double existingKhariffPaddyAreaHa;
    private Double lostAyacutHa;
    private Double estdCostOfImpRs;
    private Double estimateForCadaRs;
    private Double costHaImprovementRs;
    private Double costOfCadaRs;
    private Boolean active;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String tankName;
    private String approvalStatus;

    private Integer projectId;
    private String districtName;
    private String divisionName;
    private String subDivisionName;
    private String sectionName;
    private Long designedCcaKharifHa;
    private Long designedCcaRabiHa;
    private Integer ppMembers;
    private String formationYear;
    private String reElectionDone;

    private String blockName;




    List<CatchmentDetails> catchmentDetails;

    List<PaniPanchayatDetailsEntity> paniPanchayatDetails;

    List<SoilDetailsEntity> soilDetails;
}
