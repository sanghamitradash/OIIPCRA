package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.exception.RecordExistException;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.*;
import com.orsac.oiipcra.service.ActivityService;
import com.orsac.oiipcra.service.ExpenditureService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ExpenditureServiceImpl implements ExpenditureService {


    @Autowired
    ExpenditureRepository expenditureRepository;

    @Autowired
    ExpenditureQueryRepo expenditureQueryRepo;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ExpenditureMappingRepository expenditureMappingRepository;

    @Autowired
    private ConfigurableEnvironment env;
    @Autowired
    private MasterQryRepository masterQryRepository;
    @Autowired
    private ActivityQryRepository activityQryRepositoryImpl;


    @Override
    public Expenditure createExpenditure(ExpenditureMasterDto expenditure) {
        Expenditure expenditure1 = new Expenditure();
        BeanUtils.copyProperties(expenditure, expenditure1);
        expenditure1.setActive(true);
        expenditure1.setFinyrId(expenditureQueryRepo.getFinYear(expenditure1.getPaymentDate()));
        expenditure1.setMonthId(expenditureQueryRepo.getFinMonth(expenditure1.getPaymentDate()));
        if (expenditure1.getAgencyId() != null && expenditure1.getAgencyId() > 0) {
            AgencyInfo agency = masterQryRepository.getAgencyById(expenditure1.getAgencyId());
            expenditure1.setAgencyName(agency.getAgencyName());
            expenditure1.setPanNo(agency.getPanNo());
        }
        if (expenditure.getContractId() != null && expenditure.getContractId() > 0) {
            ContractInfo contract = masterQryRepository.getContract(expenditure.getContractId());
            AgencyInfo agency = masterQryRepository.getAgencyById(contract.getAgencyId());
            expenditure1.setAgencyId(agency.getAgencyId());
            expenditure1.setAgencyName(agency.getAgencyName());
            expenditure1.setPanNo(agency.getPanNo());
        }

        expenditure1 = expenditureRepository.save(expenditure1);
        return expenditure1;
    }

    @Override
    public ExpenditureDto getExpenditureById(int expenditureId) {
        return expenditureQueryRepo.getExpenditureById(expenditureId);
    }

    @Override
    public List<Expenditure> getAllExpenditure() {
        return expenditureRepository.findAll();
    }


    @Override
    public Expenditure updateExpenditure(Integer id, ExpenditureDataInfo expenditure) {
        Expenditure existingExpenditure = expenditureRepository.findExpenditureById(id);
        if (existingExpenditure == null) {
            throw new RecordNotFoundException("Expenditure", "id", id);
        }
        if (expenditure.getFinyrId() != null) {
            existingExpenditure.setFinyrId(expenditure.getFinyrId());
        }
        if (expenditure.getMonthId() != null) {
            existingExpenditure.setMonthId(expenditure.getMonthId());
        }
        existingExpenditure.setValue(expenditure.getValue());
        existingExpenditure.setLevel(expenditure.getLevel());
        existingExpenditure.setDeviceId(expenditure.getDeviceId());
        existingExpenditure.setContractId(expenditure.getContractId());
        existingExpenditure.setPaymentDate(expenditure.getPaymentDate());
        // existingExpenditure.setPaymentType(expenditure.getPaymentTypeId());
        existingExpenditure.setCreatedBy(expenditure.getCreatedBy());
        existingExpenditure.setUpdatedBy(expenditure.getUpdatedBy());
        //  existingExpenditure.setInvoiceId(expenditure.getInvoiceId());
        existingExpenditure.setPaymentType(expenditure.getPaymentTypeId());
        if(expenditure.getDescription()!=null){
            existingExpenditure.setDescription(expenditure.getDescription());
        }
        if (expenditure.getAgencyId() != null && expenditure.getAgencyId() > 0) {
            AgencyInfo agency = masterQryRepository.getAgencyById(expenditure.getAgencyId());
            existingExpenditure.setAgencyId(agency.getAgencyId());
            existingExpenditure.setAgencyName(agency.getAgencyName());
            existingExpenditure.setPanNo(agency.getPanNo());
        } else if (expenditure.getContractId() != null && expenditure.getContractId() > 0) {
            ContractInfo contract = masterQryRepository.getContract(expenditure.getContractId());
            AgencyInfo agency = masterQryRepository.getAgencyById(contract.getAgencyId());
            existingExpenditure.setAgencyId(agency.getAgencyId());
            existingExpenditure.setAgencyName(agency.getAgencyName());
            existingExpenditure.setPanNo(agency.getPanNo());
        } else {
            existingExpenditure.setAgencyId(expenditure.getAgencyId());
            existingExpenditure.setAgencyName(expenditure.getAgencyName());
            existingExpenditure.setPanNo(expenditure.getPanNo());
        }
        if (existingExpenditure.getType() == 5) {
            if (expenditure.getEstimateId() != null && expenditure.getEstimateId() > 0) {
                existingExpenditure.setEstimateId(expenditure.getEstimateId());
            }
        }
        return expenditureRepository.save(existingExpenditure);
    }

    @Override
    public InvoiceInfo getInvoiceById(int invoiceId) {
        return expenditureQueryRepo.getInvoiceById(invoiceId);
    }

    @Override
    public InvoiceInfo getInvoiceId(int inVoiceId) {
        return expenditureQueryRepo.getInvoiceId(inVoiceId);
    }

    @Override
    public List<Invoice> getAllInvoice() {
        return invoiceRepository.findAll();
    }

    @Override
    public Double getExpenditureByInvId(Integer invoiceId) {
        double expInfo = 0.0;
        expInfo = expenditureQueryRepo.getExpenditureValue(invoiceId);
        return expInfo;
    }

    public Double getInvoiceAmount(Integer invoiceId) {
        double invInfo = 0.0;
        invInfo = expenditureQueryRepo.getInvoiceAmount(invoiceId);
        return invInfo;
    }


    @Override
    public Page<ExpenditureInfo> getExpenditureList(ExpenditureListDto expenditureListDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        int parentId = 0;
        List<Integer> terminalList = new ArrayList<>();
        List<Integer> contractIds = new ArrayList<>();
        List<Integer> tankIds = new ArrayList<>();
        List<Integer> districtIds = new ArrayList<>();
        List<Integer> divisionIds = new ArrayList<>();
        List<Integer> estimateIds = new ArrayList<>();
        List<Integer> expenditureIds = new ArrayList<>();


        try {
            if (expenditureListDto != null) {
                if (expenditureListDto.getExpenditureType() != null) {
                    switch (expenditureListDto.getExpenditureType()) {
                        case 1:
                        case 4:
                            //Contingency
                            //Activity Wise Search
                            if (expenditureListDto.getSubActivityId() != null && expenditureListDto.getSubActivityId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getSubActivityId());
                                terminalList.add(expenditureListDto.getSubActivityId());
                            } else if (expenditureListDto.getActivityId() != null && expenditureListDto.getActivityId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getActivityId());
                                terminalList.add(expenditureListDto.getActivityId());
                            } else if (expenditureListDto.getSubComponentId() != null && expenditureListDto.getSubComponentId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getSubComponentId());
                                terminalList.add(expenditureListDto.getSubComponentId());
                            } else if (expenditureListDto.getComponentId() != null && expenditureListDto.getComponentId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getComponentId());
                                terminalList.add(expenditureListDto.getComponentId());
                            }
                            break;

                        case 2:
                            //Project Wise
                            if (expenditureListDto.getTankId() != null && expenditureListDto.getTankId() > 0) {
                                tankIds.add(expenditureListDto.getTankId());
                            } else if (expenditureListDto.getDistId() != null && expenditureListDto.getDistId() > 0) {
                                districtIds.add(expenditureListDto.getDistId());
                            } else if (expenditureListDto.getDivisionId() != null && expenditureListDto.getDivisionId() > 0) {
                                divisionIds.add(expenditureListDto.getDivisionId());
                            }
                            break;

                        case 3:
                            //Estimate Wise
                            if (expenditureListDto.getEstimateId() != null && expenditureListDto.getEstimateId() == 0 && expenditureListDto.getSubActivityId() != null && expenditureListDto.getSubActivityId() == 0 &&
                                    expenditureListDto.getActivityId() != null && expenditureListDto.getActivityId() == 0 &&
                                    expenditureListDto.getSubComponentId() != null && expenditureListDto.getSubComponentId() == 0 &&
                                    expenditureListDto.getComponentId() != null && expenditureListDto.getComponentId() == 0) {
                                expenditureIds = expenditureQueryRepo.getExpenditureIds();
                            } else if (expenditureListDto.getEstimateId() != null && expenditureListDto.getEstimateId() > 0) {
                                estimateIds.add(expenditureListDto.getEstimateId());
                            } else if (expenditureListDto.getSubActivityId() != null && expenditureListDto.getSubActivityId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getSubActivityId());
                                terminalList.add(expenditureListDto.getSubActivityId());
                            } else if (expenditureListDto.getActivityId() != null && expenditureListDto.getActivityId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getActivityId());
                                terminalList.add(expenditureListDto.getActivityId());
                            } else if (expenditureListDto.getSubComponentId() != null && expenditureListDto.getSubComponentId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getSubComponentId());
                                terminalList.add(expenditureListDto.getSubComponentId());
                            } else if (expenditureListDto.getComponentId() != null && expenditureListDto.getComponentId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getComponentId());
                                terminalList.add(expenditureListDto.getComponentId());
                            }
                            break;

                        case 5:
                            //Contract
                            if (expenditureListDto.getContractId() != null && expenditureListDto.getContractId() > 0) {
                                contractIds.add(expenditureListDto.getContractId());
                            } else if (expenditureListDto.getSubActivityId() != null && expenditureListDto.getSubActivityId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getSubActivityId());
                                terminalList.add(expenditureListDto.getSubActivityId());
                            } else if (expenditureListDto.getActivityId() != null && expenditureListDto.getActivityId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getActivityId());
                                terminalList.add(expenditureListDto.getActivityId());
                            } else if (expenditureListDto.getSubComponentId() != null && expenditureListDto.getSubComponentId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getSubComponentId());
                                terminalList.add(expenditureListDto.getSubComponentId());
                            } else if (expenditureListDto.getComponentId() != null && expenditureListDto.getComponentId() > 0) {
                                terminalList = activityService.getTerminalIds(expenditureListDto.getComponentId());
                                terminalList.add(expenditureListDto.getComponentId());
                            }
                            break;
                    }
                }
                if (expenditureListDto.getWorkType() != null) {
                    switch (expenditureListDto.getWorkType()) {
                        case 1:
                            contractIds.addAll(activityQryRepositoryImpl.getContractIdsByWorkType(expenditureListDto.getWorkType()));
                            estimateIds.addAll(activityQryRepositoryImpl.getEstimateIdsByWorkType(expenditureListDto.getWorkType()));
                            break;
                        case 2:
                            contractIds.addAll(activityQryRepositoryImpl.getContractIdsByWorkType(expenditureListDto.getWorkType()));
                            estimateIds.addAll(activityQryRepositoryImpl.getEstimateIdsByWorkType(expenditureListDto.getWorkType()));
                            break;
                        default:
                            break;
                    }
                }
                Page<ExpenditureInfo> expenditureListPage = expenditureQueryRepo.getExpenditureList(
                        expenditureListDto,
                        expenditureListDto.getUserId(),
                        terminalList,
                        districtIds,
                        divisionIds,
                        estimateIds,
                        tankIds,
                        contractIds,
                        expenditureIds,
                        expenditureListDto.getExpenditureType());

                return expenditureListPage;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<InvoiceItem> createInvoiceItem(List<InvoiceItem> invoiceItemList, Integer id) {
        for (InvoiceItem invoiceItem : invoiceItemList) {
            invoiceItem.setInvoiceId(id);
            invoiceItem.setActive(true);
        }
        return invoiceItemRepository.saveAll(invoiceItemList);
    }

    @Override
    public Boolean deactivateInvoiceItem(Integer id) {
        return expenditureQueryRepo.deactivateInvoiceItem(id);
    }

    @Override
    public List<InvoiceItemDto> getInvoiceItemByInvoiceId(Integer invoiceId) {
        return expenditureQueryRepo.getInvoiceItemByInvoiceId(invoiceId);
    }

    @Override
    public List<PaymentTypeDto> getAllPaymentType() {
        return expenditureQueryRepo.getAllPaymentType();
    }

    @Override
    public List<ExpenditureMappingDto> getExpenditureMappingByExpId(Integer expenditureId) {

        return expenditureQueryRepo.getExpenditureMappingByExpId(expenditureId);
    }


    @Override
    public Invoice saveInvoice(InvoiceDto invoice) {
        Double expenditureValue = null;
        Invoice invoice1 = new Invoice();
        BeanUtils.copyProperties(invoice, invoice1);
        invoice1.setStatus(4);
        // Expenditure expenditure = expenditureQueryRepo.getExpenditureById(invoice.getId());
      /*   expenditureValue = expenditureQueryRepo.getExpeditureValue(invoice1.getId());
         if (invoice1.getStatus() == 4) {
             if (invoice1.getInvoiceAmount() == expenditureValue) {
                 invoice1.setStatus(2);
             } else if (invoice1.getInvoiceAmount() > expenditureValue) {
                 invoice1.setStatus(2);
             }
         }*/
             /*Invoice existingInvoice = invoiceRepository.findInvoiceById(invoice1.getId());
             existingInvoice.setStatus(invoice.getStatus());*/

        invoice1 = invoiceRepository.save(invoice1);
        return invoice1;
    }


    @Override
    public Invoice updateInvoiceById(Integer invoiceId, InvoiceDto invoice) {
        Invoice existingInvoice = invoiceRepository.findInvoiceById(invoiceId);
        if (existingInvoice == null) {
            throw new RecordNotFoundException("Invoice", "id", invoiceId);
        }
        existingInvoice.setInvoiceNo(invoice.getInvoiceNo());
        existingInvoice.setInvoiceDate(invoice.getInvoiceDate());
        existingInvoice.setInvoiceAmount(invoice.getInvoiceAmount());
        existingInvoice.setStatus(invoice.getStatus());
        existingInvoice.setInvoiceDocument(invoice.getInvoiceDocument());
        return invoiceRepository.save(existingInvoice);
    }

    @Override
    public InvoiceItem createInvoiceItem(InvoiceItem invoiceItem) {

        return invoiceItemRepository.save(invoiceItem);
    }

    @Override
    public List<InvoiceItemDto> getInvoiceItemById(Integer id) {
        return expenditureQueryRepo.getInvoiceItemById(id);
    }

    @Override
    public List<InvoiceStatus> getAllInvoiceStatus() {
        return expenditureQueryRepo.getAllInvoiceStatus();
    }

    @Override
    public List<WorkTypeDto> getAllWorkType() {
        return expenditureQueryRepo.getAllWorkType();
    }

    @Override
    public List<TenderDto> getBidId() {
        return expenditureQueryRepo.getBidId();
    }

    @Override
    public List<TenderNoticeDto> getWorkId() {
        return expenditureQueryRepo.getWorkId();
    }

    @Override
    public ExpenditureDto getExpenditureByContractId(Integer contractId) {
        return expenditureQueryRepo.getExpenditureByContractId(contractId);
    }

    @Override
    public List<ExpenditureMapping> saveExpenditureMapping(List<ExpenditureMapping> expenditureMapping, Integer id, Expenditure expenditure, Integer estimateId) {
        for (ExpenditureMapping expenditureMapping1 : expenditureMapping) {
            expenditureMapping1.setExpenditureId(id);
            if (expenditure.getType() == 5) {
                if (estimateId != null && estimateId > 0) {
                    expenditureMapping1.setEstimateId(estimateId);
                }
            }
            expenditureMapping1.setActive(true);
            expenditureMapping1.setCreatedBy(expenditure.getCreatedBy());
        }
        return expenditureMappingRepository.saveAll(expenditureMapping);
    }

    public Boolean existsByInvoiceId(Integer invoiceId) {
        return expenditureMappingRepository.existsByInvoiceId(invoiceId);
    }

    @Override
    public Boolean deactivateExpenditureMapping(Integer id) {
        return expenditureQueryRepo.deactivateExpenditureMapping(id);
    }


    @Override
    public InvoiceItem updateInvoiceItem(Integer id, InvoiceItem invoiceItem) {
        InvoiceItem existingInvoiceItem = invoiceItemRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("InvoiceItem", "id", id));
        existingInvoiceItem.setItemSerialNo(invoiceItem.getItemSerialNo());
        existingInvoiceItem.setDescription(invoiceItem.getDescription());
        existingInvoiceItem.setQuantity(invoiceItem.getQuantity());
        existingInvoiceItem.setRate(invoiceItem.getRate());
        return invoiceItemRepository.save(existingInvoiceItem);
    }

    @Override
    public List<TenderInfo> getBidIdByProjectId(Integer tankId) {
        try {
            List<TenderInfo> tender = expenditureQueryRepo.getBidIdByProjectId(tankId);
            /*if (tender != null) {
                ContractDto contract = expenditureQueryRepo.getContractNoByTenderId(tender.getId());
                if(contract!=null) {
                    tender.setContractId(contract.getId());
                    tender.setContractNumber(contract.getContractNumber());
                }
            }*/
            return tender;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ContractDto> getContractNoByTenderId(Integer tenderId) {
        List<ContractDto> contract = expenditureQueryRepo.getContractNoByTenderId(tenderId);
        return contract;
    }


    public List<ExpenditureInfo> getExpenditureDataByWorkTypeId(ExpenditureListDto expenditureListDto) {
        List<ExpenditureInfo> expenditureData = expenditureQueryRepo.getExpenditureDataByWorkTypeId(expenditureListDto);
        return expenditureData;
    }





}

