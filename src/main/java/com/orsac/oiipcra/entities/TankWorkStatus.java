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
@Table(name = "tank_work_status")
public class TankWorkStatus {

    @Id
    @SequenceGenerator(name = "tank_sequence", sequenceName = "tank_survey_data_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tank_sequence")
    @Column(name = "id")
    private int id ;

    @Column(name="tank_id")
    private int tankId ;

    @Column(name="work_status_id")
    private int workStatusId ;

    @Column(name="unit_id")
    private int unitId ;

    @Column(name="noOfUnit")
    private int no_of_unit ;

    @Column(name="contract_id")
    private int contractId ;

    @Column(name="longitude")
    private double longitude ;

    @Column(name="latitude")
    private double latitude;

    @Column(name="altitude")
    private double altitude ;

    @Column(name="accuracy")
    private double accuracy;

    @Column(name="device_id")
    private int deviceId ;

    @Column(name="user_id")
    private int userId ;

    @Column(name="division_id")
    private int divisionId ;

    @Column(name="sub_division_id")
    private int subDivisionId ;

    @Column(name="section_id")
    private int sectionId ;

    @Column(name="tank_boundary")
    private int tankBoundary ;

    @Column(name="approval_status")
    private int approvalStatus ;

    @Column(name="activity_id")
    private int activityId ;

    @Column(name="is_active")
    private boolean isActive;

    @Column(name="created_by")
    private int createdBy ;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_on")
    private Date createdOn ;

    @Column(name="updated_by")
    private int updatedBy ;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_on")
    private Date updatedOn ;



}
