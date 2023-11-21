package com.orsac.oiipcra.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.AnnualFinancialTurnoverMaster;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.entities.CompletionOfSimilarTypeOfWork;
import com.orsac.oiipcra.entities.Tender;
import com.orsac.oiipcra.entities.TenderPublished;
import com.orsac.oiipcra.entities.TenderStipulation;
import com.orsac.oiipcra.exception.RecordExistException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;


public interface TenderService  {
    Boolean checkBidIdExists(String bidId);

    List<CompletionOfSimilarTypeOfWork> saveCompletionOfSimilarWorkValue(List<CompletionOfSimilarTypeOfWork> completionOfSimilarTypeOfWork,Integer bidderId);

    OIIPCRAResponse addTender(String data, MultipartFile[] tenderDoc) throws JsonProcessingException;
    OIIPCRAResponse addPreviousTender(String data, MultipartFile draftTenderNotice, MultipartFile bidDocument) throws JsonProcessingException;
    OIIPCRAResponse deactivateTenderDocumentData(Integer draftDocumentId);

    OIIPCRAResponse saveTenderCorrigendum(String data, int tenderId) throws JsonProcessingException;

    List<TenderCorrigendum> getTenderCorrigendumListByTenderId(int tender);

    Tender updateTender(int id, TenderRequest tenderRequest) throws Exception;

    List<TenderPublished> saveTenderPublish(List<TenderPublishedInfo> tenderPublish, int userId);

    Boolean deactivateTenderPublish(Integer id);

    OIIPCRAResponse tenderSearchList(TenderDto tenderDto);

    Boolean checkWorkIdentificationCodeExists(String workIdentificationCode);

    TenderNotice addTenderNotice(TenderNoticeDto tenderNoticeDto);

    List<TenderNoticeLevelMapping> saveTenderNoticeLevelMapping(List<TenderNoticeLevelMapping> tenderNoticeLevelMapping, int tenderNoticeId);

    TenderNoticeLevelMapping saveTenderNoticeLevelMappingSingle(TenderNoticeLevelMapping tenderNoticeLevelMapping, int tenderNoticeId);

    List<WorkProjectMapping> addWorkProjectMapping(List<WorkProjectMapping> workProjectMapping, int tenderNoticeId, int bidId);

    WorkProjectMapping saveWorkProjectMapping(WorkProjectMapping workProjectMapping, int tenderNoticeId, int bidId);

    OIIPCRAResponse getTenderNoticeById(Integer noticeId);

   // List<workProjectResponse> workProject= tenderRepositoryImpl.getWorkProject(noticeId);
    List<WorkProjectDto> getWorkProjectMappingByTenderNoticeId(int tenderNoticeId);

    TenderNotice updateTenderNotice(int id, TenderNoticeDto tenderNoticeDto);

    WorkProjectMapping updateWorkProjectMappingByNoticeId(int tenderNoticeId, int id, WorkProjectMapping workProjectMapping);

    TenderNoticeLevelMapping updateTenderNoticeLevelMappingByNoticeId(int tenderNoticeId, int id, TenderNoticeLevelMapping tenderNoticeLevelMapping);

    OIIPCRAResponse getTenderNoticeDate(Integer tenderId);

    OIIPCRAResponse addTenderStipulation(String data) throws RecordExistException, JsonProcessingException;

    OIIPCRAResponse getBiddingType();

    OIIPCRAResponse getTenderWorkType();

    OIIPCRAResponse addMeetingProceedings(String data)throws JsonProcessingException ;

    OIIPCRAResponse getEstimateIdAndWork(Integer id);

    OIIPCRAResponse getEstimateForTender(Integer userId,Integer estimateId);

    OIIPCRAResponse getAllMeetingType(Integer roleId);

    OIIPCRAResponse getAllTenderPublishType();

    List<AnnualFinancialTurnoverMaster> saveAnnualFinancialTurnover(List<AnnualFinancialTurnoverMaster> annualFinancialTurnover,Integer agencyId,Integer bidderId);

    OIIPCRAResponse viewTenderByTenderId(Integer tenderId);

    OIIPCRAResponse getTenderNoticeList(TenderListDto tenderListDto);

    OIIPCRAResponse getTenderStipulationList(TenderStipulationList tenderStipulationList);

    OIIPCRAResponse getTenderList(TenderListDto tenderListDto);
    OIIPCRAResponse2 getPreviousTenderData( TenderNoticePublishListDto tender);

    Page<TenderNoticePublishDto> getTenderData(TenderNoticePublishListDto tender);

    OIIPCRAResponse getAllProject();

    OIIPCRAResponse getAllSchemeOfFunding();

    OIIPCRAResponse getAllWork();



    OIIPCRAResponse viewTenderStipulationByTenderId(Integer tenderId);

    OIIPCRAResponse viewTenderStipulationById(Integer stipulationId);

    TenderStipulation updateTenderStipulation(Integer id, TenderStipulationDto tenderStipulation);

    BidderDetails createBidderDetails(BidderDetails bidderDetails);
    Integer updateCompletionOfSimilarTypeOfWorkValidation(Integer bidderId,Boolean completionValidity);
    Integer updateLiquidAssetValidation(Integer bidderId,Boolean liquidAssetValidity);

    Integer updateAnnualTurnOverValidation(Integer bidderId,Boolean annualTurnOverValidity,Double maxBidCapacity);

