package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.bindings.ActivityUpperHierarchyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdaptFilterDto {

    private Integer distId;
    private Integer blockId;
    private Integer gpId;
    private Integer finYear;
    private Integer deptId;


    private Integer page;

    private Integer size;

    private String sortOrder;

    private String sortBy;
    private Integer inOiipcra;
    private Integer activityId;
}
