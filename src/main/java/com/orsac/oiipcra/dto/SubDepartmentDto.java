package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubDepartmentDto {

    private Integer id;
    private Integer deptId;
    private String name;
    private Boolean active;
    private Integer createdBy;
    private Integer updatedBy;
}
