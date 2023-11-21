package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyBidListPDFDTO {
    private List<ListOfBidsDto> listOfBids;
    private String bidId;
    private java.sql.Date dateOfBid;

}
