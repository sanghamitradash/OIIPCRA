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
@Table(name="office_data")
public class OfficeDataEntity {
    @Id
    @SequenceGenerator(name = "office_sequence", sequenceName = "office_data_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "office_name")
    private String officeName;

    @Column(name = "head_of_dept")
    private String headOfDept;

    @Column(name = "head_of_office")
    private String headOfOffice;

    @Column(name = "designation")
    private Integer designation;

    @Column(name = "spu_address")
    private String spuAddress;

    @Column(name = "spu_post")
    private String spuPost;

    @Column(name = "spu_email")
    private String spuEmail;

    @Column(name = "land_line_no")
    private String landLineNo;

    @Column(name = "spu_pin_no")
    private Integer spuPinNo;

    @Column(name = "dist_id")
    private Integer distId;

    @Column(name = "division_id")
    private Integer divisionId;

    @Column(name = "copy_to_1")
    private String copyTo1;

    @Column(name = "copy_to_2")
    private String copyTo2;

    @Column(name = "copy_to_3")
    private String copyTo3;

    @Column(name = "copy_to_4")
    private String copyTo4;

    @Column(name = "copy_to_5")
    private String copyTo5;

    @Column(name = "copy_to_6")
    private String copyTo6;

    @Column(name = "copy_to_7")
    private String copyTo7;

    @Column(name = "copy_to_8")
    private String copyTo8;

    @Column(name = "copy_to_9")
    private String copyTo9;

    @Column(name = "copy_to_10")
    private String copyTo10;

    @Column(name = "copy_to_11")
    private String copyTo11;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_on")
    private Date createdOn ;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_on")
    private Date updatedOn ;


}
