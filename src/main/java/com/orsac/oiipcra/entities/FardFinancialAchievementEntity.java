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
@Table(name = "fard_financial_achievement")
public class FardFinancialAchievementEntity {
    @Id
    @SequenceGenerator(name = "fard_financial_achievement_details", sequenceName = "fard_financial_achievement_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fard_financial_achievement_details")
    @Column(name = "id")
    private Integer id;
    @Column(name = "year")
    private String year;
    @Column(name = "district_name")
    private String districtName;

    @Column(name = "directorate")
    private String directorate;

    @Column(name = "scheme_name")
    private String schemeName;
    @Column(name = "scheme_id")
    private Integer schemeId;
    @Column(name = "sub_scheme_id")
    private Integer subSchemeId;

    @JsonProperty("financial_allocation_in_app")
    private Double financialAllocationInApp;

    @JsonProperty("actual_fund_allocated")
    private Double actualFundAllocated;

    @JsonProperty("expenditure")
    private Double expenditure;
    @Column(name = "adapt_dist_id")
    private Integer adaptDistId;

    @Column(name = "year_id")
    private Integer yearId;

    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "activity_id")
    private Integer activityId;




}
