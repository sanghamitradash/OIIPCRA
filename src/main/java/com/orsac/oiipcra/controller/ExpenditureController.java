package com.orsac.oiipcra.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.exception.RecordExistException;
import com.orsac.oiipcra.repository.ActivityQryRepository;
import com.orsac.oiipcra.repository.ExpenditureMappingRepository;
import com.orsac.oiipcra.repository.ExpenditureQueryRepo;
import com.orsac.oiipcra.repository.SurveyRepository;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.ActivityService;
import com.orsac.oiipcra.service.ExpenditureService;
import com.orsac.oiipcra.serviceImpl.ActivityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import com.orsac.oiipcra.service.SurveyService;
import io.swagger.models.auth.In;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/expenditure")
public class ExpenditureController {


    @Autowired
    ExpenditureService expenditureService;

    @Autowired
    private ExpenditureMappingRepository expenditureMappingRepository;

    @Autowired
    private ActivityQryRepository activityQryRepository;

    @Autowired
    private ExpenditureQueryRepo expenditureQueryRepo;


    @Autowired
    ActivityService activityService;
    @Autowired
    private ActivityServiceImpl activityServiceImpl;


    @Autowired
    SurveyRepository surveyRepositoy;


    @Value("${accessDocumentPath}")
    private String accessDocumentPath;
    @Autowired
    private AWSS3StorageService awss3StorageService;


