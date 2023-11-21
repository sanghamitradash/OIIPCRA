package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fard_physical_achievement")
public class FardPhysicalAchievementEntity {
    @Id
    @SequenceGenerator(name = "fard_physical_achievement", sequenceName = "fard_physical_achievement_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fard_physical_achievement")
    @Column(name = "id")
    private Integer id;
    @Column(name = "year")
    private String year;
    @Column(name = "district_name")
    private String districtName;

    @Column(name = "block_name")
    private String blockName;

    @Column(name = "gp_name")
    private String gpName;

    @Column(name = "directorate")
    private String directorate;

    @Column(name = "scheme_name")
    private String schemeName;


    @Column(name = "target")
    private Integer target;

    @Column(name = "achievement")
    private Integer achievement;

    @Column(name = "no_of_beneficiaries")
    private Integer noofBeneficiaries;

    @Column(name = "achievement_percentage")
    private Integer achievementPercentage;
    @Column(name = "dist_id")
    private Integer distId;

    @Column(name = "block_id")
    private Integer blockId;

    @Column(name = "gp_id")
    private Integer gpId;

    @Column(name = "year_id")
    private Integer yearId;

    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "scheme_id")
    private Integer schemeId;
    @Column(name = "sub_scheme_id")
    private Integer subSchemeId;


    @Column(name = "activity_id")
    private Integer activityId;


    @Column(name = "fard_scheme_id")
    private Integer fardSchemeId;

    @Column(name = "is_active")
    private boolean isActive;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn = new Date(System.currentTimeMillis());


}
