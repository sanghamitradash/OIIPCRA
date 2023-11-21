package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySurveyImageRequest {
    private Integer id;
    private Integer surveyId;
    private String imageName;
    private String imageLocation;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    private Double accuracy;
    private Integer createdBy;
    private String createdOn;
    private Integer updatedBy;
    private String updatedOn;
    private Boolean isActive;
    private String saveTime;

}
