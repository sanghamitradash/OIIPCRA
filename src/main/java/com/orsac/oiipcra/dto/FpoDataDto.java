package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FpoDataDto {
    private  Integer tankId;
    private  Integer projectId;
    private  Integer fpoId;
    private String fpoName;
    private  String contactPerson;
    private  String designationName;
    private Long  contactNumber1;
    private Long  contactNumber2;
    private String emailId1;
    private String emailId2;
    private String emailId3;




}
