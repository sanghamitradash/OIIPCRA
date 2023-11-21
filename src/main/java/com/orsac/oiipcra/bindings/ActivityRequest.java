package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {
    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Integer masterHeadId;
    private Boolean terminalFlag;
    private Boolean activeFlag;
    private String description;
    private Integer levelId;

   /* private Integer levelId;
    private Integer approveId;
    private Integer statusId;
    private Integer distId;
    private Integer miDivisionId;
    private Integer workType;
    private Integer approvalOrder;
    private Integer technicalSanctionNo;
    private Integer projectId;
    private String procurementMadeFor;
    private String districtZoneIdentification;
    private Date nolOfTorByWb;
    private String approvalRef;
    private String correspondanceFileNo;
    private Date startDate;*/

    private List<Integer> finyrId;
    private List<Integer> unitId;
    private List<Double> physicalTarget;
    private List<Double> unitCostRs;
    private List<Double> financialTarget;
    private Double contractAmount;

    private Integer createdBy;
    private Date createdOn = new Date(System.currentTimeMillis());
    private Integer updatedBy;
    private Date updatedOn;

//   private  List<ActivityAddDto> activityAddDto;
}