    LiquidAssetAvailability createLiquidAsset(LiquidAssetAvailability liquidAsset,Integer bidderId,Integer agencyId);

    OIIPCRAResponse getTenderDetailsForStipulationByBidId(Integer tenderId);

    OIIPCRAResponse getBidDetailsForComparison(Integer workId,Integer tenderId);

    OIIPCRAResponse getWorkSlNoByBidId(Integer tenderId);
    OIIPCRAResponse getWorkSlNoAndAgencyByBidId(Integer tenderId);

    //Tender Opening Validation Functions
    FinancialBidDetails addFinancialBidDetails(FinancialBidMasterDto financialBidMasterDto);

    List<FinancialBidInfo> getFinancialBidDetailsById(Integer id);
    List<FinancialBidInfo> getFinancialAbstract(Integer bidId,Integer workId,Integer bidderId);

    FinancialBidDetails updateFinancialBidDetailsById(Integer id, FinancialBidMasterDto financialBidDto);

    OIIPCRAResponse getTenderNoticeByTenderId(Integer tenderId);
    OIIPCRAResponse getTenderNoticeByTenderIdListing(TenderNoticeListingDto tenderNotice);

    List<BidderDetailsDto> getBidderDetailsByTenderId(Integer tenderId,Integer workId);

    List<AgencyMaster> getAllAgencyName(Integer tenderId,Integer workId);


    List<AgencyDto> getAgencyDetailsById(Integer id);
    Integer getBidderIdExistsOrNotByAgencyIdAndNoticeId(Integer agencyId,Integer workId);

    List<BidderCategoryDto> getAllBidderCategory();

    List<EmdDepositTypeDto> getDepositType();
    List<CompletionOfSimilarTypeOfWorkDto> getCompletionOfSimilarWorkTypeByBidderId(Integer bidderId);

    List<AnnualFinancialTurnoverMasterDto> getAnnualFinancialTurnOver(Integer bidderId);
    List<LiquidAssetAvailability> getLiquidAssetAvailabilityByBidderId(Integer bidderId);
    List<ExpenditureYearMonthDto> getExpenditureDataByTankId(ExpenditureWorkProgress expenditure);

    Boolean checkLicenseValid(Date date1, Integer id);

    Boolean getLiquidAsset(Integer bidderId, Integer bidId, Integer workId);

    Boolean emdValid(Integer tenderId, Integer bidderId);

    DraftTenderNoticeDto getAllTenderNotice(Integer tenderId);


    ContractInfo getContractDetailsByTenderId(Integer id, Integer workId);

   List<ExpenditureDataInfo> getExpenditureDetailsBy(Integer id, Integer workId);

    List<AgencyDto> getAllBidders();

    TenderResult addTenderResult(TenderResultDto tenderResult);
    TenderResult updateFinancialAbstract(TenderResultDto tenderResult,Integer id);

    List<AwardTypeDto> getAllAwardType();
    FinancialBidDto getTenderBidderDetailsByBidId(Integer bidderId,Integer workId);

    List<FinancialBidDto> getAllAgencyNameByWorkId(Integer workId);

    BidderDetails getBidderDetailsById(Integer bidderId);

    FinancialBidInfo getFinancialBidDetailsByBidderId(Integer bidderId);

    List<AgencyDto> getAllAgencyByContractId(Integer contractId);

    List<Integer> getTenderIds();

    List<TenderDto> getAllBidIdForContractDone(List<Integer> tenderIds);

    List<TenderNoticeDto> getAllWorkIdForContractDone(Integer tenderId);

    OIIPCRAResponse bidIdExistsOrNot(String bidId);

    OIIPCRAResponse getDistinctPaperNameFromPublication();
    OIIPCRAResponse getTenderByEstimateId(Integer estimateId);

    boolean updateTenderApproval(Integer tenderId, ApproveStatusDto updateTenderApproval);

    Boolean changeTenderStatus(String bidId, Integer statusId);

    Boolean changeTenderNoticeLevelMapping(Integer tenderNoticeId);
    List<DistrictBoundaryDto> getAllDistrictForWebsite();

    TenderResult updateTenderResult(TenderResultDto tenderResult, Integer tenderId, Integer workId, Integer bidderId);


    OIIPCRAResponse workIdentificationCodeExistsOrNotByBidId(Integer tenderId);

    List<TenderDto> getBidIdListForTender(Integer userId);
    List<AgencyDto> getQualifiedAgencyByBidId(Integer tenderId,Integer workId);
    List<TenderNoticeDto> getSubDivisionOfficerNameBySubDivisionId(Integer subDivisionId);
 MeetingProceedingDto getMeetingProceedingsByTenderId(Integer tenderId, Integer meetingTypeId);
 ShlcMeetingDto getShlcMeeting(java.sql.Date date);
 List<ShlcMeetingProceedingsEntity> getShlcMeetingProceedings(Integer shlcId);
 List<CommitteeMembersDto> getCommitteeMemberByMeetingProceedingId(Integer meetingProceedingId);
 List<CommitteeMembersDto> getCommitteeMemberByShlcMeetingId (Integer shlcId);

 List<PreBidClarificationsDto> getPreBidClarificationByMeetingProceedingId(Integer meetingProceedingId);

    List<ActivityEstimateTankMappingDto> getTankNameByActivityId(Integer activityId);
}

