package com.orsac.oiipcra.bindings;

import com.orsac.oiipcra.entities.UserAreaMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveRequests {

    private Integer id;
    private String name;
    private String email;
    private Long mobileNumber;
    private Integer designationId;
    private Integer departmentId;
    private String password;
    private Integer userLevelId;
    private Boolean active;
    private Integer roleId;
    private Integer subDeptId;
    private Boolean  surveyor;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private List<UserAreaMapping> userAreaMapping;

}
