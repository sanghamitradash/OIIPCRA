package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.ContractListRequestDto;
import com.orsac.oiipcra.dto.InvoiceListRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SurveyRepository {

    Page<TankSurveyInfoResponse> getTankSurveySearchList(SurveyListRequest surveyListRequest,List<Integer>tankIdsByIssueId,List<Integer>tankIdsByContractId,List<Integer>tankIdsByEstimateId);
    Page<ActivitySurveyListInfo> searchActivityList(ActivitySearchRequest activitySearchRequest);
 //   Page<InvoiceInfo> getInvoiceList(InvoiceListRequestDto invoiceListRequestDto);

    //Page<ContractInfoListing> getContractList(ContractListRequestDto contractInfo,List<Integer> activityIds, List<Integer> estimateIds, List<Integer> tenderIds,List<Integer> tenderNoticeIds);

  //  Page<TankInfo> getTankSearchList(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId);


    Page<ContractInfoListing> getContractList(ContractListRequestDto contractInfo, List<Integer> activityIds, List<Integer> estimateIds, List<Integer> tenderIds, List<Integer> tenderNoticeIds, List<Integer> contractIdsByExpenditureId, List<Integer> contractIdsByInvoiceId) throws ArrayIndexOutOfBoundsException;

    /**
     * 538 tank Search List
     */

    Page<TankInfo> getTankSearchList(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId, List<Integer> tankIdsByExpenditureId, List<Integer>tankIdsByInvoiceId,List<Integer> tankIdsByEstimateId, List<Integer>tankIdsByIssueId,List<Integer>cadTankIds,List<Integer>surveyTankIds,List<Integer>depthTankIds,List<Integer>feederTankIds,List<Integer> civilWorkIds,List<Integer> fopTankIds,List<Integer> droppedTankIds,List<Integer> proposedToBeDroppedTankIds,List<Integer> progressTankIds);

    Page<invoiceListingInfo> getInvoiceList(InvoiceListRequestDto invoiceInfo, List<Integer> terminalList);

    List<TankInfo> getTankDetailsById(int id, int flagId);
    TankSurveyInfo getSurveyInfoGeoJsonById(int id);
    int updateSurveyTankData(TankSurveyInfo tankSurveyInfo,int id);
}
