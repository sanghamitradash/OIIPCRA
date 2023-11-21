package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.Expenditure;
import com.orsac.oiipcra.entities.ExpenditureMapping;
import com.orsac.oiipcra.entities.Invoice;
import com.orsac.oiipcra.entities.InvoiceItem;
import org.springframework.data.domain.Page;


import java.util.List;

public interface ExpenditureService {
    Expenditure createExpenditure(ExpenditureMasterDto expenditure);

    ExpenditureDto getExpenditureById(int expenditureId);

    List<Expenditure> getAllExpenditure();

    Expenditure updateExpenditure(Integer id, ExpenditureDataInfo expenditure);

    InvoiceInfo getInvoiceById(int invoiceId);

    InvoiceInfo getInvoiceId(int inVoiceId);
    List<Invoice> getAllInvoice();

    Boolean existsByInvoiceId(Integer InvoiceId);

    Invoice saveInvoice(InvoiceDto invoice);

    Invoice updateInvoiceById(Integer invoiceId, InvoiceDto invoice);

    InvoiceItem updateInvoiceItem(Integer id, InvoiceItem invoiceItem);

    InvoiceItem createInvoiceItem(InvoiceItem invoiceItem);

    List<InvoiceItemDto> getInvoiceItemById(Integer invoiceId);

    List<InvoiceStatus> getAllInvoiceStatus();

    List<WorkTypeDto> getAllWorkType();

    List<TenderDto> getBidId();

    List<TenderNoticeDto> getWorkId();

    ExpenditureDto getExpenditureByContractId(Integer contractId);

    List<ExpenditureMapping> saveExpenditureMapping(List<ExpenditureMapping> expenditureMapping, Integer id,Expenditure expenditure1,Integer estimateId);

    Boolean deactivateExpenditureMapping(Integer id);

    Page<ExpenditureInfo> getExpenditureList(ExpenditureListDto expenditureListDto);
    List<ExpenditureInfo> getExpenditureDataByWorkTypeId(ExpenditureListDto expenditureListDto);



    Double getExpenditureByInvId(Integer invoiceId);

    List<InvoiceItem> createInvoiceItem(List<InvoiceItem> invoiceItemList, Integer id);

    Boolean deactivateInvoiceItem(Integer id);

   List< InvoiceItemDto >getInvoiceItemByInvoiceId(Integer invoiceId);

    List<PaymentTypeDto> getAllPaymentType();

    List<ExpenditureMappingDto> getExpenditureMappingByExpId(Integer expenditureId);

    Double getInvoiceAmount(Integer invoiceId);
    List<TenderInfo> getBidIdByProjectId(Integer tankId);

    List<ContractDto> getContractNoByTenderId(Integer tenderId);
}
