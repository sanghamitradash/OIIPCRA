package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.AnnualFinancialTurnoverMaster;
import com.orsac.oiipcra.entities.BidderDetails;
import com.orsac.oiipcra.entities.CompletionOfSimilarTypeOfWork;
import com.orsac.oiipcra.entities.LiquidAssetAvailability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderOpeningDto {
   private BidderDetails bidderDetails;
   private List<CompletionOfSimilarTypeOfWork> completionOfSimilarTypeOfWorkList;
   private List<AnnualFinancialTurnoverMaster> annualFinancialTurnoverMasters;
   private LiquidAssetAvailability liquidAssetAvailabilities;
}