    // Create Expenditure
    @PostMapping("/createExpenditure")
    public OIIPCRAResponse createExpenditure(@RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ExpenditureMasterDto expenditureData = objectMapper.readValue(data, ExpenditureMasterDto.class);
            //Check if data has come for invoice

            if (checkInvoiceIdExists(expenditureData.getExpenditureMapping().get(0).getInvoiceId()) &&
                    expenditureData.getExpenditureMapping().get(0).getInvoiceId() > 0) {
                throw new RecordExistException("Expenditure", "InvoiceId", expenditureData.getExpenditureMapping().get(0).getInvoiceId());
            }

            if (expenditureData.getExpenditureMapping() != null && expenditureData.getExpenditureMapping().size() > 0
                    && expenditureData.getExpenditureMapping().get(0).getInvoiceId() != null && expenditureData.getExpenditureMapping().get(0).getInvoiceId() > 0) {
                double expenditureTotal = expenditureData.getValue();
                expenditureTotal = expenditureService.getExpenditureByInvId(expenditureData.getExpenditureMapping().get(0).getInvoiceId());
                double invoiceTotal = expenditureService.getInvoiceId(expenditureData.getExpenditureMapping().get(0).getInvoiceId()).getInvoiceAmount();
                if (invoiceTotal > (expenditureTotal + expenditureData.getValue())) {
                    expenditureData.setPaymentType(1);//Partially paid
                } else if (invoiceTotal == (expenditureTotal + expenditureData.getValue())) {
                    expenditureData.setPaymentType(2);//Fully paid
                }
            }
            Integer estimateId=0;
              if(expenditureData.getType()==5){
                  if(expenditureData.getContractId()!=null && expenditureData.getContractId()>0 ) {
                      estimateId = expenditureQueryRepo.getEstimateByContractId(expenditureData.getContractId());
                  }
                  expenditureData.setEstimateId(estimateId);
              }
            Expenditure expenditure1 = expenditureService.createExpenditure(expenditureData);

          /*  if(expenditure1.getType()==5){
                if(expenditure1.getContractId()!=null && expenditure1.getContractId()>0 ) {
                     estimateId = expenditureQueryRepo.getEstimateByContractId(expenditure1.getContractId());
                }
            }*/

            List<ExpenditureMapping> expenditureMapping = expenditureService.saveExpenditureMapping(expenditureData.getExpenditureMapping(),
                    expenditure1.getId(), expenditure1,estimateId);

            if (expenditure1 != null) {
                result.put("expenditure1", expenditure1);
                result.put("expenditureMapping", expenditureMapping);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage(" Expenditure Created Successfully");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    public Boolean checkInvoiceIdExists(Integer invoiceId) {
        return expenditureMappingRepository.existsByInvoiceId(invoiceId);
    }

    //Get Expenditure ById
    @PostMapping("/getExpenditureById")
    public OIIPCRAResponse getExpenditureById(@RequestParam(name = "expenditureId", required = false) Integer expenditureId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
             ExpenditureDto expenditureList = expenditureService.getExpenditureById(expenditureId);
            List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(expenditureList.getActivityId());

            String code = "";
            for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                code += item.getCode();
                if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                    code = code + ".";
                }
            }
            expenditureList.setCode(code);


            List<ExpenditureMappingDto> mapping = expenditureService.getExpenditureMappingByExpId(expenditureId);
          /*  for(int i=0;i<mapping.size();i++) {
                if (mapping.get(i).getDivisionId() != null && mapping.get(i).getDivisionId() > 0) {
                    Integer distId = expenditureQueryRepo.getDistrictByDivisionId(mapping.get(i).getDivisionId());
                    mapping.get(i).setDistrictId(distId);
                }
            }*/
           /*ActivityEstimateResponseDto allEstimate = activityService.getActivityEstimateByID(expenditureList.getEstimateId());
           int flagId = 0;
           List<TankInfo> tankList = surveyRepositoy.getTankDetailsById(expenditureList.getTankId(),flagId);
           ActivityInformationDto activityInfo = activityService.getActivityInformation(expenditureList.getActivityId());*/
            result.put("ExpenditureList", expenditureList);
            result.put("mapping", mapping);
            result.put("upperHierarchy",currentHierarchyInfoById);
               /*result.put("allEstimate",allEstimate);
               result.put("tankList",tankList);
               result.put("activityInfo", activityInfo);*/
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Expenditure By Id");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    // Get AllExpenditure
    @GetMapping("/getAllExpenditure")
    public OIIPCRAResponse getAllExpenditure() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Expenditure> expenditureList = expenditureService.getAllExpenditure();
            result.put("expenditureList", expenditureList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Expenditure");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getExpenditureByContractId")
    public OIIPCRAResponse getExpenditureByContractId(@RequestParam(name = "contractId", required = false) Integer contractId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ExpenditureDto expenditureList = expenditureService.getExpenditureByContractId(contractId);
            result.put("ExpenditureList", expenditureList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Expenditure By ContractId");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    // Update Expenditure ById
    @PostMapping("/updateExpenditure")
    public OIIPCRAResponse updateExpenditure(@RequestParam Integer id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ExpenditureDataInfo expenditureData = mapper.readValue(data, ExpenditureDataInfo.class);
            Integer estimateId=0;
            if(expenditureData.getType()==5){
                if(expenditureData.getContractId()!=null && expenditureData.getContractId()>0 ) {
                    estimateId = expenditureQueryRepo.getEstimateByContractId(expenditureData.getContractId());
                }
                expenditureData.setEstimateId(estimateId);
            }
            Expenditure expenditure1 = expenditureService.updateExpenditure(id, expenditureData);
            if(expenditureData.getExpenditureMapping()!=null && expenditureData.getExpenditureMapping().size()>0) {
                expenditureService.deactivateExpenditureMapping(expenditure1.getId());
                List<ExpenditureMapping> expenditureMappingInfo = expenditureService.saveExpenditureMapping(expenditureData.getExpenditureMapping(), expenditure1.getId(), expenditure1,estimateId);
                result.put("expenditureMappingInfo", expenditureMappingInfo);
            }
            result.put("expenditure1", expenditure1);

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Expenditure Updated successfully.");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    // Expenditure  List
   /* @PostMapping("/getExpenditureList")
    public OIIPCRAResponse getExpenditureList(@RequestBody ExpenditureListDto expenditureListDto){
        return expenditureService.getExpenditureList(expenditureListDto);
    }*/

    @PostMapping("/getExpenditureList")
    public OIIPCRAResponse getExpenditureList(@RequestBody ExpenditureListDto expenditureListDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<ExpenditureInfo> expenditureListPage = expenditureService.getExpenditureList(expenditureListDto);
            List<ExpenditureInfo> expenditureList = expenditureListPage.getContent();
            for (ExpenditureInfo item1 : expenditureList) {
                if(item1.getActivityId()!=null && item1.getActivityId()>0) {
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getActivityId());
                    String code = "";
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                        if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item1.setCode(code);
                }
            }
            result.put("expenditureList", expenditureList);
            result.put("currentPage", expenditureListPage.getNumber());
            result.put("totalItems", expenditureListPage.getTotalElements());
            result.put("totalPages", expenditureListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("ExpenditureList");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    // create invoice
    @PostMapping("/createInvoice")
    public OIIPCRAResponse saveInvoice(@RequestParam(name = "data") String data,
                                       @RequestParam(name = "doc", required = false) MultipartFile[] files) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            InvoiceDto invoice = objectMapper.readValue(data, InvoiceDto.class);
            String filename = null;
            if (files != null) {
                for (MultipartFile multipart : files) {
                    //invoice.setInvoiceDocument(multipart.getOriginalFilename());
                    String name = multipart.getOriginalFilename().split("\\.")[1];
                    String name2 = multipart.getOriginalFilename().split("\\.")[0];
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
                    String formattedDate = sdf.format(date);
                    filename = name2 + formattedDate + "." + name;
                    invoice.setInvoiceDocument(filename);
                }
            }
            //  invoice.setDocumentPath("https://ofarisbucket.s3.ap-south-1.amazonaws.com/InvoiceDocument");
            Invoice invoiceMObj = expenditureService.saveInvoice(invoice);
            List<InvoiceItem> invoiceItem1 = expenditureService.createInvoiceItem(invoice.getInvoiceItemList(), invoiceMObj.getId());

            if (files != null && files.length > 0 && invoiceMObj.getInvoiceDocument() != null) {
                for (MultipartFile mult : files) {
                    boolean saveDocument = awss3StorageService.uploadDocumentInvoice(mult, String.valueOf(invoiceMObj.getId()), filename);
                }
            }
            result.put("Invoice", invoiceMObj);
            result.put("invoiceItem1", invoiceItem1);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Invoice Created");

        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //Get Invoice ById
    @PostMapping("/getInvoiceById")
    public OIIPCRAResponse getInvoiceById(@RequestParam Integer invoiceId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            String documentPath = accessDocumentPath + invoiceId + "/";
            InvoiceInfo invoice = expenditureService.getInvoiceById(invoiceId);
            List<InvoiceItemDto> invoiceItem = expenditureService.getInvoiceItemByInvoiceId(invoiceId);
            if (invoice != null) {
                //  invoice.setInvoiceDocument(documentPath +"" +invoice.getInvoiceDocument());
                result.put("Invoice", invoice);
                result.put("invoiceItem", invoiceItem);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Invoice");
            } else {
                result.put("Invoice", invoice);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @GetMapping("/getAllInvoice")
    public OIIPCRAResponse getAllInvoice() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Invoice> invoiceList = expenditureService.getAllInvoice();
            result.put("invoiceList", invoiceList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Invoice");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @GetMapping("/getAllInvoiceStatus")
    public OIIPCRAResponse getAllInvoiceStatus() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<InvoiceStatus> invoiceStatus = expenditureService.getAllInvoiceStatus();
            result.put("invoiceStatus", invoiceStatus);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Invoice Status");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    // Update Invoice ById
    @PostMapping("/updateInvoice")
    public OIIPCRAResponse updateInvoice(@RequestParam int id, @RequestParam(name = "data") String data,
                                         @RequestParam(name = "doc", required = false) MultipartFile[] files) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            InvoiceDto invoice = mapper.readValue(data, InvoiceDto.class);
            String filename = null;
            if (files != null) {
                for (MultipartFile multipart : files) {
                    String name = multipart.getOriginalFilename().split("\\.")[1];
                    String name2 = multipart.getOriginalFilename().split("\\.")[0];
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
                    String formattedDate = sdf.format(date);
                    filename = name2 + formattedDate + "." + name;
                    invoice.setInvoiceDocument(filename);
                }
            }
            Invoice invoiceObj = expenditureService.updateInvoiceById(id, invoice);
            expenditureService.deactivateInvoiceItem(invoiceObj.getId());
            List<InvoiceItem> invoiceItem1 = expenditureService.createInvoiceItem(invoice.getInvoiceItemList(), invoiceObj.getId());

            if (files != null && files.length > 0 && invoiceObj.getInvoiceDocument() != null) {
                for (MultipartFile mult : files) {
                    boolean saveDocument = awss3StorageService.uploadDocumentInvoice(mult, String.valueOf(invoiceObj.getId()), filename);
                }
            }
            result.put("Invoice", invoiceObj);
            result.put("invoiceItem1", invoiceItem1);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Invoice Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    //Create InvoiceItem
    @PostMapping("/createInvoiceItem")
    public OIIPCRAResponse createInvoiceItem(@RequestBody InvoiceItem invoiceItem) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            InvoiceItem invoiceItem1 = expenditureService.createInvoiceItem(invoiceItem);
            result.put("invoiceItem1", invoiceItem1);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" InvoiceItem Created Successfully");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getInvoiceItemById")
    public OIIPCRAResponse getInvoiceItemById(@RequestParam Integer invoiceId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            List<InvoiceItemDto> invoiceItems = expenditureService.getInvoiceItemById(invoiceId);
            if (invoiceItems != null) {
                result.put("InvoiceItem", invoiceItems);
                result.put("InvoiceItem", invoiceItems);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All invoiceItems");
            } else {
                result.put("InvoiceItem", invoiceItems);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //update InvoiceItem
    @PostMapping("/updateInvoiceItem/{id} ")
    public OIIPCRAResponse updateInvoiceItem(@PathVariable Integer id, @RequestBody InvoiceItem invoiceItem) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            InvoiceItem invoiceItem1 = expenditureService.updateInvoiceItem(id, invoiceItem);
            result.put("invoiceItem1", invoiceItem1);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("InvoiceItem Updated successfully.");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //get All workType
    @PostMapping("/getAllWorkType")
    public OIIPCRAResponse getAllWorkType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<WorkTypeDto> workType = expenditureService.getAllWorkType();
            result.put("workType", workType);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Work Type");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    //Get Bid Id
    @PostMapping("/getBidId")
    public OIIPCRAResponse getBidId() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderDto> bidId = expenditureService.getBidId();
            result.put("bidId", bidId);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Bid Id List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getWorkId")
    public OIIPCRAResponse getWorkId() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderNoticeDto> workId = expenditureService.getWorkId();
            result.put("workId", workId);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" Work Id List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllPaymentType")
    public OIIPCRAResponse getAllPaymentType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<PaymentTypeDto> paymentType = expenditureService.getAllPaymentType();
            result.put("paymentType", paymentType);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Invoice PaymentType");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getAllSubActivity")
    public OIIPCRAResponse getAllSubActivity(@RequestParam Integer userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
             List<Activity> subActivity=activityServiceImpl.getAllSubActivity(userId);
            Boolean i = true;
            for(Activity item1 : subActivity){
//
                i = false;
                if(item1.getMasterHeadId() != 0){
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getId());
                    String code = "";
                    for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
                        code += item.getCode();
                        if(!code.isEmpty() && item.getMasterHeadId() <= 1){
                            code = code+".";
                        }
                    }
                    item1.setCode(code);
                }

            }
            result.put("subActivity", subActivity);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All SubActivity");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getBidIdAndContractNoByProjectId")
    public OIIPCRAResponse getBidIdAndContractNoByProjectId(@RequestParam Integer tankId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ContractDto> contract=new ArrayList<>();
            List<TenderInfo> tenderData = expenditureService.getBidIdByProjectId(tankId);
            /*if(tenderData!=null) {
                 contract = expenditureService.getContractNoByTenderId(tenderData.getId());
            }*/
            result.put("tenderData", tenderData);
            //result.put("contract",contract);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender By Project ");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getContractNoByBidId")
    public OIIPCRAResponse getContractNoByBidId(@RequestParam Integer bidId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractDto> contract=new ArrayList<>();
            contract = expenditureService.getContractNoByTenderId(bidId);


            result.put("contract",contract);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Contract By Tender ");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getExpenditureDataByWorkTypeId")
    public OIIPCRAResponse getExpenditureDataByWorkTypeId(@RequestBody ExpenditureListDto expenditureListDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            List<ExpenditureInfo> expenditureData = expenditureService.getExpenditureDataByWorkTypeId(expenditureListDto);

            result.put("expenditureData", expenditureData);
            result.put("currentPage", 0);
            result.put("totalItems", expenditureData.size());
            result.put("totalPages", 0);

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Expenditure Data");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getUpperHierarchyInfoById")
    public OIIPCRAResponse getUpperHierarchyInfoById(@RequestParam Integer activityId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityUpperHierarchyInfo> currentHierarchyInfoById1 = activityQryRepository.getUpperHierarchyInfoById(activityId);
            for (ActivityUpperHierarchyInfo item1 : currentHierarchyInfoById1) {
                if (item1.getParentId()!= 0) {
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getId());
                    String code = "";
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                        if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item1.setCode(code);
                }

            }

            result.put("upperHierarchy", currentHierarchyInfoById1);

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tender By Project ");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }





}
