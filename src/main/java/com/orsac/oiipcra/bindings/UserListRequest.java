package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListRequest {

    private int id;
    private int searchId;
    private int deptId;
    private int designation;
    private int userLevelId;
    private int distId;
    private int blockId;
    private int gpId;
    private int villageId;
    private int divisionId;
    private int subDivisionId;
    private int sectionId;
    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
    private Long mobileNumber;

}
