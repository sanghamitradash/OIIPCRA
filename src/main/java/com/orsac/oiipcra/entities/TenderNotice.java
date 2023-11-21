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
@Table(name = "tender_notice")
public class TenderNotice {

    @Id
    @SequenceGenerator(name = "tender_notice_sequence", sequenceName = "tender_notice_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tender_notice_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name = "bidding_type")
    private Integer biddingType;

    @Column(name = "type_of_work")
    private Integer typeOfWork;

    @Column(name = "work_sl_no_in_tcn")
    private Integer workSlNoInTcn;

    @Column(name = "work_identification_code")
    private String workIdentificationCode;

    @Column(name = "name_of_work")
    private String nameOfWork;

    @Column(name = "tender_amount")
    private Double tenderAmount;

    @Column(name = "paper_cost")
    private Double paperCost;

    @Column(name = "emd_to_be_deposited")
    private Double emdToBeDeposited;

    @Column(name = "time_for_completion")
    private Double timeForCompletion;

    @Column(name = "contact_no")
    private Long contactNo;

    @Column(name = "tender_level_id")
    private Integer tenderLevelId;

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

    @Column(name = "circle_id")
    private Integer circleId;

    @Column(name = "division_id")
    private Integer divisionId;

    @Column(name = "ee_id")
    private Integer eeId;

    @Column(name = "sub_division_id")
    private Integer subDivisionId;

    @Column(name = "sub_division_officer")
    private Integer subDivisionOfficer;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "tender_not_awarded_reason")
    private String tenderNotAwardedReason;

    @Column(name = "ee_type")
    private Integer eeType;

    @Column(name = "other_ee")
    private String otherEe;

    @Column(name = "ee_contact_no")
    private Long eeContactNo;

    @Column(name = "sectionId")
    private Integer sectionId;

    @Column(name = "other_sub_division_officer")
    private String otherSubDivisionOfficer;


}
