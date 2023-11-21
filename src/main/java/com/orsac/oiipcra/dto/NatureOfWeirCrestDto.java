package com.orsac.oiipcra.dto;


import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.NamedEntityGraph;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NatureOfWeirCrestDto {

    private Integer id;
    private String name;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
}
