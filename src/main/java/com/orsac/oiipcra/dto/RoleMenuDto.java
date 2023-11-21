package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuDto {

    private Integer id;
    private String name;
    private Integer parentId;
    private String module;
    private Integer roleId;
    private Integer menuId;
    private Boolean active;
    private Integer createdBy;
    private Integer updatedBy;
    private Boolean isDefault;
}
