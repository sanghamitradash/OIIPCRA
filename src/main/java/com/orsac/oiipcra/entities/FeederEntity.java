package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feeder_m")
public class FeederEntity {

    @Id
    @SequenceGenerator(name = "feeder_m_sequence", sequenceName = "feeder_m_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feeder_m_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name ="tank_id")
    private Integer tankId;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name ="surveyed_by")
    private Integer surveyedBy;

    @Column(name = "upload_on")
    private Date uploadOn;

    @Column(name = "surveyor_image")
    private String surveyorImage;

    @Column(name = "geom")
    private Geometry geom;

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


}
