package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "denormalized_achievement")
public class DenormalizedAchievement {

    @Id
    @SequenceGenerator(name = "denormalized_details", sequenceName = "denormalized_achievement_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "denormalized_details")
    @Column(name = "id")
    private Integer id;
    @Column(name = "year")
    private Integer year;
    @Column(name = "district_name")
    private String districtName;

    @Column(name = "block_name")
    private String blockName;

    @Column(name = "gp_name")
    private String gpName;

    @Column(name = "directorate")
    private String directorate;


    @Column(name = "master_component")
    private String masterComponent;

    @Column(name = "scheme_name")
    private String schemeName;

    @Column(name = "component_name")
    private String componentName;

    @Column(name = "target")
    private Integer target;

    @Column(name = "achievement")
    private Integer achievement;

    @Column(name = "no_of_beneficiaries")
    private Integer noofBeneficiaries;

    @Column(name = "achievement_percentage")
    private Integer achievementPercentage;
    @Column(name = "adapt_dist_id")
    private Integer adaptDistId;

    @Column(name = "adapt_block_id")
    private Integer adaptBlockId;

    @Column(name = "adapt_gp_id")
    private Integer adaptGpId;

    @Column(name = "year_id")
    private Integer yearId;

    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "scheme_id")
    private Integer schemeId;

    @Column(name = "component_id")
    private Integer componentId;


}
