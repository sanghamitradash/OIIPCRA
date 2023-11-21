package com.orsac.oiipcra.bindings;

import com.orsac.oiipcra.entities.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserInfo {

    private Integer id;
    private String userName;
    private String email;
    private Long mobileNumber;
    private String department;
    private String userLevel;
    private String role;
    private String designation;
    private Boolean active;
    private Integer createdBy;
    private Integer updatedBy;
    private Boolean surveyor;

}
