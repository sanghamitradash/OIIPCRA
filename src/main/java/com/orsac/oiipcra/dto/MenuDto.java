package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {

    private int menuId;
    private String menuName;
    private Integer parentId;
    private String targetUrl;
    private List<HierarchyMenuDto> children;

    private boolean active;

    private Integer createdBy;

    private Date createdOn ;

    private Integer updatedBy;

    private Date updatedOn;

}
