package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuInfo {

    private Integer roleId;
    private List<Integer> menuId;
    private Integer createdBy;
    private Boolean isDefault;
}
