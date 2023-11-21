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
@Table(name = "demographic_details")
public class DemographicDetails {

    @Id
    @SequenceGenerator(name = "demographic_details", sequenceName = "demographic_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "demographic_details")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "no_of_villages_in_command_area")
    private Integer noOfVillagesInCommandArea;

    @Column(name = "total_population_of_command")
    private Integer totalPopulationOfCommand;

    @Column(name = "percentage_of_sc_population")
    private Double percentageOfScPopulation;

    @Column(name = "percentage_of_st_population")
    private Double percentageOfStPopulation;

    @Column(name = "percentage_of_obc_population")
    private Double percentageOfObcPopulation;

    @Column(name = "total_population_of_the_block")
    private Integer totalPopulationOfTheBlock;

    @Column(name = "population_of_male_in_the_block")
    private Integer populationOfMaleInTheBlock;

    @Column(name = "population_of_female_in_the_block")
    private Integer populationOfFemaleInTheBlock;

    @Column(name = "male_literacy_in_the_block_perc")
    private Double maleLiteracyInTheBlockPerc;

    @Column(name = "female_literacy_in_the_block_perc")
    private Double femaleLiteracyInTheBlockPerc;

    @Column(name = "general_profession_of_population")
    private String generalProfessionOfPopulation;

    @Column(name = "perc_agricultural_labour_in_the_block")
    private Double percAgriculturalLabourInTheBlock;

    @Column(name = "perc_of_cultivators_in_the_block")
    private Double percOfCultivatorsInTheBlock;

    @Column(name = "perc_of_non_working_population")
    private Double percOfNonWorkingPopulation;

    @Column(name = "livestock_population")
    private Integer livestockPopulation;

    @Column(name = "fishermen_population")
    private Integer fishermenPopulation;

    @Column(name = "year_of_census_data")
    private Integer yearOfCensusData;

    @Column(name = "present_funding_scheme")
    private String presentFundingScheme;

    @Column(name = "present_funding_amount_in_lakh")
    private Double presentFundingAmountInLakh;

    @Column(name="is_active",nullable = false)
    private Boolean isActive;

    @Column(name = "created_by")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;

    @Column(name = "tank_other_details_id")
    private Integer tankOtherDetailsId;

}
