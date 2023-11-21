package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaniPanchayatDetailsDto {

    private Integer id;
    private Integer paniPanchayatId;
    private Integer tankId;
    private Integer ppMembers;
    private String paniPanchayatName;
    private String reElectionDone;
    private Integer formationYear;
    private String finyrName;
    private Boolean active;
    private Integer dprInformationId;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;
    private Date updatedOn;
    private String blockName;
}
