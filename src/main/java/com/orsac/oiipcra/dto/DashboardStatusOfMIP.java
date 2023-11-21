package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatusOfMIP {

/*
    private Integer takenUp;
    private Integer completed;
    private Integer progress;
    private Integer agrmtAwaited;
    private Integer tenderScrutiny;
    private Integer tenderInvited;
    private Integer estimateApproved;
    private Integer toBeEstimated;
*/

    private String name;
    private Integer value;

}
