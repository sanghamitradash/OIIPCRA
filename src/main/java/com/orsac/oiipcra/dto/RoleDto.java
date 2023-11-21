package com.orsac.oiipcra.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Integer id;
    private String name;
    private String description;
    private  Integer parentRoleId;
    private Integer areaMId;
    private Integer userLevelId;
    private boolean canEdit;
    private boolean canView;
    private boolean canAdd;
    private boolean canDelete;
    private boolean canApprove;
    private boolean deletionRequestAccess;
    private boolean deletionApprovalAccess;
    private boolean additionRequestAccess;
    private boolean additionApprovalAccess;
    private boolean surveyAccess;
    private boolean issueResolutionAccess;
    private boolean permissionAccess;
    private boolean active;
}
