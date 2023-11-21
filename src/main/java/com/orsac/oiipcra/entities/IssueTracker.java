package com.orsac.oiipcra.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "issue_tracker")
public class IssueTracker {

    @Id
    @SequenceGenerator(name = "issue_tracker_sequence", sequenceName = "issue_tracker_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issue_tracker_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "contract_id")
    private Integer contractId;

    @Column(name = "issue_type_id")
    private Integer issueTypeId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "status")
    private Integer status;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "resolution_remarks")
    private String resolutionRemarks;

    @Column(name = "resolved_by")
    private Integer resolvedBy;

    @Column(name="is_active",nullable = false)
    private Boolean isActive=true;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "estimate_id")
    private Integer estimateId;

    @Column(name = "issue_date")
    private Date issueDate;

    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "resolution_level")
    private Integer resolutionLevel;

    @Column(name = "permission_required")
    private Boolean permissionRequired;

    @Column(name = "description")
    private String description;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved_on")
    @CreationTimestamp
    private Date approvedOn;

    @Column(name = "work_id")
    private Integer workId;

    @Column(name = "designation_id")
    private Integer designationId;

    @Column(name = "resolved_user_id")
    private Integer resolvedUserId;



}
