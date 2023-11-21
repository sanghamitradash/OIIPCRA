package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfo {

    private int id;
    private String name;
    private int parentRoleId;
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

}
