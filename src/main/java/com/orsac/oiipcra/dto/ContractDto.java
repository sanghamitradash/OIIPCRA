package com.orsac.oiipcra.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
/*import com.orsac.oiipcra.entities.ContractDocumentModel;*/
/*import com.orsac.oiipcra.entities.ContractMappingModel;*/
import com.orsac.oiipcra.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDto {
    private Integer id;
    private String contractCode;
    private String contractName;
    private Date contractDate;
    private Integer contractStatusId;
    private Integer tenderId;
    private Integer contractTypeId;
    private Integer contractLevelId;
    private Integer procurementMadeFor;
    private String zone;
    private Date approvalOrderDate;
    private String workDescription;
    private String eoiId;
    private Date rfpIssuedOn;
    private Date rfpReceivedOn;
    private Integer areaId;
    private String correspondenceFileNo;
    private String contractNumber;
    private Integer agencyId;
    private Double contractAmount;
    private Double gst;
    private Date stipulatedDateOfComencement;
    private Date stipulatedDateOfCompletion;
    private String approvalOrder;
    private String tachnicalSanctionNo;
    private Integer workId;
    private Integer activityId;
    private Double estimatedCost;
    private Integer awardedAs;
    private String agreementNumber;
    private String loaIssuedNo;
    private Date loaIssuedDate;
    private Double rateOfPremium;
    private Date actualDateOfCommencement;
    private Date actualDateOfCompletion;
    private boolean isActive;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer finyrId;
    private Date dateEoi;
    private Integer estimateId;
    private Integer workTypeId;
    private Integer typeId;
    private Integer procurementTypeId;
    private Integer contractId;
    private String contractLevel;
    private String ContractType;
    private String contractStatus;
    private String agencyName;
    private String workTypeName;
    private List<ContractMappingDto> contractMappingDto;
    private List<ContractDocumentDto> contractDocumentDto;
    private List<PhysicalProgressUpdateDto> physicalProgressWork;

    private List<PhysicalProgressConsultancy> physicalProgressConsultancy;
    private RfqEntity rfqData;
    private DirEntity dirData;
    private CdsEntity cdsData;
    private QcbsEntity qcbsData;





}
