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
@Table(name = "depth_m")
public class DepthEntity {

    @Id
    @SequenceGenerator(name = "depth_m_sequence", sequenceName = "depth_m_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "depth_m_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name ="tank_id")
    private Integer tankId;

    @Column(name ="capture")
    private Date capture;

    @Column(name ="latitude")
    private Double latitude;

    @Column(name ="longitude")
    private Double longitude;

    @Column(name ="altitude")
    private Double altitude;

    @Column(name ="accuracy")
    private Double accuracy;

    @Column(name ="surveyed_by")
    private Integer surveyedBy;

    @Column(name = "surveyor_image")
    private String surveyorImage;


    @Column(name="created_by")
    private Integer createdBy;

    @Column(name="created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name="updated_by")
    private Integer updatedBy;

    @Column(name="updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "image")
    private String image;

    @Column(name = "depth_in_m")
    private Double depthInM;









}
