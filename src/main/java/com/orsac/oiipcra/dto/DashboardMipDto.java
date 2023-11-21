package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.AccessType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMipDto {
    private Integer count;
    List<String> paniPanchayatName;
    DprInformationDto dprInfo;
    Double estimateCostOfCivilWork;
    Double  expenditureByPreviousMonth;
    Double  expenditureThisMonth;
    Double totalExpenditure;
    List<String> villageName;
    private  String tankName;


}
