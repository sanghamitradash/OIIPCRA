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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_m")
public class Role {

    @Id
    @SequenceGenerator(name = "role_sequence", sequenceName = "role_m_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name = "parent_role_id")
    private Integer parentRoleId;

    @Column(name = "can_edit")
    private boolean canEdit;

    @Column(name = "can_view")
    private boolean canView;

    @Column(name = "can_add")
    private boolean canAdd;

    @Column(name = "can_delete")
    private boolean canDelete;

    @Column(name = "can_approve")
    private boolean canApprove;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn = new Date(System.currentTimeMillis());

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "is_deletion_request_access")
    private boolean deletionRequestAccess;

    @Column(name = "is_deletion_approval_access")
    private boolean deletionApprovalAccess;

    @Column(name = "is_addition_request_access")
    private boolean additionRequestAccess;

    @Column(name = "is_addition_approval_access")
    private boolean additionApprovalAccess;

    @Column(name = "is_survey_access")
    private boolean surveyAccess;


    @Column(name = "is_issue_resolution_access")
    private boolean issueResolutionAccess;


    @Column(name = "is_permission_access")
    private boolean permissionAccess;


}
