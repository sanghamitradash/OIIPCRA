package com.orsac.oiipcra.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.exception.RecordExistException;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.*;
import com.orsac.oiipcra.repositoryImpl.MasterRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.TenderRepositoryImpl;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.TenderService;
import com.orsac.oiipcra.utility.Constant;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class TenderServiceImpl implements TenderService {

    @Autowired
    private ConfigurableEnvironment env;
    @Value("${accessBidDocumentPreviousPath}")
    private String accessBidDocumentPreviousPath;
    @Value("${accessDraftTenderNoticePreviousPath}")
    private String accessDraftTenderNoticePreviousPath;
    @Autowired
    private TenderNoticePublishRepository tenderNoticePublishRepository;

    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    AnnualFinancialTurnoverRepository annualFinancialTurnoverRepository;

    @Autowired
    private ActivityServiceImpl activityServiceImpl;

    @Autowired
    private TenderResultRepository tenderResultRepository;


    @Autowired
    private MasterRepositoryImpl masterRepositoryImpl;

    @Autowired
    private AWSS3StorageService awss3StorageService;

    @Autowired
    private TenderLevelMappingRepository tenderLevelMappingRepository;

    @Autowired
    private TenderRepositoryImpl tenderRepositoryImpl;


    @Autowired
    private WorkProjectRepository workProjectRepository;

    @Autowired
    private TenderNoticeRepository tenderNoticeRepository;

    @Autowired
    private MeetingProceedingRepository meetingProceedingRepository;

    @Autowired
    private CommitteeMembersRepository committeeMembersRepository;

    @Autowired
    private TenderStipulationRepository tenderStipulationRepository;

    @Autowired
    private AWSS3StorageServiceImpl aws;

    @Autowired
    private TenderDocRepository tenderDocRepository;

    @Autowired
    private FinancialBidRepository financialBidRepository;

    @Autowired
    private PreBidClarificationsRepository preBidClarificationsRepository;

    @Autowired
    private BidderDetailsRepository bidderDetailsRepository;

    @Autowired
    private LiquidAssetRepository liquidAssetRepository;

    @Autowired
    private TenderPublishedRepository tenderPublishedRepository;


    @Autowired
    private CompletionOfSimilarTypeRepository completionOfSimilarTypeRepository;

    @Autowired
    private TenderNoticeLevelRepository tenderNoticeLevelRepository;

    @Autowired
    private TenderCorrigendumRepository tenderCorrigendumRepository;

    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    private ActivityQryRepository activityQryRepository;


    @Override
    public Boolean checkBidIdExists(String bidId) {
        return tenderRepository.existsByBidId(bidId);
    }

    public Boolean workIdentificationCodeExists(Integer tenderId) {
        return tenderNoticeRepository.existsByTenderId(tenderId);
    }

    @Override
    public List<CompletionOfSimilarTypeOfWork> saveCompletionOfSimilarWorkValue(List<CompletionOfSimilarTypeOfWork> completionOfSimilarTypeOfWork, Integer bidderId) {
        List<CompletionOfSimilarTypeOfWorkDto> similarWorkType = tenderRepositoryImpl.getCompletionOfSimilarWorkTypeByBidderId(bidderId);
        List<CompletionOfSimilarTypeOfWork> similarWorkType1 = new ArrayList<>();
        if (similarWorkType.size() == 0) {
            List<CompletionOfSimilarTypeOfWork> work = new ArrayList<>();
            for (CompletionOfSimilarTypeOfWork workSimilar : completionOfSimilarTypeOfWork) {
                workSimilar.setActive(true);
                workSimilar.setBidderId(bidderId);
                work.add(workSimilar);
            }
            similarWorkType1 = completionOfSimilarTypeRepository.saveAll(work);
        } else {
            for (CompletionOfSimilarTypeOfWork workSimilar : completionOfSimilarTypeOfWork) {
                workSimilar.setBidderId(bidderId);
                Integer update = tenderRepositoryImpl.updateCompletionOfSimilarTypeOfWork(workSimilar);

                similarWorkType1.add(workSimilar);
            }
        }


        return similarWorkType1;
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public OIIPCRAResponse addTender(String data, MultipartFile[] tenderDoc) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        ObjectMapper mapper = new ObjectMapper();
        TenderRequest tenderRequest = mapper.readValue(data, TenderRequest.class);
        List<TenderPublishedInfo> published = tenderRequest.getTenderPublishedInfo();
        TenderStipulationDto stipulationDto = tenderRequest.getStipulation();
        TenderStipulation tenderStipulation1 = new TenderStipulation();
        BeanUtils.copyProperties(stipulationDto, tenderStipulation1);


        String tenderCode = null;
        try {
            if (tenderRequest != null) {
                Tender tender = new Tender();
                BeanUtils.copyProperties(tenderRequest, tender);
                /* List<FinYrDto> finYrList = masterRepositoryImpl.getFinYrList(tenderRequest.getFinyrId());
                NameCodeTree node = activityServiceImpl.getParentNameCodeTree(tenderRequest.getActivityId());
                if (finYrList != null && node != null) {
                    tenderCode = finYrList.get(0).getName().concat("_").concat(node.getCodeTree());
                }
                tender.setCode(tenderCode);*/
                tender.setIsActive(tenderRequest.getActive());
                tender.setApprovedStatus(1);
                Integer finyrId = tenderRepositoryImpl.getFinyrIdByPublicationDate(tenderRequest.getTenderPublicationDate());
                tender.setFinyrId(finyrId);
//                tender.setEstimateId(0);
                if (checkBidIdExists(tender.getBidId())) {
                    throw new RecordExistException("Tender", "BidId", tender.getBidId());
                }
                Tender tenderId = tenderRepository.save(tender);
                if (tenderId != null) {
                    /*TenderLevelMapping tenderLevelMapping = new TenderLevelMapping();
                    tenderLevelMapping.setTenderId(tenderId.getId());
                    tenderLevelMapping.setActivityId(tenderRequest.getActivityId());
                    tenderLevelMapping.setDistId(tenderRequest.getDistId());
                    tenderLevelMapping.setBlockId(tenderRequest.getBlockId());
                    tenderLevelMapping.setGpId(tenderRequest.getGpId());
                    tenderLevelMapping.setVillageId(tenderRequest.getVillageId());
                    tenderLevelMapping.setDivisionId(tenderRequest.getDivisionId());
                    tenderLevelMapping.setSubDivisionId(tenderRequest.getSubDivisionId());
                    tenderLevelMapping.setSectionId(tenderRequest.getSectionId());
                    tenderLevelMapping.setCreatedBy(tenderRequest.getCreatedBy());
                    tenderLevelMapping.setUpdatedBy(tenderRequest.getUpdatedBy());
                    tenderLevelMapping.setActive(tenderRequest.getActive());
                    tenderLevelMappingRepository.save(tenderLevelMapping);*/
                    List<TenderPublished> tenderPublishedList = new ArrayList<>();
                    if (published != null && published.size() > 0) {
                        for (TenderPublishedInfo publish : published) {
                            TenderPublished tenderPublished = new TenderPublished();
                            tenderPublished.setTenderId(tenderId.getId());
                            tenderPublished.setSerialNo(publish.getSerialNo());
                            tenderPublished.setTenderPublishedType(1);
                            tenderPublished.setName(publish.getName());
                            tenderPublished.setNewspaperType(publish.getNewspaperType());
                            tenderPublished.setPublishedDate(publish.getPublishedDate());
                            tenderPublished.setPublicationPeriodUpto(publish.getPublicationPeriodUpto());
                            tenderPublished.setCreatedBy(tenderRequest.getCreatedBy());
                            tenderPublished.setUpdatedBy(tenderRequest.getUpdatedBy());
                            tenderPublished.setActive(tenderRequest.getActive());
                            tenderPublishedList.add(tenderPublished);
                        }
                        tenderPublishedRepository.saveAll(tenderPublishedList);
                    }
                    tenderStipulation1.setTenderId(tenderId.getId());
                    TenderStipulation tenderStipulationMObj = tenderStipulationRepository.save(tenderStipulation1);
                }

                /*if(tenderDoc != null || tenderDoc.length>0) {
                    boolean uploadDocs = uploadTenderDocument(Arrays.asList(tenderDoc), tenderId.getId());
                    if (!uploadDocs){
                        List<TenderDocument> tenderList = new ArrayList<>();
                        TenderDocument document = new  TenderDocument();
                        for(MultipartFile multipartFile :tenderDoc){
                            document.setDocName(multipartFile.getOriginalFilename());
                            document.setTenderId(tenderId.getId());
                            document.setCreatedBy(tenderRequest.getCreatedBy());
                            document.setUpdatedBy(tenderRequest.getUpdatedBy());
                            document.setActive(tenderRequest.getActive());
                            tenderList.add(document);
                        }
                        tenderDocRepository.saveAll(tenderList);
                    }
                }*/
/*                TenderPublishDateCaluculateDto type1 = tenderRepositoryImpl.getTenderPublication(tenderId.getId());
                TenderPublishDateCaluculateDto type2 = tenderRepositoryImpl.getCorrigendumDate(tenderId.getId());
                TenderPublishDateCaluculateDto periodUpto = tenderRepositoryImpl.getPublicationPeriodUpto(tenderId.getId());
                Boolean update = tenderRepositoryImpl.updateTenderDate(tenderId.getId(), type1, type2, periodUpto);*/
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Inserted Successfully.");
            } else {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Data not found");
            }
            //response.setData(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(0);
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setMessage(e.getMessage());
            response.setData(Collections.emptyList());
            log.error(e.getMessage());
        }
        return response;
    }

    @Override
    public OIIPCRAResponse addPreviousTender(String data, MultipartFile draftTenderNotice, MultipartFile bidDocument) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        ObjectMapper mapper = new ObjectMapper();
        TenderNoticePublishDto previousTender = mapper.readValue(data, TenderNoticePublishDto.class);
        TenderNoticePublishedEntity tender = new TenderNoticePublishedEntity();
        BeanUtils.copyProperties(previousTender, tender);
        try {
            if (previousTender != null) {
                if (bidDocument != null) {
                    tender.setBidDocument(bidDocument.getOriginalFilename());
                }
                if (draftTenderNotice != null) {
                    tender.setDraftTenderNoticeDoc(draftTenderNotice.getOriginalFilename());
                }
                TenderNoticePublishedEntity tenderPrevious = tenderNoticePublishRepository.save(tender);
                if (draftTenderNotice != null) {
                    boolean saveDraftTenderNoticeDocument = awss3StorageService.uploadDraftTenderNoticeForPreviousTender(draftTenderNotice, String.valueOf(tenderPrevious.getId()), draftTenderNotice.getOriginalFilename());
                }
                if (bidDocument != null) {
                    boolean saveBidDocument = awss3StorageService.uploadBidDocumentForPreviousTender(bidDocument, String.valueOf(tenderPrevious.getId()), bidDocument.getOriginalFilename());
                }
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("PreviousTenderData Added Successfully.");
            } else {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Data not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(0);
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setMessage(e.getMessage());
            response.setData(Collections.emptyList());
        }
        return response;
    }


    @Override
    public OIIPCRAResponse saveTenderCorrigendum(String data, int tenderId) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        ObjectMapper mapper = new ObjectMapper();
        TenderRequest tenderRequestForCorrigendum = mapper.readValue(data, TenderRequest.class);
        List<TenderPublishedInfo> published = tenderRequestForCorrigendum.getTenderPublishedInfo();
        try {

            if (tenderRequestForCorrigendum != null) {
                TenderCorrigendum tenderCorrigendum = new TenderCorrigendum();
                BeanUtils.copyProperties(tenderRequestForCorrigendum, tenderCorrigendum);
                tenderCorrigendum.setTenderId(tenderId);
                tenderCorrigendum.setActive(true);
                tenderCorrigendumRepository.save(tenderCorrigendum);
                List<TenderPublished> tenderPublishedList = new ArrayList<>();
                if (published != null && published.size() > 0) {
                    for (TenderPublishedInfo publish : published) {
                        TenderPublished tenderPublished = new TenderPublished();
                        tenderPublished.setTenderId(tenderId);
                        tenderPublished.setSerialNo(publish.getSerialNo());
                        tenderPublished.setTenderPublishedType(2);
                        tenderPublished.setName(publish.getName());
                        tenderPublished.setNewspaperType(publish.getNewspaperType());
                        tenderPublished.setPublishedDate(publish.getPublishedDate());
                        tenderPublished.setPublicationPeriodUpto(publish.getPublicationPeriodUpto());
                        tenderPublished.setCreatedBy(tenderRequestForCorrigendum.getCreatedBy());
                        tenderPublished.setUpdatedBy(tenderRequestForCorrigendum.getUpdatedBy());
                        tenderPublished.setActive(true);
                        tenderPublishedList.add(tenderPublished);
                    }
                    tenderPublishedRepository.saveAll(tenderPublishedList);
                }
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Corrigendum Added Successfully.");
            } else {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Data not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(0);
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setMessage(e.getMessage());
            response.setData(Collections.emptyList());
        }
        return response;
    }

    @Override
    public List<TenderCorrigendum> getTenderCorrigendumListByTenderId(int tenderId) {
        return tenderRepositoryImpl.getTenderCorrigendumListByTenderId(tenderId);
    }


    @Override
    public Tender updateTender(int id, TenderRequest tenderRequest) throws Exception {
        Tender existingTender = tenderRepository.findById(id);
        if (existingTender == null) {
            throw new RecordNotFoundException("Tender", "id", id);
        }
        existingTender.setBidId(tenderRequest.getBidId());
        existingTender.setApprovalForProcurementDate(tenderRequest.getApprovalForProcurementDate());
        existingTender.setTechnicalBidOpeningDate(tenderRequest.getTechnicalBidOpeningDate());
        existingTender.setTechnicalBidOpeningDateRevised(tenderRequest.getTechnicalBidOpeningDateRevised());
        existingTender.setBidSubmissionDate(tenderRequest.getBidSubmissionDate());
        existingTender.setBidSubmissionDateRevised(tenderRequest.getBidSubmissionDateRevised());
        existingTender.setFinancialBidOpeningDate(tenderRequest.getFinancialBidOpeningDate());
        existingTender.setUpdatedBy(tenderRequest.getUpdatedBy());
        existingTender.setNameOfWork(tenderRequest.getNameOfWork());
        existingTender.setPreBidMeetingType(tenderRequest.getPreBidMeetingType());
        existingTender.setPreBidMeetingDate(tenderRequest.getPreBidMeetingDate());
        existingTender.setTenderPublicationDate(tenderRequest.getTenderPublicationDate());
        existingTender.setPublicationPeriodUpto(tenderRequest.getPublicationPeriodUpto());
        existingTender.setTenderType(tenderRequest.getTenderType());
        existingTender.setTenderLevelId(tenderRequest.getTenderLevelId());
        existingTender.setFinyrId(tenderRequest.getFinyrId());
        existingTender.setActivityId(tenderRequest.getActivityId());
        existingTender.setTenderOpeningDate(tenderRequest.getTenderOpeningDate());
        existingTender.setDateOfTenderNotice(tenderRequest.getDateOfTenderNotice());
        existingTender.setEstimateId(tenderRequest.getEstimateId());
        existingTender.setTenderStatus(tenderRequest.getTenderStatus());
        existingTender.setMeetingLocation(tenderRequest.getMeetingLocation());
        existingTender.setIsActive(true);
        Tender save = tenderRepository.save(existingTender);
        return save;
    }

    @Override
    public Boolean deactivateTenderPublish(Integer id) {
        return tenderRepositoryImpl.deactivateTenderPublish(id);
    }

    @Override
    public List<TenderPublished> saveTenderPublish(List<TenderPublishedInfo> publishedInfo, int id) {
        List<TenderPublished> tenderPublishedList = new ArrayList<>();
        for (TenderPublishedInfo publish : publishedInfo) {
            TenderPublished tenderPublished = new TenderPublished();
            tenderPublished.setTenderId(id);
            tenderPublished.setSerialNo(publish.getSerialNo());
            tenderPublished.setTenderPublishedType(1);
            tenderPublished.setName(publish.getName());
            tenderPublished.setNewspaperType(publish.getNewspaperType());
            tenderPublished.setPublishedDate(publish.getPublishedDate());
            tenderPublished.setPublicationPeriodUpto(publish.getPublicationPeriodUpto());
            tenderPublished.setCreatedBy(publish.getCreatedBy());
            tenderPublished.setUpdatedBy(publish.getUpdatedBy());
            tenderPublished.setActive(true);
            if (publish.getDocument() != null && publish.getDocument() != "") {
                tenderPublished.setDocument("https://ofarisbucket.s3.ap-south-1.amazonaws.com/tenderPublished/" + id + "/" + publish.getDocument());
            }
            tenderPublishedList.add(tenderPublished);
        }
        tenderPublishedRepository.saveAll(tenderPublishedList);
        /*TenderPublishDateCaluculateDto type1 = tenderRepositoryImpl.getTenderPublication(id);
        TenderPublishDateCaluculateDto type2 = tenderRepositoryImpl.getCorrigendumDate(id);
        TenderPublishDateCaluculateDto periodUpto = tenderRepositoryImpl.getPublicationPeriodUpto(id);
        Boolean update = tenderRepositoryImpl.updateTenderDate(id, type1, type2, periodUpto);*/

        return tenderPublishedList;

    }

    /**
     * Tender Search list
     */
    @Transactional
    public OIIPCRAResponse tenderSearchList(TenderDto tenderDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<TenderResponse> tenderListPage = tenderRepositoryImpl.tenderSearchList(tenderDto);
            List<TenderResponse> tenderList = tenderListPage.getContent();
            result.put("tankList", tenderList);
            result.put("currentPage", tenderListPage.getNumber());
            result.put("totalItems", tenderListPage.getTotalElements());
            result.put("totalPages", tenderListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tenders.");
        } catch (Exception e) {
            log.info("Tender List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @Override
    public Boolean checkWorkIdentificationCodeExists(String workIdentificationCode) {
        return tenderNoticeRepository.existsByWorkIdentificationCode(workIdentificationCode);
    }

    //Save Tender Notice
    @Override
    public TenderNotice  addTenderNotice(TenderNoticeDto tenderNoticeDto) {
        TenderNotice tenderNotice = new TenderNotice();
        BeanUtils.copyProperties(tenderNoticeDto, tenderNotice);
        tenderNotice.setTenderId(tenderNoticeDto.getTenderId());
        Integer workSlNo = tenderRepositoryImpl.getWorkSlNo(tenderNotice.getTenderId());
        tenderNotice.setWorkSlNoInTcn(workSlNo);
        List<TenderNotice> notice=tenderRepositoryImpl.getWorkIdentificationExistOrNot(tenderNotice.getWorkIdentificationCode(),tenderNotice.getTenderId());
        if (notice.size()>0) {
            throw new RecordExistException("TenderNotice", "checkWorkCodeIdExists", tenderNotice.getWorkIdentificationCode());
        }
        return tenderNoticeRepository.save(tenderNotice);
    }

    @Override
    public List<TenderNoticeLevelMapping> saveTenderNoticeLevelMapping(List<TenderNoticeLevelMapping> tenderNoticeLevelMapping, int tenderNoticeId) {
        for (TenderNoticeLevelMapping tenderNoticeMappingInfo : tenderNoticeLevelMapping) {
            tenderNoticeMappingInfo.setTenderNoticeId(tenderNoticeId);
            tenderNoticeMappingInfo.setActive(true);
        }
        return tenderNoticeLevelRepository.saveAll(tenderNoticeLevelMapping);
    }

    @Override
    public TenderNoticeLevelMapping saveTenderNoticeLevelMappingSingle(TenderNoticeLevelMapping tenderNoticeLevelMapping, int tenderNoticeId) {

        tenderNoticeLevelMapping.setTenderNoticeId(tenderNoticeId);
        tenderNoticeLevelMapping.setActive(true);
        return tenderNoticeLevelRepository.save(tenderNoticeLevelMapping);
    }

    @Override
    public List<WorkProjectMapping> addWorkProjectMapping(List<WorkProjectMapping> workProjectMapping, int tenderNoticeId, int tenderId) {
        for (WorkProjectMapping workProjectMapping1 : workProjectMapping) {
            workProjectMapping1.setTenderNoticeId(tenderNoticeId);
            workProjectMapping1.setTenderId(tenderId);
        }
        return workProjectRepository.saveAll(workProjectMapping);
    }

    @Override
    public WorkProjectMapping saveWorkProjectMapping(WorkProjectMapping workProjectMapping, int tenderNoticeId, int bidId) {
        workProjectMapping.setTenderNoticeId(tenderNoticeId);
        workProjectMapping.setTenderId(bidId);
        return workProjectRepository.save(workProjectMapping);
    }

    /**
     * upload tender document file to aws server
     */
    public boolean uploadTenderDocument(List<MultipartFile> files, Integer tenderId) throws IOException {
        int count = 0;
        for (MultipartFile tenderFile : files) {
            boolean noOfDoc = aws.uploadTenderDoc(tenderFile, String.valueOf(tenderId), tenderFile.getOriginalFilename());
            if (noOfDoc) {
                count++;
            }
        }
        return files.size() == count;
    }
    /*@Override
    public OIIPCRAResponse addTenderNotice(String data) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        TenderNoticeDto tenderNoticeDto = objectMapper.readValue(data, TenderNoticeDto.class);
        List<WorkProjectDto> workProjectDto = tenderNoticeDto.getWorkProjectDto();
        try {
            TenderNotice tenderNotice = new TenderNotice();
            BeanUtils.copyProperties(tenderNoticeDto, tenderNotice);
            tenderNotice.setTenderId(tenderNoticeDto.getBidId());
            TenderNotice tenderNoticeMObj = tenderNoticeRepository.save(tenderNotice);

            WorkProjectMapping workProjectMapping = new WorkProjectMapping();
            BeanUtils.copyProperties(workProjectDto, workProjectMapping);
            workProjectDto.setTenderNoticeId(tenderNoticeMObj.getId());
            workProject1.setTenderId(tenderNoticeMObj.getTenderId());
            WorkProjectMapping workProjectMObj = workProjectRepository.save(workProject1);

            result.put("TenderNotice", tenderNoticeMObj);
            result.put("WorkProjectMapping",workProjectMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Work Created");
        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }*/

    @Override
    public OIIPCRAResponse getTenderNoticeById(Integer noticeId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            TenderNoticeResponse notice = tenderRepositoryImpl.getTenderNotice(noticeId);
            if (notice.getEeId() == null) {
                notice.setEeId(-99);
            }
            // Integer total=tenderRepositoryImpl.getTotalNotice(tenderId);
            Integer nextId = tenderRepositoryImpl.getNextId(noticeId);
            Integer previousId = tenderRepositoryImpl.getPreviousId(noticeId);
            notice.setNextId(nextId);
            notice.setPreviousId(previousId);
            String distName = "";
            String blockName = "";
            String tankName = "";
            List<TenderNoticeResponse> allNotice = new ArrayList<>();
            List<NoticeLevelMappingDto> noticeListing = tenderRepositoryImpl.getTenderLevelByNoticeId(notice.getId());
            List<WorkProjectDto> workListing = tenderRepositoryImpl.getWorkProjectMappingListByTenderNoticeId(notice.getId());

            for (int j = 0; j < noticeListing.size(); j++) {
                if (noticeListing.get(j).getDistName() != null) {
                    if (!distName.contains(noticeListing.get(j).getDistName())) {
                        if (j > 0) {
                            distName += ",";
                        }

                        distName += noticeListing.get(j).getDistName();
                    }
                }
                if (noticeListing.get(j).getBlockName() != null) {
                    if (!blockName.contains(noticeListing.get(j).getBlockName())) {
                        if (j > 0) {
                            blockName += ",";
                        }

                        blockName += noticeListing.get(j).getBlockName();
                    }
                }

                if (noticeListing.get(j).getTankName() != null) {
                    if (!tankName.contains(noticeListing.get(j).getTankName())) {
                        if (j > 0) {
                            tankName += ",";
                        }

                        tankName += noticeListing.get(j).getTankName();
                    }
                }
            }

            notice.setDistName(distName);
            notice.setBlockName(blockName);
            notice.setTankName(tankName);
            allNotice.add(notice);
            result.put("TenderNotice", notice);
            result.put("NoticeLevelMapping", noticeListing);
            result.put("WorkProjectMapping", workListing);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Tender Notice By ID");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public List<WorkProjectDto> getWorkProjectMappingByTenderNoticeId(int tenderNoticeId) {
        return tenderRepositoryImpl.getWorkProjectMappingListByTenderNoticeId(tenderNoticeId);
    }

    @Override
    public TenderNotice updateTenderNotice(int id, TenderNoticeDto tenderNoticeDto) {
        TenderNotice existingInfo = tenderNoticeRepository.findById(id);
        if (existingInfo == null) {
            throw new RecordNotFoundException("Tender Notice", "id", id);
        }
        existingInfo.setBiddingType(tenderNoticeDto.getBiddingType());
        existingInfo.setTypeOfWork(tenderNoticeDto.getTypeOfWork());
        existingInfo.setWorkSlNoInTcn(tenderNoticeDto.getWorkSlNoInTcn());
        existingInfo.setWorkIdentificationCode(tenderNoticeDto.getWorkIdentificationCode());
        existingInfo.setNameOfWork(tenderNoticeDto.getNameOfWork());
        //existingInfo.setDistId(tenderNoticeDto.getDistId());
        //existingInfo.setBlockId(tenderNoticeDto.getBlockId());
        existingInfo.setTenderAmount(tenderNoticeDto.getTenderAmount());
        existingInfo.setPaperCost(tenderNoticeDto.getPaperCost());
        existingInfo.setEmdToBeDeposited(tenderNoticeDto.getEmdToBeDeposited());
        existingInfo.setTimeForCompletion(tenderNoticeDto.getTimeForCompletion());
        existingInfo.setContactNo(tenderNoticeDto.getContactNo());
        existingInfo.setTenderLevelId(tenderNoticeDto.getTenderLevelId());
        existingInfo.setUpdatedBy(tenderNoticeDto.getUpdatedBy());
        existingInfo.setDivisionId(tenderNoticeDto.getDivisionId());
        existingInfo.setEeId(tenderNoticeDto.getEeId());
        existingInfo.setEeType(tenderNoticeDto.getEeType());
        existingInfo.setOtherEe(tenderNoticeDto.getOtherEe());
        existingInfo.setSubDivisionId(tenderNoticeDto.getSubDivisionId());
        existingInfo.setSectionId(tenderNoticeDto.getSectionId());
        existingInfo.setSubDivisionOfficer(tenderNoticeDto.getSubDivisionOfficer());
        if(tenderNoticeDto.getSubDivisionOfficer()!=null){
            existingInfo.setOtherSubDivisionOfficer(tenderNoticeDto.getOtherSubDivisionOfficer());
        }
        existingInfo.setProjectId(tenderNoticeDto.getProjectId());
        existingInfo.setCircleId(tenderNoticeDto.getCircleId());

        TenderNotice tenderNotice = tenderNoticeRepository.save(existingInfo);
        return tenderNotice;
    }

    @Override
    public WorkProjectMapping updateWorkProjectMappingByNoticeId(int tenderNoticeId, int id, WorkProjectMapping workProjectMapping) {
        WorkProjectMapping existingInfo = workProjectRepository.findById(id);
        //existingInfo.setTenderNoticeId(tenderNoticeId);
        existingInfo.setTankId(workProjectMapping.getTankId());
        return workProjectRepository.save(existingInfo);
    }

    @Override
    public TenderNoticeLevelMapping updateTenderNoticeLevelMappingByNoticeId(int tenderNoticeId, int id, TenderNoticeLevelMapping tenderNoticeLevelMapping) {
        TenderNoticeLevelMapping existingInfo = tenderNoticeLevelRepository.findById(id);
        //existingInfo.setTenderNoticeId(tenderNoticeId);
        existingInfo.setTankId(tenderNoticeLevelMapping.getTankId());
        existingInfo.setActive(true);
        existingInfo.setBlockId(tenderNoticeLevelMapping.getBlockId());
        existingInfo.setDistId(tenderNoticeLevelMapping.getDistId());
        //existingInfo.setUpdatedBy(tenderNoticeLevelMapping.getUpdatedBy());
        return tenderNoticeLevelRepository.save(existingInfo);
    }

    @Override
    public OIIPCRAResponse getTenderNoticeDate(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            TenderMasterDto tenderMasterDto = tenderRepositoryImpl.getTenderNoticeDate(tenderId);
            Integer workSlNo = tenderRepositoryImpl.getWorkSlNo(tenderId);
            Integer total = tenderRepositoryImpl.getTotalNotice(tenderId);
            tenderMasterDto.setWorkSlNo(workSlNo);
            tenderMasterDto.setTotalNoticeCount(total);
            if (tenderMasterDto != null) {
                result.put("TenderDate", tenderMasterDto);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Date Details");
        } catch (Exception e) {
            log.info("Tender Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse addTenderStipulation(String data) throws RecordExistException, JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        TenderStipulationDto tenderStipulation = objectMapper.readValue(data, TenderStipulationDto.class);
        try {
            TenderStipulation tenderStipulation1 = new TenderStipulation();
            BeanUtils.copyProperties(tenderStipulation, tenderStipulation1);
            tenderStipulation1.setTenderId(tenderStipulation.getBidId());
            Optional<Tender> existingTender = tenderRepository.findById(tenderStipulation1.getTenderId());
            if (existingTender == null) {
                throw new RecordNotFoundException("TenderStipulation", "tenderId", tenderStipulation1.getTenderId());
            }
            TenderStipulation tenderStipulationMObj = tenderStipulationRepository.save(tenderStipulation1);
            result.put("TenderStipulation", tenderStipulationMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New TenderStipulation Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse addMeetingProceedings(String data) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        MeetingProceedingDto meetingProceeding = objectMapper.readValue(data, MeetingProceedingDto.class);
//        CommitteeMembersDto committeeMembers = meetingProceeding.getCommitteeMembersDto();
        try {

            MeetingProceeding meetingProceeding1 = new MeetingProceeding();
            BeanUtils.copyProperties(meetingProceeding, meetingProceeding1);
            meetingProceeding1.setTenderId(meetingProceeding.getBidId());
            //meetingProceeding1.setMeetingType(2);
            meetingProceeding1.setActive(true);
            MeetingProceeding meetingProceedingMObj = meetingProceedingRepository.save(meetingProceeding1);
            if(meetingProceeding.getCommitteeMembersDto()!=null && meetingProceeding.getCommitteeMembersDto().size()>0) {
                List<CommitteeMembersDto> committeeMembers1 = meetingProceeding.getCommitteeMembersDto();
                CommitteeMembers committeeMembersMObj = null;
                for (CommitteeMembersDto committeeMembers2 : committeeMembers1) {
                    Integer designationId = tenderRepositoryImpl.getDesignationId(committeeMembers2.getMemberId());
                    CommitteeMembers committeeMember = new CommitteeMembers();
                    BeanUtils.copyProperties(committeeMembers2, committeeMember);
                    committeeMember.setMeetingProceedingId(meetingProceedingMObj.getId());
                    committeeMember.setCreatedBy(meetingProceedingMObj.getCreatedBy());
                    committeeMember.setDesignationId(designationId);
                    committeeMember.setMemberId(committeeMembers2.getMemberId());
                    committeeMember.setUpdatedBy(meetingProceedingMObj.getUpdatedBy());
                    committeeMember.setActive(true);
                    committeeMembersMObj = committeeMembersRepository.save(committeeMember);
                }
            }


           if(meetingProceeding.getPreBidClarificationsDto()!=null && meetingProceeding.getPreBidClarificationsDto().size()>0) {
               List<PreBidClarificationsDto> preBidClarifications = meetingProceeding.getPreBidClarificationsDto();
               PreBidClarifications preBidClarificationsMObj = null;
               if (preBidClarifications != null && preBidClarifications.size() > 0) {

                   for (PreBidClarificationsDto preBidClarification : preBidClarifications) {
                       PreBidClarifications pre = new PreBidClarifications();
                       BeanUtils.copyProperties(preBidClarification, pre);
                       pre.setMeetingProceedingId(meetingProceedingMObj.getId());
                       pre.setCreatedBy(meetingProceedingMObj.getCreatedBy());
                       pre.setUpdatedBy(meetingProceedingMObj.getUpdatedBy());
                       pre.setActive(true);
                       preBidClarificationsMObj = preBidClarificationsRepository.save(pre);
                   }
               }
           }
            result.put("MeetingProceeding", meetingProceedingMObj);
         /*   result.put("CommitteeMembers", committeeMembersMObj);
            result.put("PreBidClarifications", preBidClarificationsMObj);*/

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Meeting Proceeding Created");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getBiddingType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<WorksBiddingTypeDto> biddingType = tenderRepositoryImpl.getBiddingType();
            result.put("BiddingType", biddingType);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Bidding Type");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getTenderWorkType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderWorkTypeDto> workType = tenderRepositoryImpl.getTenderWorkType();
            result.put("WorkType", workType);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Tender Work Type");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getEstimateIdAndWork(Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityEstimate> estimate = tenderRepositoryImpl.getEstimateIdAndWork(id);
            if (estimate != null) {
                result.put("Estimate", estimate);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Estimate Details");
        } catch (Exception e) {
            log.info("Estimate Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getEstimateForTender(Integer userId, Integer estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityEstimate> estimate = tenderRepositoryImpl.getEstimateForTender(userId, estimateId);
            if (estimate != null) {
                result.put("Estimate", estimate);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Estimate Details");
        } catch (Exception e) {
            log.info("Estimate Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getAllMeetingType(Integer roleId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<MeetingDto> meeting = tenderRepositoryImpl.getAllMeetingType(roleId);
            if (meeting != null) {
                result.put("meetingType", meeting);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Meeting Details");
        } catch (Exception e) {
            log.info("Estimate Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getAllTenderPublishType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderPublishTypeDto> tenderPublish = tenderRepositoryImpl.getAllTenderPublishType();
            if (tenderPublish != null) {
                result.put("tenderPublishType", tenderPublish);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Publish List");
        } catch (Exception e) {
            log.info("Estimate Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public List<AnnualFinancialTurnoverMaster> saveAnnualFinancialTurnover(List<AnnualFinancialTurnoverMaster> annualFinancialTurnover, Integer agencyId, Integer bidderId) {
        // List<AnnualFinancialTurnoverMasterDto> financialTurnover=tenderRepositoryImpl.getAnnualFinancialTurnOverByAgencyId(agencyId);
        List<AnnualFinancialTurnoverMasterDto> financialTurnover = tenderRepositoryImpl.getAnnualFinancialTurnOver(bidderId);
        List<AnnualFinancialTurnoverMaster> financialTurnover1 = new ArrayList<>();
        if (financialTurnover.size() == 0) {
            List<AnnualFinancialTurnoverMaster> financial = new ArrayList<>();
            for (AnnualFinancialTurnoverMaster financialTurnOver : annualFinancialTurnover) {
                financialTurnOver.setActive(true);
                financialTurnOver.setAgencyId(agencyId);
                financialTurnOver.setBidderId(bidderId);
                financial.add(financialTurnOver);
            }
            financialTurnover1 = annualFinancialTurnoverRepository.saveAll(financial);
        } else {
            for (AnnualFinancialTurnoverMaster financialTurnOver : annualFinancialTurnover) {
                financialTurnOver.setBidderId(bidderId);
                financialTurnOver.setAgencyId(agencyId);
                Integer update = tenderRepositoryImpl.updateAnnualFinancialTurnOver(financialTurnOver);
                if (update == 1) {
                    financialTurnover1.add(financialTurnOver);
                }
            }

        }
        return financialTurnover1;
    }

    @Override
    public OIIPCRAResponse viewTenderByTenderId(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityUpperHierarchyInfo> upperLevel = new ArrayList<>();
            TenderInfo tenderMaster = tenderRepositoryImpl.viewTenderByTenderId(tenderId);
           /* TenderCorrigendumDto preBidMeetingDate= tenderRepositoryImpl.getPreBidMeetingDateFromTenderCorrigendum(tenderId);
            tenderMaster.setPreBidMeetingDate(preBidMeetingDate.getPreBidMeetingDate());*/
            if (tenderMaster.getActivityId() != null && tenderMaster.getActivityId() > 0) {
                //  ActivityEstimateResponseDto activity = activityQryRepository.getActivityEstimateByID(tenderMaster.getEstimateId());
                upperLevel = activityQryRepository.getUpperHierarchyInfoById(tenderMaster.getActivityId());

            }
            List<TenderPublishedInfo> tenderPublished = tenderRepositoryImpl.getAllTenderPublication(tenderId);
            Integer nextId = tenderRepositoryImpl.viewStipulationNextId(tenderId);
            tenderMaster.setNextId(nextId);
            if (tenderMaster != null) {
                result.put("TenderMaster", tenderMaster);
                result.put("upperDetails", upperLevel);
                result.put("tenderPublished", tenderPublished);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Details");
        } catch (Exception e) {
            log.info("Tender Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }


    @Override
    public OIIPCRAResponse getTenderNoticeList(TenderListDto tenderListDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<TenderNoticeDto> tenderNoticeListPage = tenderRepositoryImpl.getTenderNoticeList(tenderListDto);
            List<TenderNoticeDto> tenderNoticeList = tenderNoticeListPage.getContent();
            result.put("TenderNoticeList", tenderNoticeList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Tender Notice");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getTenderStipulationList(TenderStipulationList tenderStipulationList) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<TenderStipulationInfo> tenderStipulationListPage = tenderRepositoryImpl.getTenderStipulationList(tenderStipulationList);
            List<TenderStipulationInfo> tenderStipulation = tenderStipulationListPage.getContent();
            result.put("TenderStipulationList", tenderStipulation);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Tender Stipulation");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }


    @Override
    public OIIPCRAResponse getTenderList(TenderListDto tenderListDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> tenderIds = new ArrayList<>();
            List<Integer> expenditureIds = new ArrayList<>();

            if (tenderListDto.getContractId() != null && tenderListDto.getContractId() > 0) {
                tenderIds = tenderRepositoryImpl.getTenderIdsByContractId(tenderListDto.getContractId());
            }
            if (tenderListDto.getExpenditureId() != null && tenderListDto.getExpenditureId() > 0) {
                tenderIds = tenderRepositoryImpl.getTenderIdsByExpenditureId(tenderListDto.getExpenditureId());
            }
            Page<Tender> tenderListPage = tenderRepositoryImpl.getTenderList(tenderListDto, tenderIds);
            List<Tender> tender = tenderListPage.getContent();

            result.put("TenderList", tender);
            result.put("totalItems", tenderListPage.getTotalElements());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Tender List");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse2 getPreviousTenderData(@RequestBody TenderNoticePublishListDto tenderData) {
        OIIPCRAResponse2 response = new OIIPCRAResponse2();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<TenderNoticePublishDto> tenderListPage = tenderRepositoryImpl.getPreviousTenderData(tenderData);
            List<TenderNoticePublishDto> tender = tenderListPage.getContent();
            for (int i = 0; i < tender.size(); i++) {
                /*    tender.get(i).setDraw(tenderData.getDraw());*/
                if (tender.get(i).getDraftTenderNoticeDoc() != null && tender.get(i).getDraftTenderNoticeDoc() != "") {
                    tender.get(i).setDraftTenderNoticeDoc(accessDraftTenderNoticePreviousPath + tender.get(i).getId() + "/" + tender.get(i).getDraftTenderNoticeDoc());
                }
                if (tender.get(i).getBidDocument() != null && tender.get(i).getBidDocument() != "") {
                    tender.get(i).setBidDocument(accessBidDocumentPreviousPath + tender.get(i).getId() + "/" + tender.get(i).getBidDocument());
                }
            }
            result.put("TenderList", tender);
            result.put("totalItems", tenderListPage.getTotalElements());
            response.setData(result);
            response.setStatus(1);
            // response.setDraw(tenderData.getDraw());
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Tender List");
            response.setRecordsFiltered(tenderListPage.getTotalElements());
            response.setRecordsTotal(tenderListPage.getTotalElements());
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse2(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public Page<TenderNoticePublishDto> getTenderData(TenderNoticePublishListDto tender) {
        Page<TenderNoticePublishDto> tenderListPage = tenderRepositoryImpl.getTenderData(tender);
        return tenderListPage;
    }

    @Override
    public OIIPCRAResponse getAllProject() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProjectMasterDto> project = tenderRepositoryImpl.getAllProject();
            if (project != null) {
                result.put("projectList", project);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Project Details");
        } catch (Exception e) {
            log.info("Estimate Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getAllSchemeOfFunding() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProjectMasterDto> project = tenderRepositoryImpl.getAllSchemeOfFunding();
            if (project != null) {
                result.put("projectList", project);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Project Details");
        } catch (Exception e) {
            log.info("Estimate Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getAllWork() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderInfo> work = tenderRepositoryImpl.getAllWork();
            if (work != null) {
                result.put("workList", work);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Work Details");
        } catch (Exception e) {
            log.info("Estimate Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse viewTenderStipulationByTenderId(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            StipulationInfo tenderStipulation = tenderRepositoryImpl.viewTenderStipulationByTenderId(tenderId);
            if (tenderStipulation != null) {
                result.put("TenderStipulation", tenderStipulation);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Stipulation Details");
        } catch (Exception e) {
            log.info("Tender Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse viewTenderStipulationById(Integer stipulationId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            StipulationInfo tenderStipulation = tenderRepositoryImpl.viewTenderStipulationById(stipulationId);
            Integer nextId = tenderRepositoryImpl.viewStipulationNextId(stipulationId);
            Integer previousId = tenderRepositoryImpl.viewStipulationPreviousId(stipulationId);
            tenderStipulation.setNextId(nextId);
            tenderStipulation.setPreviousId(previousId);
            if (tenderStipulation != null) {
                result.put("TenderStipulation", tenderStipulation);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Stipulation Details");
        } catch (Exception e) {
            log.info("Tender Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public TenderStipulation updateTenderStipulation(Integer id, TenderStipulationDto tenderStipulation) {
        TenderStipulation existingTenderStipulation = tenderStipulationRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("TenderStipulation", "id", id));
        existingTenderStipulation.setSimilarWorkValue(tenderStipulation.getSimilarWorkValue());
        existingTenderStipulation.setSimilarWorkCompletion(tenderStipulation.getSimilarWorkCompletion());
        existingTenderStipulation.setAnnualFinancialTurnover(tenderStipulation.getAnnualFinancialTurnover());
        existingTenderStipulation.setPreviousYrWeightage(tenderStipulation.getPreviousYrWeightage());
        existingTenderStipulation.setCreditLinesAmount(tenderStipulation.getCreditLinesAmount());
        existingTenderStipulation.setBidCapacityTurnover(tenderStipulation.getBidCapacityTurnover());
        existingTenderStipulation.setCompletionOfWorkValueTarget(tenderStipulation.getCompletionOfWorkValueTarget());
        existingTenderStipulation.setTurnoverTarget(tenderStipulation.getTurnoverTarget());
        existingTenderStipulation.setLiquidAssetTarget(tenderStipulation.getLiquidAssetTarget());
        TenderStipulation save = tenderStipulationRepository.save(existingTenderStipulation);
        return save;
    }

    @Override
    public BidderDetails createBidderDetails(BidderDetails bidderDetails) {
        bidderDetails.setIsEmdValid(bidderDetails.getIsEmdValid());
        bidderDetails.setCompletionWorkValueQualified(false);
        bidderDetails.setLiquidAssetQualified(false);
        bidderDetails.setTurnOverQualified(false);
        bidderDetails.setIsBidQualified(false);
        return bidderDetailsRepository.save(bidderDetails);
    }

    @Override
    public Integer updateCompletionOfSimilarTypeOfWorkValidation(Integer bidderId, Boolean completionValidity) {
        BidderDetails bidder = tenderRepositoryImpl.getBidderDetailsById(bidderId);
        return tenderRepositoryImpl.updateCompletionOfSimilarTypeOfWorkValidation(bidderId, completionValidity, bidder);
    }

    @Override
    public Integer updateLiquidAssetValidation(Integer bidderId, Boolean liquidAssetValidity) {
        BidderDetails bidder = tenderRepositoryImpl.getBidderDetailsById(bidderId);
        return tenderRepositoryImpl.updateLiquidAssetValidation(bidderId, liquidAssetValidity, bidder);
    }

    @Override
    public Integer updateAnnualTurnOverValidation(Integer bidderId, Boolean annualTurnOverValidity, Double maxBidCapacity) {
        BidderDetails bidder = tenderRepositoryImpl.getBidderDetailsById(bidderId);
        return tenderRepositoryImpl.updateAnnualTurnOverValidation(bidderId, annualTurnOverValidity, bidder, maxBidCapacity);
    }

    @Override
    public LiquidAssetAvailability createLiquidAsset(LiquidAssetAvailability liquidAsset, Integer bidderId, Integer agencyId) {
        List<LiquidAssetAvailability> liquidAssetAvailabilities = tenderRepositoryImpl.getLiquidAssetAvailabilityByBidderId(bidderId);
        if (liquidAssetAvailabilities.size() == 0) {
            liquidAsset.setActive(true);
            liquidAsset.setBidderId(bidderId);
            liquidAsset.setAgencyId(agencyId);
            liquidAsset = liquidAssetRepository.save(liquidAsset);
        } else {
            liquidAsset.setBidderId(bidderId);
            liquidAsset.setAgencyId(agencyId);
            Integer update = tenderRepositoryImpl.updateLiquidAssetAvailability(liquidAsset);
            if (update == 1) {
                liquidAsset = liquidAsset;

            }

        }
        return liquidAsset;
    }

    public Boolean checkTenderIdExistsInStipulation(Integer tenderId) {
        return tenderStipulationRepository.existsByTenderId(tenderId);
    }

    public Boolean checkBidderIdExistsInFinancial(Integer bidderId) {
        return financialBidRepository.existsByBidderId(bidderId);
    }


    @Override
    public OIIPCRAResponse getTenderDetailsForStipulationByBidId(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        TenderStipulationDto tenderStipulationDto = new TenderStipulationDto();
        try {
            TenderMasterDto tenderMasterDto = tenderRepositoryImpl.getTenderNoticeDate(tenderId);
            if (!checkTenderIdExistsInStipulation(tenderId)) {
                tenderStipulationDto.setFlagId(0);
                tenderStipulationDto.setId(null);
            } else {
                TenderStipulation existingBidId = tenderStipulationRepository.findByTenderId(tenderId);
                tenderStipulationDto.setFlagId(1);
                tenderStipulationDto.setId(existingBidId.getId());
            }

            result.put("tenderMasterDto", tenderMasterDto);
            result.put("tenderStipulationDto", tenderStipulationDto);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Date Details");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getBidDetailsForComparison(Integer workId, Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            BidComparisonDto bidData = tenderRepositoryImpl.getBidDetailsForComparison(workId, tenderId);
            TenderNoticeResponse notice = tenderRepositoryImpl.getTenderNotice(workId);
            bidData.setEstimatedAmountForTender(notice.getTenderAmount());
            List<BidComparisonDto> distBlockByWorkSlNo = tenderRepositoryImpl.distBlockByWorkSlNo(workId, tenderId);
            String districtName = "";
            String blockName = "";
            String divisionName = "";
            for (int j = 0; j < distBlockByWorkSlNo.size(); j++) {
                if (distBlockByWorkSlNo.get(j).getDistrictName() != null) {
                    if (!districtName.contains(distBlockByWorkSlNo.get(j).getDistrictName())) {
                        if (j > 0) {
                            districtName += ",";
                        }

                        districtName += distBlockByWorkSlNo.get(j).getDistrictName();
                    }
                }
                if (distBlockByWorkSlNo.get(j).getBlockName() != null) {
                    if (!blockName.contains(distBlockByWorkSlNo.get(j).getBlockName())) {
                        if (j > 0) {
                            blockName += ",";
                        }

                        blockName += distBlockByWorkSlNo.get(j).getBlockName();
                    }
                }
                if (distBlockByWorkSlNo.get(j).getDivisionName() != null) {
                    if (!divisionName.contains(distBlockByWorkSlNo.get(j).getDivisionName())) {
                        if (j > 0) {
                            divisionName += ",";
                        }

                        divisionName += distBlockByWorkSlNo.get(j).getDivisionName();
                    }
                }
            }
            List<TankInfo> tankData= tenderRepositoryImpl.getTankName(workId);

            bidData.setDistrictName(districtName);
            bidData.setBlockName(blockName);
            bidData.setDivisionName(divisionName);
            result.put("bidData", bidData);
            result.put("tenderId",tenderId);
            result.put("tankData",tankData);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Details Against WorkSlNo");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getWorkSlNoByBidId(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BidComparisonDto> workSlNoList = tenderRepositoryImpl.getWorkSlNoByBidId(tenderId);


            result.put("workSlNoList", workSlNoList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("workSlNoList List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getWorkSlNoAndAgencyByBidId(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<WorkSlNoAndAgencyDto> workSlNoList = tenderRepositoryImpl.getWorkSlNoForOpening(tenderId);
            if (workSlNoList != null) {
                for (int i = 0; i < workSlNoList.size(); i++) {
                    List<FinancialBidDto> agencyList = tenderRepositoryImpl.getAgencyNameByWorkId(workSlNoList.get(i).getWorkId(), tenderId);
                    workSlNoList.get(i).setAgency(agencyList);
                    TenderNoticeResponse tenderWork = tenderRepositoryImpl.getTenderNotice(workSlNoList.get(i).getWorkId());
                    workSlNoList.get(i).setWorkName(tenderWork.getNameOfWork());
                    Integer count = tenderRepositoryImpl.getCount(workSlNoList.get(i).getWorkId(), tenderId);
                    workSlNoList.get(i).setAgencyCount(count);
                }
            }
            result.put("workSlNoAgencyList", workSlNoList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("WorkSlNoAgencyList List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }


    @Override
    public FinancialBidDetails addFinancialBidDetails(FinancialBidMasterDto financialBidMasterDto) {
        FinancialBidDetails financialBidDetails = new FinancialBidDetails();
        BidderDetails bidderDetails = new BidderDetails();

        BeanUtils.copyProperties(financialBidMasterDto, financialBidDetails);

        //check technical qualification
       List<BidderDetails> technicalQualified =tenderRepositoryImpl.cheeckTechnicalQualified(financialBidDetails.getBidderId());
       if(technicalQualified.size()>0) {
           List<FinancialBidDetails> financialValue = tenderRepositoryImpl.getFinancialValueByWorkId(
                   financialBidMasterDto.getWorkId());

           if (financialValue.size() == 0) {
               //Check if technical BID present for Bidder
               List<BidderDetails> technicalBid =tenderRepositoryImpl.cheeckTechnicalBidPresent(financialBidDetails.getTenderId(),financialBidDetails.getWorkId());

               //if yes then add 7
               //else add 1
               if (technicalBid.size() == 0) {
                   tenderRepositoryImpl.updateBidderDetails(financialBidDetails.getBidderId(), 1);
               }
               else {
                   tenderRepositoryImpl.updateBidderDetails(financialBidDetails.getBidderId(), 7);
               }
           } else {
               financialValue.add(financialBidDetails);
               financialValue.sort(Comparator.comparing(FinancialBidDetails::getAmountQuoted));
               for (int i = 0; i < financialValue.size(); i++) {
                   if (i == 0) {
                       tenderRepositoryImpl.updateBidderDetails(financialValue.get(i).getBidderId(), 2);
                   }
                   if (i == 1) {
                       tenderRepositoryImpl.updateBidderDetails(financialValue.get(i).getBidderId(), 3);
                   }
                   if (i == 2) {
                       tenderRepositoryImpl.updateBidderDetails(financialValue.get(i).getBidderId(), 4);
                   }
                   if (i == 3) {
                       tenderRepositoryImpl.updateBidderDetails(financialValue.get(i).getBidderId(), 5);
                   }
                   if (i >= 4) {
                       tenderRepositoryImpl.updateBidderDetails(financialValue.get(i).getBidderId(), 6);
                   }
               }


           }
        /*if (financialValue.size()==0){
            tenderRepositoryImpl.updateBidDetails(financialBidMasterDto.getWorkId());
        }
        else {
            List<Double> values = new ArrayList<>();
            financialValue.forEach(a -> values.add(a.getAmountQuoted()));
            values.add(financialBidMasterDto.getAmountQuoted());
        List<FinancialBidMasterDto> list=new ArrayList<>();
        list.add(financialBidMasterDto);


        }*/


           return financialBidRepository.save(financialBidDetails);
       }
       else{
           return null;
       }
    }

    @Override
    public List<FinancialBidInfo> getFinancialBidDetailsById(Integer id) {
        return tenderRepositoryImpl.getFinancialBidDetailsById(id);
    }

    @Override
    public List<FinancialBidInfo> getFinancialAbstract(Integer bidId, Integer workId, Integer bidderId) {
        return tenderRepositoryImpl.getFinancialAbstract(bidId, workId, bidderId);
    }

    @Override
    public FinancialBidDetails updateFinancialBidDetailsById(Integer id, FinancialBidMasterDto financialBidDto) {
        FinancialBidDetails existingFinancialBid = financialBidRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("FinancialBid", "id", id));
        existingFinancialBid.setAmountQuoted(financialBidDto.getAmountQuoted());
        existingFinancialBid.setAmountPercentage(financialBidDto.getAmountPercentage());
        existingFinancialBid.setAdditionalPerformanceSecRequired(financialBidDto.getAdditionalPerformanceSecRequired());
        existingFinancialBid.setAdditionalSubmitted(financialBidDto.getAdditionalSubmitted());
        existingFinancialBid.setBalanceApsRequired(financialBidDto.getBalanceApsRequired());
        existingFinancialBid.setReviewTechBidDate(financialBidDto.getReviewTechBidDate());
        existingFinancialBid.setReviewFinBidDate(financialBidDto.getReviewFinBidDate());
        existingFinancialBid.setWorkInHand(financialBidDto.getWorkInHand());
        existingFinancialBid.setBalanceBidCapacity(financialBidDto.getBalanceBidCapacity());
        FinancialBidDetails save = financialBidRepository.save(existingFinancialBid);
        return save;
    }

    @Override
    public OIIPCRAResponse getTenderNoticeByTenderId(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
           /*List< NoticeListingDto> tenderNotices = tenderRepositoryImpl.getTenderNoticeByTenderId(tenderId);
            if (tenderNotices != null) {
                result.put("TenderNotices", tenderNotices);
            }*/
            List<NoticeListingDto> tenderNotices = tenderRepositoryImpl.getTenderNoticeByTenderId(tenderId);
          /*  String distName = "";
            String blockName = "";*/
            List<NoticeListingDto> allNotice = new ArrayList<>();
            for (NoticeListingDto notice : tenderNotices) {
                String tankName = "";
                String distName = "";
                String blockName = "";
                List<NoticeLevelMappingDto> noticeListing = tenderRepositoryImpl.getTenderLevelByNoticeId(notice.getId());
                for (int j = 0; j < noticeListing.size(); j++) {
                    if (noticeListing.get(j).getDistName() != null) {
                        if (!distName.contains(noticeListing.get(j).getDistName())) {
                            if (j > 0) {
                                distName += ",";
                            }

                            distName += noticeListing.get(j).getDistName();
                        }
                    }
                    if (noticeListing.get(j).getBlockName() != null) {
                        if (!blockName.contains(noticeListing.get(j).getBlockName())) {
                            if (j > 0) {
                                blockName += ",";
                            }

                            blockName += noticeListing.get(j).getBlockName();
                        }
                    }
                    if (noticeListing.get(j).getTankName() != null) {
                        if (!tankName.contains(noticeListing.get(j).getTankName())) {
                            if (j > 0) {
                                tankName += ",";
                            }

                            tankName += noticeListing.get(j).getTankName();
                        }
                    }
                    notice.setDistName(distName);
                    notice.setBlockName(blockName);
                    notice.setTankName(tankName);
                    allNotice.add(notice);
                }
            }
            result.put("allTenderNotice", tenderNotices);
            //result.put("tenderLevel", noticeListing);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Notice Details");
        } catch (Exception e) {
            log.info("Tender Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getTenderNoticeByTenderIdListing(TenderNoticeListingDto tenderNotice) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
           /*List< NoticeListingDto> tenderNotices = tenderRepositoryImpl.getTenderNoticeByTenderId(tenderId);
            if (tenderNotices != null) {
                result.put("TenderNotices", tenderNotices);
            }*/
            Page<NoticeListingDto> tenderListPage = tenderRepositoryImpl.getTenderNoticeByTenderIdListing(tenderNotice);
            List<NoticeListingDto> tenderNoticeList = tenderListPage.getContent();

           /* String distName = "";
            String blockName = "";*/


            List<NoticeListingDto> allNotice = new ArrayList<>();
            for (NoticeListingDto notice : tenderNoticeList) {
                String tankName = "";
                String distName = "";
                String blockName = "";
                List<NoticeLevelMappingDto> noticeListing = tenderRepositoryImpl.getTenderLevelByNoticeId(notice.getId());
                for (int j = 0; j < noticeListing.size(); j++) {
                    if (noticeListing.get(j).getDistName() != null) {
                        if (!distName.contains(noticeListing.get(j).getDistName())) {
                            if (j > 0) {
                                distName += ",";
                            }

                            distName += noticeListing.get(j).getDistName();
                        }
                    }
                    if (noticeListing.get(j).getBlockName() != null) {
                        if (!blockName.contains(noticeListing.get(j).getBlockName())) {
                            if (j > 0) {
                                blockName += ",";
                            }

                            blockName += noticeListing.get(j).getBlockName();
                        }
                    }
                    if (noticeListing.get(j).getTankName() != null) {
                        if (!tankName.contains(noticeListing.get(j).getTankName())) {
                            if (j > 0) {
                                tankName += ",";
                            }

                            tankName += noticeListing.get(j).getTankName();
                        }
                    }
                    notice.setDistName(distName);
                    notice.setBlockName(blockName);
                    notice.setTankName(tankName);
                    allNotice.add(notice);
                }
            }
            result.put("allTenderNotice", tenderNoticeList);
            result.put("totalItems", tenderListPage.getTotalElements());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Notice Details");
        } catch (Exception e) {
            log.info("Tender Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0, new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public List<BidderDetailsDto> getBidderDetailsByTenderId(Integer tenderId, Integer workId) {
        return tenderRepositoryImpl.getBidderDetailsByTenderId(tenderId, workId);
    }

    @Override
    public List<AgencyMaster> getAllAgencyName(Integer tenderId, Integer workId) {
        return tenderRepositoryImpl.getAllAgencyName(tenderId, workId);
    }

    @Override
    public List<AgencyDto> getAgencyDetailsById(Integer id) {
        return tenderRepositoryImpl.getAgencyDetailsById(id);
    }

    @Override
    public Integer getBidderIdExistsOrNotByAgencyIdAndNoticeId(Integer agencyId, Integer workId) {
        return tenderRepositoryImpl.getBidderIdExistsOrNotByAgencyIdAndNoticeId(agencyId, workId);
    }

    public AgencyDto getAgencyDetailsById2(Integer id) {
        return tenderRepositoryImpl.getAgencyDetailsById2(id);
    }

    @Override
    public List<BidderCategoryDto> getAllBidderCategory() {
        return tenderRepositoryImpl.getAllBidderCategory();
    }

    @Override
    public List<EmdDepositTypeDto> getDepositType() {
        return tenderRepositoryImpl.getDepositType();
    }

    @Override
    public List<CompletionOfSimilarTypeOfWorkDto> getCompletionOfSimilarWorkTypeByBidderId(Integer bidderId) {
        List<CompletionOfSimilarTypeOfWorkDto> completionOfSimilarWorkType = new ArrayList<>();
        completionOfSimilarWorkType = tenderRepositoryImpl.getCompletionOfSimilarWorkTypeByBidderId(bidderId);
        /*if(completionOfSimilarWorkType.isEmpty()){
            Integer agencyId=tenderRepositoryImpl.getAgencyByBidderId(bidderId);
           List<Integer> bidderIdsList= tenderRepositoryImpl.getBidderIdsByAgencyId(agencyId);
           if(bidderIdsList!=null&&bidderIdsList.size()>0) {
               CompletionOfSimilarTypeOfWorkDto completionOfSimilarWorkTypeSingle = tenderRepositoryImpl.getCompletionOfSimilarWorkTypeByBidderIds(bidderIdsList);
               completionOfSimilarWorkType = tenderRepositoryImpl.getCompletionOfSimilarWorkTypeByBidderId(completionOfSimilarWorkTypeSingle.getBidderId());
           }
        }*/


        return completionOfSimilarWorkType;
    }

    @Override
    public List<AnnualFinancialTurnoverMasterDto> getAnnualFinancialTurnOver(Integer bidderId) {
        return tenderRepositoryImpl.getAnnualFinancialTurnOver(bidderId);
    }

    @Override
    public List<LiquidAssetAvailability> getLiquidAssetAvailabilityByBidderId(Integer bidderId) {
        return tenderRepositoryImpl.getLiquidAssetAvailabilityByBidderId(bidderId);
    }

    @Override
    public List<ExpenditureYearMonthDto> getExpenditureDataByTankId(ExpenditureWorkProgress expenditure) {
//        List<Integer> estimateIds=new ArrayList<>();
//        List<Integer> contractIds=new ArrayList<>();
//
//        int activityId=0;
//        if (expenditure.getTankId() != null && expenditure.getTankId() > 0) {
//            estimateIds = tenderRepositoryImpl.getEstimateIdsByTankId(expenditure.getTankId());
//            contractIds = tenderRepositoryImpl.getContractIdsByTankId(expenditure.getTankId());
//
//        }
        return tenderRepositoryImpl.getExpenditureDataByTankId(expenditure);
    }


    @Override
    public Boolean checkLicenseValid(Date date1, Integer id) {
        AgencyDto agencyDto = tenderRepositoryImpl.getAgencyDetailsById2(id);
        date1 = Date.valueOf(agencyDto.getLicenseValidity());
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new java.util.Date());
        Date date2 = Date.valueOf(currentDateTime);
        if (date1.compareTo(date2) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean getLiquidAsset(Integer agencyId, Integer bidId, Integer workId) {
        TenderNoticeDto tenderNoticeDto = tenderRepositoryImpl.getTenderNoticeByBidId(bidId);
        TenderStipulationDto tenderStipulationDto = tenderRepositoryImpl.getLiquidAssetByBidId(bidId);
        double targetLiquidAsset = tenderNoticeDto.getTenderAmount() * tenderStipulationDto.getLiquidAssetTarget();
        LiquidAssetAvailability liquidAsset = tenderRepositoryImpl.getLiquidAssetByAgencyId(agencyId);
        if (liquidAsset != null) {
            double liquid = liquidAsset.getTotalLiquidAsset();
            if (targetLiquidAsset >= liquid) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Boolean emdValid(Integer tenderId, Integer bidderId) {
        TenderNotice tenderNotice = tenderRepositoryImpl.getTenderNoticeDetailsByTenderId(tenderId);
        double emdToBeDeposited = tenderNotice.getEmdToBeDeposited();
        BidderDetails bidderDetails = tenderRepositoryImpl.getBidderDetailsByBidderId(bidderId);
        if (bidderDetails != null) {
            double emdDeposited = bidderDetails.getEmdAmount();
            if (emdDeposited >= emdToBeDeposited) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;

        }
    }


    public Boolean getFinancialTurnOverValidity(Integer agencyId, Integer tenderId, Integer workId) {
        //get total Tender Amount from Tender Master Based On BidId

        Double totalTenderAmount = tenderRepositoryImpl.getTotalTenderAmount(tenderId);
        //get  stipulation from tenderStipulation based on bidId
        StipulationInfo stipulationInfo = tenderRepositoryImpl.viewTenderStipulationByBidId(tenderId);
        //targetTurnOver=total tender amount*get tender stipulation.financialTurnover from tender stipulation
        Double targetTurnover = totalTenderAmount * stipulationInfo.getAnnualFinancialTurnover();
        // select all financial turnover against bidderId
        List<AnnualFinancialTurnoverMaster> financialTurnover = tenderRepositoryImpl.getFinancialTurnOver(agencyId);
        if (financialTurnover != null && financialTurnover.size() > 0) {
            //  select isMaximum financial turnover against bidderId
            AnnualFinancialTurnoverMaster maximumTurnover = tenderRepositoryImpl.getMaximumFinancialTurnOver(agencyId);
            if (maximumTurnover.getValue() >= targetTurnover) {
                Double maximumBidCapacity = 1.2 * maximumTurnover.getValue();
                tenderRepositoryImpl.updateMaximumBidCapacity(maximumBidCapacity, tenderId);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public Boolean getSimilarTypeOfWorkValidity(Integer bidderId, Integer bidId, Integer workId) {
        //get total Tender Amount from Tender Master Based On BidId
        Double totalTenderAmount = tenderRepositoryImpl.getTotalTenderAmount(bidId);
        //get  stipulation from tenderStipulation based on bidId
        StipulationInfo stipulationInfo = tenderRepositoryImpl.viewTenderStipulationByBidId(bidId);
        Double targetWorkExcecutedAmount = totalTenderAmount * stipulationInfo.getSimilarWorkValue();
        Double targetWorkCompletionValue = stipulationInfo.getSimilarWorkCompletion();
        List<CompletionOfSimilarTypeOfWork> completionOfSimilarWorkValue = tenderRepositoryImpl.getAllCompletedWorkByBidderId(bidderId);
        CompletionOfSimilarTypeOfWork maximumWorkCompleted = tenderRepositoryImpl.getMaximumWorkCompleted(bidderId);
        if (maximumWorkCompleted != null) {
            if (maximumWorkCompleted.getCompletedAmount() >= targetWorkExcecutedAmount && maximumWorkCompleted.getPercentageCompleted() > targetWorkCompletionValue) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public DraftTenderNoticeDto getAllTenderNotice(Integer tenderId) {
        return tenderRepositoryImpl.getAllTenderNotice(tenderId);
    }

    @Override
    public ContractInfo getContractDetailsByTenderId(Integer id, Integer workId) {
        return tenderRepositoryImpl.getContractDetailsByTenderId(id, workId);
    }

    @Override
    public List<ExpenditureDataInfo> getExpenditureDetailsBy(Integer id, Integer workId) {
        return tenderRepositoryImpl.getExpenditureDetailsBy(id, workId);
    }

    @Override
    public List<AgencyDto> getAllBidders() {
        return tenderRepositoryImpl.getAllBidders();
    }

    @Override
    public TenderResult addTenderResult(TenderResultDto tenderResult) {
        TenderResult tenderResult1 = new TenderResult();
        BeanUtils.copyProperties(tenderResult, tenderResult1);
        tenderResult1.setTenderId(tenderResult1.getTenderId());
        tenderResult1.setBidderId(tenderResult1.getBidderId());
        tenderResult1.setWorkId(tenderResult1.getWorkId());
        BidderDetails bidderDetails = tenderRepositoryImpl.getBidderDetailsById(tenderResult1.getBidderId());
        if(tenderResult.getTenderAwarded()!=null){
            bidderDetails.setIsBidAwarded(tenderResult.getTenderAwarded());
        }
        BidderDetails bidder=bidderDetailsRepository.save(bidderDetails);
        Boolean bidValid = bidderDetails.getIsBidQualified();
      /*  if(bidValid == true)
        {
            tenderResult1.setRemarks("Qualified");
        } else{
            tenderResult1.setRemarks("Not Qualified");
        }
*/
        return tenderResultRepository.save(tenderResult1);
    }

    @Override
    public TenderResult updateFinancialAbstract(TenderResultDto tenderResult, Integer id) {
        TenderResult existingResult = tenderResultRepository.findTenderResultById(id);
        if (existingResult == null) {
            throw new RecordNotFoundException("TenderResult", "id", id);
        }
        existingResult.setTenderId(tenderResult.getTenderId());
        existingResult.setWorkId(tenderResult.getWorkId());
        existingResult.setBidderId(tenderResult.getBidderId());
        existingResult.setLotteryRequired(tenderResult.getLotteryRequired());
        existingResult.setDateOfLottery(tenderResult.getDateOfLottery());
     //   existingResult.setAwardType(tenderResult.getAwardType());
        existingResult.setAcceptanceLetterNo(tenderResult.getAcceptanceLetterNo());
        existingResult.setContractId(tenderResult.getContractId());
        existingResult.setCompletionPeriod(tenderResult.getCompletionPeriod());
        existingResult.setCompletionDate(tenderResult.getCompletionDate());
        existingResult.setAgreementNo(tenderResult.getAgreementNo());
        existingResult.setLegalCase(tenderResult.getLegalCase());
        existingResult.setRemarks(tenderResult.getRemarks());
        existingResult.setIsActive(tenderResult.getIsActive());
        existingResult.setTenderNotAwardedReason(tenderResult.getTenderNotAwardedReason());
        existingResult.setReviewFinBidDate(tenderResult.getReviewFinBidDate());
        existingResult.setTenderAwarded(tenderResult.getTenderAwarded());
        return tenderResultRepository.save(existingResult);
    }

    @Override
    public List<AwardTypeDto> getAllAwardType() {
        return tenderRepositoryImpl.getAllAwardType();
    }

    @Override
    public FinancialBidDto getTenderBidderDetailsByBidId(Integer bidderId, Integer workId) {
        return tenderRepositoryImpl.getTenderBidderDetailsByBidId(bidderId, workId);
    }

    @Override
    public List<FinancialBidDto> getAllAgencyNameByWorkId(Integer workId) {
        return tenderRepositoryImpl.getAllAgencyNameByWorkId(workId);
    }

    @Override
    public BidderDetails getBidderDetailsById(Integer bidderId) {
        return tenderRepositoryImpl.getBidderDetailsById(bidderId);
    }

    @Override
    public FinancialBidInfo getFinancialBidDetailsByBidderId(Integer bidderId) {
        //  FinancialBidInfo financialBidInfo = new FinancialBidInfo();
        // BidderDetails bidderDetailsDto = tenderRepositoryImpl.getBidderDetailsByBidderId(bidderId);

        return tenderRepositoryImpl.getFinancialBidDetailsByBidderId(bidderId);
    }

    @Override
    public List<AgencyDto> getAllAgencyByContractId(Integer contractId) {
        return tenderRepositoryImpl.getAllAgencyByContractId(contractId);
    }

    @Override
    public List<Integer> getTenderIds() {
        return tenderRepositoryImpl.getTenderIds();
    }

    @Override
    public List<TenderDto> getAllBidIdForContractDone(List<Integer> tenderIds) {
        return tenderRepositoryImpl.getAllBidIdForContractDone(tenderIds);
    }

    @Override
    public List<TenderNoticeDto> getAllWorkIdForContractDone(Integer tenderId) {
        return tenderRepositoryImpl.getAllWorkIdForContractDone(tenderId);
    }

    @Override
    public OIIPCRAResponse bidIdExistsOrNot(String bidId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        if (checkBidIdExists(bidId)) {
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("BidId Already Exist");
        } else {
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("BidId DoesNot Exist");
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getDistinctPaperNameFromPublication() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        List<PaperDto> paper = tenderRepositoryImpl.getDistinctPaperNameFromPublication();
        if (paper != null) {
            response.setStatus(1);
            response.setData(paper);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All PaperName");
        } else {
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("BidId DoesNot Exist");
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getTenderByEstimateId(Integer estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        List<TenderDto> tender = new ArrayList<>();
        tender = tenderRepositoryImpl.getTenderByEstimateId(estimateId);
        if (tender != null) {
            response.setStatus(1);
            response.setData(tender);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Tender By EstimateId");
        } else {
            response.setStatus(1);
            response.setData(tender);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Tender By EstimateId");
        }
        return response;
    }

    @Override
    public boolean updateTenderApproval(Integer tenderId, ApproveStatusDto updateTenderApproval) {
        return tenderRepositoryImpl.updateTenderApproval(tenderId, updateTenderApproval);
    }


    @Override
    public Boolean changeTenderStatus(String bidId, Integer statusId) {
        return tenderRepositoryImpl.updateTenderStatus(bidId, statusId);
    }

    @Override
    public Boolean changeTenderNoticeLevelMapping(Integer tenderNoticeId) {
        return tenderRepositoryImpl.updateTenderNoticeLevelStatus(tenderNoticeId);
    }

    @Override
    public TenderResult updateTenderResult(TenderResultDto tenderResult, Integer tenderId, Integer workId, Integer bidderId) {
        TenderResult existingTenderResult = tenderResultRepository.findById(bidderId).orElseThrow(() -> new RecordNotFoundException("FinancialBid", "bidderId", bidderId));
        existingTenderResult.setLotteryRequired(tenderResult.getLotteryRequired());
        existingTenderResult.setDateOfLottery(tenderResult.getDateOfLottery());
        existingTenderResult.setAwardType(tenderResult.getAwardType());
        existingTenderResult.setAcceptanceLetterNo(tenderResult.getAcceptanceLetterNo());
        existingTenderResult.setCompletionPeriod(tenderResult.getCompletionPeriod());
        existingTenderResult.setCompletionDate(tenderResult.getCompletionDate());
        existingTenderResult.setAgreementNo(tenderResult.getAgreementNo());
        existingTenderResult.setLegalCase(tenderResult.getLegalCase());
        existingTenderResult.setRemarks(tenderResult.getRemarks());
        TenderResult save = tenderResultRepository.save(existingTenderResult);
        return save;
    }

    @Override
    public OIIPCRAResponse workIdentificationCodeExistsOrNotByBidId(Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        List<TenderNotice> existingWorkIdentificationCode = tenderRepositoryImpl.workIdetificationCodeByTenderId(tenderId);
        if (existingWorkIdentificationCode != null) {
            if (workIdentificationCodeExists(tenderId)) {
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("workIdentificationCode Already Exist");
            } else {
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("workIdentificationCode DoesNot Exist");
            }
        }
            return response;
        }

    @Override
    public List<TenderDto> getBidIdListForTender(Integer userId) {
        return tenderRepositoryImpl.getBidIdListForTender(userId);
    }




    @Override
    public List<DistrictBoundaryDto> getAllDistrictForWebsite() {
        List<DistrictBoundaryDto> districtInfo = masterQryRepository.getAllDistrict();
        return districtInfo;
    }

    @Override
    public OIIPCRAResponse deactivateTenderDocumentData(Integer draftDocumentId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Integer deactivateTenderDocumentData = tenderRepositoryImpl.deactivateTenderDocumentData(draftDocumentId);
        if (deactivateTenderDocumentData == 1) {
            response.setStatus(1);
            response.setData(draftDocumentId);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Document DeActivated Successfully");
        } else {
            response.setStatus(0);
            response.setData(draftDocumentId);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender Document DeActivation UnSuccessfully");
        }
        return response;
    }
    @Override
    public List<AgencyDto> getQualifiedAgencyByBidId(Integer tenderId,Integer workId) {
        return tenderRepositoryImpl.getQualifiedAgencyByBidId(tenderId,workId);
    }

    @Override
    public List<TenderNoticeDto> getSubDivisionOfficerNameBySubDivisionId(Integer subDivisionId) {
        return tenderRepositoryImpl.getSubDivisionOfficerNameBySubDivisionId(subDivisionId);
    }

    @Override
    public MeetingProceedingDto getMeetingProceedingsByTenderId(Integer tenderId, Integer meetingTypeId) {
        return tenderRepositoryImpl.getMeetingProceedingsByTenderId(tenderId,meetingTypeId);
    }

    @Override
    public ShlcMeetingDto getShlcMeeting(java.sql.Date date) {
        return tenderRepositoryImpl.getShlcMeeting(date);
    }

    @Override
    public List<ShlcMeetingProceedingsEntity> getShlcMeetingProceedings(Integer shlcId) {
        return tenderRepositoryImpl.getShlcMeetingProceedings(shlcId);
    }

    @Override
    public List<CommitteeMembersDto> getCommitteeMemberByMeetingProceedingId(Integer meetingProceedingId) {
        return tenderRepositoryImpl.getCommitteeMemberByMeetingProceedingId(meetingProceedingId);
    }

    @Override
    public List<CommitteeMembersDto> getCommitteeMemberByShlcMeetingId(Integer shlcId) {
        return tenderRepositoryImpl.getCommitteeMemberByShlcMeetingId(shlcId);
    }


    @Override
    public List<PreBidClarificationsDto> getPreBidClarificationByMeetingProceedingId(Integer meetingProceedingId) {
        return tenderRepositoryImpl.getPreBidClarificationByMeetingProceedingId(meetingProceedingId);
    }

    @Override
    public List<ActivityEstimateTankMappingDto> getTankNameByActivityId(Integer activityId) {
        return tenderRepositoryImpl.getTankNameByActivityId(activityId);
    }


}


















