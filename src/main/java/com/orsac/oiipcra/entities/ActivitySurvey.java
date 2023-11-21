package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="activity_survey")
public class ActivitySurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name= "tank_id")
    private Integer tankId;

    @Column(name= "work_status_id")
    private Integer workStatusId;

    @Column(name= "unit_id")
    private Integer unitId ;

    @Column(name="no_of_unit")
    private Integer noOfUnit;

    @Column(name= "contract_id")
    private Integer contractId;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude ;

    @Column(name = "altitude")
    private Double altitude ;

    @Column(name = "accuracy")
    private Double accuracy ;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name="division_id")
    private Integer divisionId;

    @Column(name="sub_division_id")
    private Integer subDivisionId;

    @Column(name="section_id")
    private Integer sectionId;

    @Column(name="approval_status")
    private Integer approvalStatus ;

    @Column(name="activity_id")
    private Integer activityId;

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
    private Boolean active;

    @Column(name="dist_id")
    private Integer distId;

    @Column(name="block_id")
    private Integer blockId;

    @Column(name="gp_id")
    private Integer gpId;

    @Column(name="village_id")
    private Integer villageId;

    @Column(name="surveyor_image")
    private String surveyorImage;

    @Column(name="image_path")
    private String imagePath;

    @Column(name="target_value")
    private  Double targetValue;

    @Column(name="delay_days")
    private  Integer delayDays;


}
