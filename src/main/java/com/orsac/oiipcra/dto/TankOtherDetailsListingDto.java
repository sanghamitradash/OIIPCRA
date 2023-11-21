package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.databind.DatabindException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TankOtherDetailsListingDto {

    private String deptDistName;
    private String deptGpName;
    private String miDivisionName;
    private Integer projectId;
    private String nameOfTheMip;
    private String deptBlockName;


    private Integer distId;
    private Integer blockId;
    private Integer divisionId;
    private Integer tankId;
    private String uploadFrom;
    private String uploadTo;
    private Integer approvedStatus;
    private Integer id;

    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
}
