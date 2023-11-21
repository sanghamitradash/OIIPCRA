package com.orsac.oiipcra.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeListingDto {

    private Integer id;
    private Integer biddingTypeId;
    private String biddingType;
    private Integer typeOfWork;
    private String workType;
    private Integer workSlNoInTcn;
    private String workIdentificationCode;
    private String nameOfWork;
  //  private Integer tankId;
    private String tankName;
   // private Integer distId;
    private String distName;
  //  private Integer blockId;
    private String blockName;
    private Double tenderAmount;
    private Double paperCost;
    private Double emdToBeDeposited;
    private Double timeForCompletion;
    private Long contactNo;
    private Integer projectId;
    private String projectName;
    private String tenderLevel;
    private String tenderNotAwardedReason;
    private String eeName;
    private Integer eeType;
    private String eeTypeName;
    private String otherEe;
    private Long eeContactNo;
    private Integer eeId;
    private String bidId;

}
