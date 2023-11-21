package com.orsac.oiipcra.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;
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
public class IssueTrackImagesDto {

    private Integer id;
    private Integer issueId;
    private String imageName;
    private String imageLocation;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    private Double accuracy;
    private Boolean isActive=true;
    private Date saveTime;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

}
