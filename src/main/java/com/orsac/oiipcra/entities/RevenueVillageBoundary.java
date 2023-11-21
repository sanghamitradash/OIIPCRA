package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "village_boundary")
public class RevenueVillageBoundary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="gid")
    private int gid;

    @Column(name ="revenue_village_name")
    private String revenueVillageName;

    @Column(name ="revenue_village_code")
    private String revenueVillageCode;

    @Column(name ="grampanchayat_name")
    private String grampanchayatName;

    @Column(name ="grampanchayat_code")
    private String grampanchayatCode;

    @Column(name ="block_name")
    private String blockName;

    @Column(name ="block_code")
    private String blockCode;

    @Column(name ="district_name")
    private String districtName;

    @Column(name ="district_code")
    private String districtCode;

    @Column(name ="state_name")
    private String stateName;

    @Column(name ="state_code")
    private String stateCode;

    @Column(name ="gp_id")
    private int gpId;

    @Column(name="village_id")
    private int villageId;

    @Column(name="dist_id")
    private int districtId;

    @Column(name="block_id")
    private int blockId;


}
