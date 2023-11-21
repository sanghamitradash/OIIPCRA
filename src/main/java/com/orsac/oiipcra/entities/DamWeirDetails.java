package com.orsac.oiipcra.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dam_weir_details")
public class DamWeirDetails {

    @Id
    @SequenceGenerator(name = "dam_weir_details_sequence", sequenceName = "dam_weir_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dam_weir_details_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "name_of_stream")
    private String nameOfStream;

    @Column(name = "water_source")
    private Integer waterSource;

    @Column(name = "dry_weather_flow_rate_of_water_source")
    private Double dryWeatherFlowRateOfWaterSource;

    @Column(name = "maximum_height_of_dam_m")
    private Double maximumHeightOfDamM;

    @Column(name = "length_of_dam_m")
    private Double lengthOfDamM;

    @Column(name = "present_top_width_of")
    private Double presentTopWidthOf;

    @Column(name = "upstream_pitching_riprap_provided")
    private boolean upstreamPitchingRiprapProvided;

    @Column(name = "status_of_pitching")
    private String statusOfPitching;

    @Column(name = "down_stream_slope_drains")
    private boolean downStreamSlopeDrains;

    @Column(name = "status_of_down_stream")
    private String statusOfDownStream;

    @Column(name = "rock_toe_if_provided")
    private boolean rockToeIfProvided;

    @Column(name = "status_of_rock_toe")
    private String statusOfRockToe;

    @Column(name = "v_notch_down_stream_of_rocktoe")
    private boolean vNotchDownStreamOfRockToe;

    @Column(name = "status_of_v_notch")
    private String statusOfVNotch;

    @Column(name = "instrumentation_installed")
    private boolean instrumentationInstalled;

    @Column(name = "status_of_instrumentation")
    private String statusOfInstrumentation;

    @Column(name = "tbl_in_m")
    private Double tblInM;

    @Column(name = "frl_in_m")
    private Double frlInM;

    @Column(name = "mwl_in_m")
    private Double mwlInM;

    @Column(name = "dsl_in_m")
    private Double dslInM;

    @Column(name = "length_of_diversion")
    private Double lengthOfDiversion;

    @Column(name = "length_of_over_flow")
    private Double lengthOfOverFlow;

    @Column(name = "type_of_weir_crest")
    private Integer typeOfWeirCrest;

    @Column(name = "weir_crest_is")
    private Integer weirCrestIs;

    @Column(name = "crest_level_m")
    private Double crestLevelM;

    @Column(name = "max_pond_level")
    private Double maxPondLevel;

    @Column(name = "width_of_crest")
    private Double widthOfCrest;

    @Column(name = "up_stream_appron_level")
    private Double upStreamAppronLevel;

    @Column(name = "down_stream_appron_level")
    private Double downStreamAppronLevel;

    @Column(name = "no_of_scour_vents")
    private Double noOfScourVents;

    @Column(name = "size_of_scour_vent")
    private Double sizeOfScourVent;

    @Column(name = "down_stream_appron_length")
    private Double downStreamAppronLength;

    @Column(name = "type_of_construction_of_bodywall")
    private Double typeOfConstructionOfBodywall;

    @Column(name = "type_of_construction_abutments")
    private Integer typeOfConstructionAbutments;

    @Column(name = "status_of_body_wall")
    private String statusOfBodyWall;

    @Column(name = "status_of_abutments")
    private String statusOfAbutments;

    @Column(name = "status_of_return_walls")
    private String statusOfReturnWalls;

    @Column(name = "status_of_down")
    private String statusOfDown;

    @Column(name = "status_of_cut_off")
    private String statusOfCutOff;

    @Column(name = "status_of_end_sill")
    private String statusOfEndSill;

    @Column(name = "status_of_ss_gates")
    private String statusOfSsGates;

    @Column(name = "status_of_hr_gates")
    private String statusOfHrGates;

    @Column(name="is_active",nullable = false)
    private Boolean isActive=true;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

















}
