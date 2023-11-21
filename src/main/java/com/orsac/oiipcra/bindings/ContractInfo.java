package com.orsac.oiipcra.bindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractInfo {
    private Integer id;
    private String contractCode;
    private String contractName;
    private String contractDate;
    private Integer contractStatusId;
    private String contractStatus;
    private Integer contractTypeId;
    private String contractType;
    private Integer contractLevelId;
    private String contractLevel;
    private Integer procurementMadeFor;
    private String zone;
    private Date approvalOrderDate;
    private String workDescription;
    private String eoiId;
    private Date rfpIssuedOn;
    private Date rfpReceivedOn;
    private Integer areaId;
   // private String areaOfOperation;
    private String correspondenceFileNo;
    private String contractNumber;
    private Integer agencyId;
    private String agency;
    private Double contractAmount;
    private Double gst;
  //  private Double gstAmount;
    private Double totalAmount;
    private String stipulatedDateOfComencement;
    private String stipulatedDateOfCompletion;
   // private Integer timePeriod;
    //private Integer actualTimePeriod;
    private String approvalOrder;
    private String tachnicalSanctionNo;
    private Integer workId;
    private Double estimatedCost;
    private String awardedAs;
    private String agreementNumber;
    private String loaIssuedNo;
    private Date loaIssuedDate;
    private Double rateOfPremium;
    private Date actualDateOfCommencement;
    private Date actualDateOfCompletion;
    private boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer finyrId;
    private String financialYear;
    private Date dateEoi;
    private Integer activityId;
    private Integer estimateId;
    private Integer typeId;
    private Integer workTypeId;

    private int page;
    private int size;
    private  String sortOrder;
    private String sortBy;
    private String workTypeName;
    private String procurementTypeName;
    private String componentName;
    private String subComponentName;
    private String activityName;
    private String subActivityName;
    private Integer procurementTypeId;





}
