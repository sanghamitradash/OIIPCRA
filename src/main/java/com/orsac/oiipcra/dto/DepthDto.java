package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepthDto {

    private Integer id;
    private Integer tankId;
    private Date capture;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double accuracy;
    private Integer surveyedBy;
    private String surveyorImage;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private boolean isActive;
    private Double depthInM;
    private String image;
    private String imageUrl;
    private String surveyorImageUrl;
    private String month;

    private String imageName;
    private String year;

}
