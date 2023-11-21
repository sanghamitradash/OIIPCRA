package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueResolutionLevelDto {

    private Integer id;
    private String levelName;
    private Integer parentId;


}
