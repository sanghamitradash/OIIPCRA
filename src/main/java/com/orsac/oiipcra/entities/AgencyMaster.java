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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "agency_m")
public class AgencyMaster {


        @Id
        @SequenceGenerator(name = "agency_master_sequence", sequenceName = "agency_m_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agency_master_sequence")
        @Column(name = "id")
        private Integer id;

        @Column(name = "name")
        private String name;

        @Column(name = "address")
        private String address;

        @Column(name = "phone")
        private Long phone;

        @Column(name = "pan_no")
        private String panNo;

        @Column(name = "is_active")
        private boolean isActive;

        @Column(name = "created_by")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private Integer createdBy;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_on")
        @CreationTimestamp
        private Date createdOn = new Date(System.currentTimeMillis());

        @Column(name = "updated_by")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private Integer updatedBy;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "updated_on")
        @UpdateTimestamp
        private Date updatedOn;
        @Column(name = "license_class_id")
        private Integer licenseClassId;
        @Column(name = "exempt_id")
        private Integer exemptId;
        @Column(name = "gstin_no")
        private String gstinNo;
        @Column(name = "license_validity")
        private Date licenseValidity;
        @Column(name = "post")
        private String post;
        @Column(name = "dist_id")
        private Integer distId;
        @Column(name = "pincode")
        private Integer pincode;
        @Column(name = "image_name")
        private String imageName;
        @Column(name = "image_path")
        private String imagePath;

}
