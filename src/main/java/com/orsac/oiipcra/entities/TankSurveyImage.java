package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tank_survey_images")
public class TankSurveyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "survey_id")
    private int surveyId;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_location")
    private String imageLocation;

    @Column(name = "longitude")
    private double lon;

    @Column(name = "latitude")
    private double lat;

    @Column(name = "altitude")
    private double alt;

    @Column(name = "accuracy")
    private double acc;

    @Column(name = "created_by")
    private int createdBy;

    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private int updatedBy;

    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name="savetime")
    private Date savetime;
}
