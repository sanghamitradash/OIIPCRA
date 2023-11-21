package com.orsac.oiipcra.dto;


import com.orsac.oiipcra.entities.ExpenditureMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureDto {

   private Integer id;

   private String bidId;
   private Integer workId;
   private String workType;
   private String paymentType;
   private Double value;
   private String paymentDate;
   private Integer paymentYear;
   private Integer paymentMonth;
   private String agencyName;
   private Integer contractId;
   private String contractName;
   private Integer activityId;
   private Integer finyrId;
   private String finYear;
   private Integer monthId;
   private String monthName;
   private Integer deviceId;
   private Integer invoiceId;
   private String invoiceNo;
   private Integer agencyId;
   private String paymentTypeName;
   private Integer estimateId;
   private Integer tankId;
   private Integer level;
   private Integer type;
   private String activityName;
   private  String divisionName;
   private String invoiceNumber;
   private String code;
   private String panNo;
   private String  description;


   List<ExpenditureMapping> expenditureMapping;



}
