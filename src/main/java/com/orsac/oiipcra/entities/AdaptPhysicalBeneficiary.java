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
@Table(name = "adapt_physical_beneficiary")
public class AdaptPhysicalBeneficiary {

    @Id
    @SequenceGenerator(name = "adapt_physical_beneficiary_details", sequenceName = "adapt_physical_beneficiary_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adapt_physical_beneficiary_details")
    @Column(name = "id")
    private Integer id;

    @Column(name = "year_id")
    private Integer yearId;

    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "block_id")
    private Integer blockId;

    @Column(name = "gp_id")
    private Integer gpId;

    @Column(name = "village_id")
    private Integer villageId;

    @Column(name = "village_name")
    private String villageName;

    @Column(name = "scheme_id")
    private Integer schemeId;

    @Column(name = "scheme_name")
    private String schemeName;

    @Column(name = "component_id")
    private Integer componentId;

    @Column(name = "component_name")
    private String componentName;

    @Column(name = "mobile_number")
    private String mobileNo;

    @Column(name = "farmer_name")
    private String farmerName;

    @Column(name = "aadhar_number")
    private String aadhaarNo;

    @Column(name = "lat_long")
    private String latLong;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "block_name")
    private String blockName;

    @Column(name = "gp_name")
    private String gpName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn = new Date(System.currentTimeMillis());

    @Column(name = "is_active")
    private Boolean isActive;



}
