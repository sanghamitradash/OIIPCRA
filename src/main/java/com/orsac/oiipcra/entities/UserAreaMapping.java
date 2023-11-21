package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_area_mapping")
public class UserAreaMapping {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "user_area_sequence", sequenceName = "user_area_mapping_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_area_sequence")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "dist_id")
    private Integer distId;

    @Column(name = "block_id")
    private Integer blockId;

    @Column(name = "gp_id")
    private Integer gpId;

    @Column(name = "village_id")
    private Integer villageId;

    @Column(name = "division_id")
    private Integer divisionId;

    @Column(name = "subdivision_id")
    private Integer subdivisionId;

    @Column(name = "section_id")
    private Integer sectionId;
    
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by", updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Column(name = "created_on", updatable = false)
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Column(name = "updated_on")
    @JsonIgnore
    @UpdateTimestamp
    private Date updatedOn;

}
