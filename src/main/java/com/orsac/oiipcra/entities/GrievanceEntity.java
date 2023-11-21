package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grievance")
public class GrievanceEntity {

    @Id
    @SequenceGenerator(name = "grievance_sequence", sequenceName = "grievance_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grievance_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private Long mobile;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "address")
    private String address;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updtedOn;

    @Column(name = "status")
    private Integer status;

    @Column(name = "document")
    private String document;

    @Column(name = "image")
    private String image;

    @Column(name = "resolution_level")
    private Integer resolutionLevel;

    @Column(name = "resolved_user_id")
    private Integer resolvedUserId;

    @Column(name = "designation_id")
    private Integer designationId;
    @Column(name = "dept_id")
    private Integer deptId;

}
