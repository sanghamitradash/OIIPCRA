package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureWorkProgress {

    private Integer tankId;
    private Integer bidId;
    private Integer workId;
    private Integer contractId;
    private Integer estimateId;
}
