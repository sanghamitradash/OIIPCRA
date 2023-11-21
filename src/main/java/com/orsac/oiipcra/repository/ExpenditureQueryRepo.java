package com.orsac.oiipcra.repository;


import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class ExpenditureQueryRepo {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private UserQueryRepository userQryRepo;

    public int count(String qryStr, MapSqlParameterSource sqlParam) {
        String sqlStr = "SELECT COUNT(*) from (" + qryStr + ") as t";
        Integer intRes = namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
        if (null != intRes) {
            return intRes;
        }
        return 0;
    }


    public ExpenditureDto getExpenditureById(int expenditureId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "SELECT ed.id,ed.finyr_id,fy.name as finYear, ed.month_id,month.name as monthName, ed.value, ed.device_id, ed.payment_date,\n" +
                "extract(month from payment_date) as paymentMonth,extract(year from payment_date) as paymentYear,ed.level as level, \n" +
                "ed.payment_type,payment.name as paymentTypeName ,mhd.id as activityId,mhd.name as activityName,ed.type as type,division.mi_division_name as divisionName," +
                "invoice.invoice_no as invoiceNumber,ed.agency_id as agencyId,ed.agency_name as agencyName,ed.pan_no as panNo,contract.contract_name as contractName,ed.description as description " +
                "from oiipcra_oltp.expenditure_data as ed   \n" +
                "left join oiipcra_oltp.expenditure_mapping  as emp on emp.expenditure_id = ed.id  \n" +
                "left join oiipcra_oltp.invoice_payment_type as payment on ed.payment_type = payment.id   \n" +
                "left join oiipcra_oltp.invoice_m as invoice on invoice.id= emp.invoice_id   \n" +
                "left join oiipcra_oltp.master_head_details as mhd on mhd.id=emp.activity_id\n" +
                //"left join oiipcra_oltp.agency_m as agency on agency.id = invoice.agency_id    \n" +
                "left join oiipcra_oltp.contract_m as contract on contract.id = emp.contract_id  \n" +
                "left join oiipcra_oltp.fin_year_m as fy on fy.id = ed.finyr_id \n" +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=emp.division_id\n" +
                "left join oiipcra_oltp.month_m as month on month.id= ed.month_id\n" +
                "WHERE ed.id=:id   and emp.is_active=true ";

        sqlParam.addValue("id", expenditureId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ExpenditureDto.class));
    }


    public int getFinYear(Date paymentDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = formatter.format(paymentDate);
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "SELECT id from oiipcra_oltp.fin_year_m where \'" + strDate + "\' between start_date and end_date";

//        sqlParam.addValue("dateVal", strDate);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }


    public int getFinMonth(Date paymentDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM");
        String strDate = formatter.format(paymentDate);
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "SELECT id from oiipcra_oltp.month_m where name = :strDate";

        sqlParam.addValue("strDate", strDate);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

   /* public Page<ExpenditureInfo> getExpenditureList(ExpenditureListDto expenditureListDto, List<Integer> terminalList){
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(expenditureListDto.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("id");
        userListRequest.setSize(14);

        PageRequest pageable = PageRequest.of(expenditureListDto.getPage(), expenditureListDto.getSize(), Sort.Direction.fromString(expenditureListDto.getSortOrder()), expenditureListDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        List<Integer> userIdList = new ArrayList<>();
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        for (int i = 0; i < userList.getSize(); i++) {
            userIdList.add(userList.getContent().get(i).getId());
        }

        UserInfoDto userInfoById = userQryRepo.getUserById(expenditureListDto.getUserId());

        queryString += "select ed.id,tn.type_of_work as workId,tm.bid_id as bidId,invoice.invoice_no, invoice.invoice_date,  " +
                "cm.contract_number, agency.name as agencyName, ed.value,ed.payment_date, ip.name as paymentType  " +
                "from oiipcra_oltp.expenditure_data as ed  " +
                "left join oiipcra_oltp.invoice_m as invoice on invoice.id = ed.invoice_id  " +
                "left join oiipcra_oltp.contract_m as cm on cm.id = ed.contract_id   " +
                "left join oiipcra_oltp.invoice_payment_type as ip on ip.id= ed.payment_type  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id = cm.agency_id   " +
                "left join oiipcra_oltp.invoice_status as invoiceStatus on invoice.status= invoiceStatus.id  " +
                "left join oiipcra_oltp.contract_mapping as mapping on cm.id = mapping.contract_id  " +
                "left join oiipcra_oltp.tender_m as tm on cm.tender_id = tm.id   " +
                "left join oiipcra_oltp.tender_notice as tn on tm.id = tn.tender_id  " +
                "left join oiipcra_oltp.user_m as um on um.agency_id = agency.id   " +
                "where ed.is_active=true";

        if (expenditureListDto.getContractId() > 0) {
            queryString += " AND tn.id=:workTypeId ";
            sqlParam.addValue("workTypeId", expenditureListDto.getContractId());
        }
        if (expenditureListDto.getActivityId() > 0) {
            queryString += " AND agency.agency.id=:agencyId ";
            sqlParam.addValue("agencyId", expenditureListDto.getActivityId());
        }

        if (expenditureListDto.getEstimateId() > 0) {
            queryString += " AND tm.bid_id=:bidId ";
            sqlParam.addValue("bidId", expenditureListDto.getEstimateId());
        }


        if (expenditureListDto.getInvoiceId() > 0) {
            queryString += " AND invoice.status=:invoiceStatusId ";
            sqlParam.addValue("invoiceStatusId", expenditureListDto.getInvoiceId());
        }


       *//* SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (expenditureListDto.getUploadFromDate() != null && !expenditureListDto.getUploadFromDate().isEmpty()) {
            queryString += " AND date(ed.created_on) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(expenditureListDto.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (expenditureListDto.getUploadToDate() != null && !expenditureListDto.getUploadToDate().isEmpty()) {
            queryString += " AND date(ed.created_on) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(expenditureListDto.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }
*//*
        if (expenditureListDto.getBlockId() > 0) {
            queryString += "  AND mapping.block_id=:blockId ";
            sqlParam.addValue("blockId", expenditureListDto.getBlockId());
        }
        if (expenditureListDto.getDistId() > 0) {
            queryString += " AND mapping.dist_id=:distId ";
            sqlParam.addValue("distId", expenditureListDto.getDistId());
        }

        if (expenditureListDto.getTankId() > 0) {
            queryString += " AND mapping.tank_id=:tankId ";
            sqlParam.addValue("tankId", expenditureListDto.getTankId());
        }

        if (expenditureListDto.getDivisionId() > 0) {
            queryString += " AND mapping.division_id=:divisionId ";
            sqlParam.addValue("divisionId", expenditureListDto.getDivisionId());
        }

        if (expenditureListDto.getSubdivisionId() > 0) {
            queryString += " AND mapping.sub_division_id=:subDivisionId ";
            sqlParam.addValue("subDivisionId", expenditureListDto.getDivisionId());
        }

        if (expenditureListDto.getSectionId() > 0) {
            queryString += " AND mapping.section_id=:sectionId ";
            sqlParam.addValue("sectionId", expenditureListDto.getSectionId());
        }

        if(terminalList.size() > 0){
            queryString += " AND ed.activity_id IN(:terminalLIst)";
            sqlParam.addValue("terminalLIst", terminalList);
        }

        if(activityList.size() > 0){
            queryString += " AND ed.activity_id IN(:terminalLIst)";
            sqlParam.addValue("terminalLIst", terminalList);
        }


       *//* if(userInfoById.getSurveyor())
        {
            queryString +=" AND ed.created_by IN (:userId)";
            sqlParam.addValue("userId", expenditureListDto.getUserId());
        }else
        {
            queryString += " AND ed.created_by IN (:userIdList)";
            sqlParam.addValue("userIdList", userIdList);
        }*//*

        queryString += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        return new PageImpl<>(namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(ExpenditureInfo.class)));

    }*/


    public InvoiceInfo getInvoiceById(int invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "Select invoice.id,invoice_no,invoice_date,invoice_document,invoiceStatus.name as invoiceStatus,    " +
//                "tm.bid_id as bidId, tn.id as workId,agency.name as agencyName,cm.id as contractId, cm.contract_number,   " +
//                "tank.id as tankId, tank.project_id, tank.name_of_the_m_i_p as nameOfTheMip, invoice.invoice_amount,   " +
//                "invoice.gst, (invoice.invoice_amount*invoice.gst/100)as gstAmount,    " +
//                "invoice.invoice_amount+(invoice.invoice_amount*invoice.gst/100) as totalAmount , " +
//                "tm.id as tenderId, ed.payment_type as paymentTypeId, type.name as paymentType, ed.payment_date,    " +
//                "ed.value as amountPaid, ed.finyr_id, year.name as yearName, ed.month_id, month.name as monthName   " +
//                "from oiipcra_oltp.invoice_m as invoice    " +
//                "left join oiipcra_oltp.invoice_status as invoiceStatus on invoice.status = invoiceStatus.id   " +
//                "left join oiipcra_oltp.expenditure_mapping as map on map.invoice_id = invoice.id   " +
//                "left join oiipcra_oltp.expenditure_data as ed on ed.id = map.expenditure_id  " +
//                "left join oiipcra_oltp.fin_year_m as year on year.id = ed.finyr_id    " +
//                "left join oiipcra_oltp.month_m as month on month.id = ed.month_id      " +
//                "left join oiipcra_oltp.invoice_payment_type as type on type.id = ed.payment_type   " +
//                "left join oiipcra_oltp.contract_m as cm on invoice.contract_id = cm.id   " +
//                "left join oiipcra_oltp.contract_mapping as cmp on cmp.contract_id = cm.id   " +
//                "left join oiipcra_oltp.tender_notice as tn on cmp.tender_notice_id=tn.id     " +
//                "left join oiipcra_oltp.tender_m as tm on  tm.id = tn.tender_id     " +
//                "left join oiipcra_oltp.agency_m as agency on invoice.agency_id = agency.id    " +
//                "left join oiipcra_oltp.tank_m_id  as tank on tank.id =cmp.tank_id   " ;

        String qry = "Select  invoice.id,invoice_no,invoice_date,invoice_document,invoiceStatus.name as invoiceStatus,     " +
                "tm.bid_id as bidId,tn.id as workId,tn.work_identification_code as workIdentificationCode,agency.name as agencyName,cm.id as contractId, cm.contract_number,    " +
                "tank.id as tankId, tank.project_id, tank.name_of_the_m_i_p as nameOfTheMip,   " +
                "invoice.invoice_amount,invoice.gst, (invoice.invoice_amount*invoice.gst/100)as gstAmount,    " +
                "invoice.invoice_amount+(invoice.invoice_amount*invoice.gst/100) as totalAmount,  " +
                "ed.payment_type as paymentTypeId, type.name as paymentType, ed.payment_date,   " +
                "ed.value as amountPaid, ed.finyr_id, year.name as yearName, ed.month_id,  " +
                "month.name as monthName    " +
                "from oiipcra_oltp.invoice_m as invoice  " +
                "left join oiipcra_oltp.invoice_status as invoiceStatus on invoiceStatus.id = invoice.status  " +
                "left join oiipcra_oltp.expenditure_mapping as mapping on mapping.invoice_id = invoice.id  " +
                "left join oiipcra_oltp.expenditure_data as ed on ed.id = mapping.expenditure_id  " +
                "left join oiipcra_oltp.invoice_payment_type as type on type.id=ed.payment_type  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id =ed.finyr_id  " +
                "left join oiipcra_oltp.month_m as month on month.id = ed.month_id  " +
                "left join oiipcra_oltp.tender_m as tm on tm.id = mapping.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id = mapping.tender_notice_id  " +
                "left join oiipcra_oltp.contract_m as cm on cm.id = invoice.contract_id  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id = mapping.tank_id  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id = cm.agency_id  ";


        if (invoiceId > 0) {
            qry += " WHERE invoice.id=:invoiceId ";
            sqlParam.addValue("invoiceId", invoiceId);
        }
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(InvoiceInfo.class));
    }


    public Double getExpenditureValue(int invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetInvoiceAmount = "select sum(ed.value) from oiipcra_oltp.expenditure_data as ed   " +
                "left join oiipcra_oltp.expenditure_mapping as map on map.expenditure_id = ed.id  " +
                "where map.invoice_id =:invoiceId";

        sqlParam.addValue("invoiceId", invoiceId);
        Double info = namedJdbc.queryForObject(GetInvoiceAmount, sqlParam, Double.class);
        if (info == null) {
            info = 0.0;
        }
        return info;
    }


    public Double getInvoiceAmount(int invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetInvoiceAmount = "SELECT id,invoice_amount from oiipcra_oltp.invoice_m " +
                "where id=:invoiceId";

        sqlParam.addValue("invoiceId", invoiceId);
        return namedJdbc.queryForObject(GetInvoiceAmount, sqlParam, Double.class);
    }


    public List<InvoiceItemDto> getInvoiceItemById(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select im.item_serial_no,im.description,im.quantity,im.rate,(im.quantity*im.rate)as total " +
                "from oiipcra_oltp.invoice_item as im " +
                "left join oiipcra_oltp.invoice_m_new as invoice on invoice.id = im.invoice_id ";

        if (id > 0) {
            qry += " WHERE invoice.id=:invoiceId ";
            sqlParam.addValue("invoiceId", id);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(InvoiceItemDto.class));
    }

    public List<InvoiceStatus> getAllInvoiceStatus() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select id, name from oiipcra_oltp.invoice_status";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(InvoiceStatus.class));
    }

    public List<WorkTypeDto> getAllWorkType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select work.id, work.name from oiipcra_oltp.work_type_m as work  ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WorkTypeDto.class));
    }

    public List<TenderDto> getBidId() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select id, bid_id from oiipcra_oltp.tender_m where is_active=true ORDER BY created_on desc  ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderDto.class));
    }

    public List<TenderNoticeDto> getWorkId() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select tender.id, tender.name_of_work from oiipcra_oltp.tender_notice as tender ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeDto.class));
    }

    public ExpenditureDto getExpenditureByContractId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT ed.id,emp.contract_id,contract.contract_name as contractName, emp.activity_id, ed.finyr_id,fy.name as finYear, ed.month_id,month.name as monthName, ed.value, ed.device_id, ed.payment_date,  " +
                "extract(month from payment_date) as paymentMonth,extract(year from payment_date) as paymentYear,  " +
                "ed.invoice_id,invoice.invoice_no,agency.id as agencyId, agency.name as agencyName, ed.payment_type,payment.name as paymentTypeName, emp.estimate_id, emp.tank_id  " +
                "from oiipcra_oltp.expenditure_data as ed   " +
                "left join oiipcra_oltp.expenditure_mapping  as emp on emp.expenditure_id = ed.id   " +
                "left join oiipcra_oltp.invoice_payment_type as payment on ed.payment_type = payment.id  " +
                "left join oiipcra_oltp.invoice_m as invoice on invoice.id= ed.invoice_id  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id = invoice.agency_id  " +
                "left join oiipcra_oltp.contract_m as contract on contract.id = emp.contract_id   " +
                "left join oiipcra_oltp.fin_year_m as fy on fy.id = ed.finyr_id   " +
                "left join oiipcra_oltp.month_m as month on month.id= ed.month_id  " +
                "WHERE emp.contract_id=:contractId";
        sqlParam.addValue("contractId", contractId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ExpenditureDto.class));
    }

    public Boolean deactivateExpenditureMapping(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE expenditure_mapping SET is_active =false  WHERE expenditure_id=:id";
        sqlParam.addValue("id", id);
        int update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }

    public List<EstimateDto> getEstimateDetailsByEstimateId(Integer estimateId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select em.id, em.activity_id, em.level_id,lm.level_name, em.status_id,status.name as activityStatus,em.work_type, em.approval_order,em.name_of_work,em.technical_sanction_no,em.project_id,\n" +
                "em.procurement_type,em.district_zone_identification,em.nol_of_tor_by_wb,em.approval_ref,em.correspondance_file_no,em.period_of_completion,\n" +
                "em.start_date,em.end_date,em.estimated_amount,em.approved_status,em.approval_date,em.document_name,em.document_path,em.estimate_type,type.name as estimateType,project.name as projectName\n" +
                "from oiipcra_oltp.activity_estimate_mapping as em\n" +
                "left join oiipcra_oltp.activity_level_master as lm on lm.id = em.level_id\n" +
                "left join oiipcra_oltp.activity_status as status on status.id = em.status_id\n" +
                "left join oiipcra_oltp.project_m as project on project.id = em.project_id\n" +
                "left join oiipcra_oltp.estimate_type as type on type.id = em.estimate_type\n" +
                "left join oiipcra_oltp.expenditure_mapping as ed on ed.estimate_id = em.id";
        return namedJdbc.query(qry, new BeanPropertyRowMapper<>(EstimateDto.class));
    }


    public List<Integer> getEstimateIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT estimate_id from oiipcra_oltp.activity_estimate_tank_mapping where tank_id=:tankId and is_active=true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getActivityIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select activity_id from oiipcra_oltp.activity_estimate_mapping as estimate " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as tank on estimate.id=tank.estimate_id " +
                "where tank.tank_id=:tankId and tank.is_active=true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getContractIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT contract_id from oiipcra_oltp.contract_mapping as contract  " +
                "where tank_id=:tankId and is_active = true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByContractId(int contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT tank_id FROM oiipcra_oltp.contract_mapping WHERE contract_id =:contractId";
        sqlParam.addValue("contractId", contractId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByEstimateId(int estimateId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT tank_id FROM oiipcra_oltp.activity_estimate_tank_mapping WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId", estimateId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTenderNoticeIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select tender_notice_id from oiipcra_oltp.work_project_mapping where tank_id=:tankId and is_active=true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTenderIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select tender_id from oiipcra_oltp.work_project_mapping where tank_id=:tankId and is_active=true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public Page<ExpenditureInfo> getExpenditureList(ExpenditureListDto expenditureListDto,
                                                    Integer userId,
                                                    List<Integer> terminalList,
                                                    List<Integer> districtIds,
                                                    List<Integer> divisionIds,
                                                    List<Integer> estimateIds,
                                                    List<Integer> tankIds,
                                                    List<Integer> contractIds,
                                                    List<Integer> expenditureIds,
                                                    Integer expType) {

        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(expenditureListDto.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("payment_date");
        userListRequest.setSize(14);

        PageRequest pageable = PageRequest.of(expenditureListDto.getPage(), expenditureListDto.getSize(), Sort.Direction.fromString(expenditureListDto.getSortOrder()), expenditureListDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "payment_date");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        Integer resultCount = 0;
        String queryString = " ";

        List<Integer> userIdList = new ArrayList<>();

        userIdList.add(expenditureListDto.getUserId());

        userIdList = userQryRepo.getSubUsers(expenditureListDto.getUserId());


        UserInfoDto userInfoById = userQryRepo.getUserById(expenditureListDto.getUserId());
        //all
        if(expenditureListDto.getWorkType()==null || expenditureListDto.getWorkType()==0) {
    queryString += "select ed.id,tn.work_identification_code as workId,tm.bid_id as bidId,cm.id as contractId,invoice.invoice_no, " +
            "invoice.invoice_date,  " +
            "cm.contract_number, ed.agency_name as agencyName,mhd.id as activityId,mhd.name activityName," +
            "round(cm.contract_amount+(cm.contract_amount*(cm.gst/100)),2) as contractAmount, ed.value,ed.payment_date, ip.name as paymentType   " +
            "from oiipcra_oltp.expenditure_data as ed    " +
            "left join oiipcra_oltp.expenditure_mapping as epm on epm.expenditure_id = ed.id   " +
            "left join oiipcra_oltp.invoice_m as invoice on invoice.id = epm.invoice_id  " +
            "left join oiipcra_oltp.contract_m as cm on cm.id = epm.contract_id   " +
            "left join oiipcra_oltp.invoice_payment_type as ip on ip.id= ed.payment_type    " +
            //"left join oiipcra_oltp.agency_m as agency on agency.id = cm.agency_id    " +
            "left join oiipcra_oltp.invoice_status as invoiceStatus on invoice.status= invoiceStatus.id   " +
            "left join oiipcra_oltp.tender_m as tm on epm.tender_id = tm.id   " +
            " left join oiipcra_oltp.master_head_details as mhd on mhd.id=epm.activity_id " +
            "left join oiipcra_oltp.tender_notice as tn on epm.tender_notice_id= tn.id   " +
            "left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=epm.estimate_id " +
            //"left join oiipcra_oltp.user_m as um on um.agency_id = agency.id     " +
            "where ed.is_active=true and epm.is_active=true ";

    if (terminalList != null && !terminalList.isEmpty()) {
        queryString += " AND epm.activity_id IN (:activityIds)";
        sqlParam.addValue("activityIds", terminalList);
    }
    if (expenditureIds != null && !expenditureIds.isEmpty()) {
        queryString += " AND ed.id IN (:expenditureIds)";
        sqlParam.addValue("expenditureIds", expenditureIds);
    }

    if (divisionIds != null && !divisionIds.isEmpty()) {
        queryString += " AND epm.division_id IN (:tankIdsByEstimateId)";
        sqlParam.addValue("tankIdsByEstimateId", divisionIds);
    }
    if (estimateIds != null && !estimateIds.isEmpty()) {
        queryString += " AND epm.estimate_id IN (:estimateIds)";
        sqlParam.addValue("estimateIds", estimateIds);
    }
    if (tankIds != null && !tankIds.isEmpty()) {
        queryString += " AND epm.tank_id IN (:tenderNoticeIds)";
        sqlParam.addValue("tenderNoticeIds", tankIds);
    }
    if (contractIds != null && !contractIds.isEmpty()) {
        queryString += " AND epm.contract_id IN (:contractIds)";
        sqlParam.addValue("contractIds", contractIds);
    }

    if (expenditureListDto.getInvoiceId() != null && expenditureListDto.getInvoiceId() > 0) {
        queryString += " AND epm.invoice_id IN (:invoiceId)";
        sqlParam.addValue("invoiceId", expenditureListDto.getInvoiceId());
    }
    if (expenditureListDto.getLevelId() != null && expenditureListDto.getLevelId() > 0) {
        queryString += " AND ed.level=:level";
        sqlParam.addValue("level", expenditureListDto.getLevelId());
    }
    if (expType != null && expType > 0) {
        if (expType != 3) {
            queryString += " AND ed.type IN (:type)";
            sqlParam.addValue("type", expType);
        } else {
            queryString += " ";
        }
    }
    if (expenditureListDto.getExpenditureType() != null && expenditureListDto.getExpenditureType() == 5 && expenditureListDto.getWorkType() != null && expenditureListDto.getWorkType() > 0) {
        queryString += " AND cm.work_type_id=:workTypeId";
        sqlParam.addValue("workTypeId", expenditureListDto.getWorkType());
    }
    if (expenditureListDto.getExpenditureType() != null && expenditureListDto.getExpenditureType() == 3 && expenditureListDto.getWorkType() != null && expenditureListDto.getWorkType() > 0) {
        queryString += " AND estimate.work_type=:workTypeId";
        sqlParam.addValue("workTypeId", expenditureListDto.getWorkType());
    }
}
        //for work
        if(expenditureListDto.getWorkType()!=null && expenditureListDto.getWorkType()==1){
    queryString += " select epm.tender_id as tenderId,epm.tender_notice_id as tenderNoticeId,tn.work_identification_code as workId, tm.bid_id as bidId,cm.id as contractId,cm.contract_number,\n" +
            " round(cm.contract_amount+(cm.contract_amount*(cm.gst/100)),2) as contractAmount, sum(ed.value) as value\n" +
            " from oiipcra_oltp.expenditure_data as ed    \n" +
            " left join oiipcra_oltp.expenditure_mapping as epm on epm.expenditure_id = ed.id   \n" +
            " left join oiipcra_oltp.invoice_m as invoice on invoice.id = epm.invoice_id  \n" +
            " left join oiipcra_oltp.contract_m as cm on cm.id = epm.contract_id   \n" +
            " left join oiipcra_oltp.invoice_payment_type as ip on ip.id= ed.payment_type    \n" +
            " left join oiipcra_oltp.invoice_status as invoiceStatus on invoice.status= invoiceStatus.id  \n" +
            " left join oiipcra_oltp.tender_m as tm on epm.tender_id = tm.id    \n" +
            " left join oiipcra_oltp.master_head_details as mhd on mhd.id=epm.activity_id \n" +
            " left join oiipcra_oltp.tender_notice as tn on epm.tender_notice_id= tn.id   \n" +
            " left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=epm.estimate_id \n" +
            " where ed.is_active=true and epm.is_active=true and ed.type in(3,2,5)  ";
    if(expenditureListDto.getExpenditureType()!=null && expenditureListDto.getExpenditureType()==2 && expenditureListDto.getTankId()!=null && expenditureListDto.getTankId()>0){
        queryString += " and tn.id in(select tender_notice_id from oiipcra_oltp.tender_notice_level_mapping where tank_id=:tankId) ";
        sqlParam.addValue("tankId", expenditureListDto.getTankId());
    }else {
        if (contractIds != null && expenditureListDto.getContractId() != null && expenditureListDto.getContractId() == 0) {
            queryString += " and (ed.contract_id in(:contractIds) ";
            sqlParam.addValue("contractIds", contractIds);
        } else {
            queryString += " and ed.contract_id=:contractIds ";
            sqlParam.addValue("contractIds", expenditureListDto.getContractId());
        }
        if (expenditureListDto.getContractId() == 0) {
            if (estimateIds != null || expenditureListDto.getEstimateId() != null && expenditureListDto.getEstimateId() == 0) {
                queryString += " or epm.estimate_id in(:estimateIds) ) ";
                sqlParam.addValue("estimateIds", estimateIds);
            }
            else{
                queryString += "  ) ";
            }
        }
    }
    /*else{
        queryString +=" and epm.estimate_id=:estimateIds ";
        sqlParam.addValue("estimateIds", expenditureListDto.getContractId());
    }*/
    if (terminalList != null && !terminalList.isEmpty()) {
        queryString += " AND epm.activity_id IN (:activityIds)";
        sqlParam.addValue("activityIds", terminalList);
    }

}
        //for consultancy
        if(expenditureListDto.getWorkType()!=null && expenditureListDto.getWorkType()==2){
            queryString += "  select cm.id as contractId,  cm.contract_number, ed.agency_name as agencyName,mhd.id as activityId,mhd.name activityName,\n" +
                    " round(cm.contract_amount+(cm.contract_amount*(cm.gst/100)),2) as contractAmount, sum(ed.value) as value  \n" +
                    " from oiipcra_oltp.expenditure_data as ed    \n" +
                    " left join oiipcra_oltp.expenditure_mapping as epm on epm.expenditure_id = ed.id   \n" +
                    " left join oiipcra_oltp.invoice_m as invoice on invoice.id = epm.invoice_id  \n" +
                    " left join oiipcra_oltp.contract_m as cm on cm.id = epm.contract_id   \n" +
                    " left join oiipcra_oltp.invoice_payment_type as ip on ip.id= ed.payment_type    \n" +
                    " left join oiipcra_oltp.invoice_status as invoiceStatus on invoice.status= invoiceStatus.id  \n" +
                    " left join oiipcra_oltp.tender_m as tm on epm.tender_id = tm.id    \n" +
                    " left join oiipcra_oltp.master_head_details as mhd on mhd.id=epm.activity_id \n" +
                    " left join oiipcra_oltp.tender_notice as tn on epm.tender_notice_id= tn.id   \n" +
                    " left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=epm.estimate_id \n" +
                    " where ed.is_active=true and epm.is_active=true and ed.type in (3,2,5) ";
            if(contractIds!=null && expenditureListDto.getContractId()!=null && expenditureListDto.getContractId()==0 ){
                queryString +=" and ed.contract_id in(:contractIds) ";
                sqlParam.addValue("contractIds", contractIds);
            }
            else{
                queryString +=" and ed.contract_id=:contractIds ";
                sqlParam.addValue("contractIds",expenditureListDto.getContractId());
            }
            if (terminalList != null && !terminalList.isEmpty()) {
                queryString += " AND epm.activity_id IN (:activityIds)";
                sqlParam.addValue("activityIds", terminalList);
            }
            if(estimateIds!=null  && expenditureListDto.getEstimateId()!=null && expenditureListDto.getEstimateId()==0){
                queryString +=" or epm.estimate_id in(:estimateIds) ";
                sqlParam.addValue("estimateIds",estimateIds);
            }
            else{
                queryString +=" and epm.estimate_id=:estimateIds ";
                sqlParam.addValue("estimateIds", expenditureListDto.getContractId());
            }


        }
        //for contingency and activity
        if(expenditureListDto.getWorkType()!=null && expenditureListDto.getWorkType()==3){
            queryString += " select mhd.id as activityId,mhd.name activityName,\n" +
                    " sum(ed.value) as value\n" +
                    " from oiipcra_oltp.expenditure_data as ed    \n" +
                    " left join oiipcra_oltp.expenditure_mapping as epm on epm.expenditure_id = ed.id   \n" +
                    " left join oiipcra_oltp.invoice_m as invoice on invoice.id = epm.invoice_id  \n" +
                    " left join oiipcra_oltp.contract_m as cm on cm.id = epm.contract_id   \n" +
                    " left join oiipcra_oltp.invoice_payment_type as ip on ip.id= ed.payment_type    \n" +
                    " left join oiipcra_oltp.invoice_status as invoiceStatus on invoice.status= invoiceStatus.id  \n" +
                    " left join oiipcra_oltp.tender_m as tm on epm.tender_id = tm.id    \n" +
                    " left join oiipcra_oltp.master_head_details as mhd on mhd.id=epm.activity_id \n" +
                    " left join oiipcra_oltp.tender_notice as tn on epm.tender_notice_id= tn.id   \n" +
                    " left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=epm.estimate_id \n" +
                    " where ed.is_active=true and epm.is_active=true and ed.type in (1,4) ";

            if (terminalList != null && !terminalList.isEmpty()) {
                queryString += " AND epm.activity_id IN (:activityIds)";
                sqlParam.addValue("activityIds", terminalList);
            }
            if (expenditureListDto.getSubActivityId() != null && expenditureListDto.getSubActivityId()>0) {
                queryString += " AND epm.activity_id=:activityId";
                sqlParam.addValue("activityId", expenditureListDto.getSubActivityId());
            }

        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (expenditureListDto.getUploadFromDate() != null && !expenditureListDto.getUploadFromDate().isEmpty()) {
            queryString += " AND date(ed.payment_date) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(expenditureListDto.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (expenditureListDto.getUploadToDate() != null && !expenditureListDto.getUploadToDate().isEmpty()) {
            queryString += " AND date(ed.payment_date) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(expenditureListDto.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }

        if (userInfoById.getRoleId() > 4) {
            if (userInfoById.getSurveyor()) {
                queryString += " AND ed.created_by IN (:userId)";
                sqlParam.addValue("userId", expenditureListDto.getUserId());
            } else {
                queryString += " AND ed.created_by IN (:userIdList)";
                sqlParam.addValue("userIdList", userIdList);
            }
        }
        if(expenditureListDto.getWorkType()!=null && expenditureListDto.getWorkType()>0) {
            if (expenditureListDto.getWorkType() == 1) {
                queryString += " group by cm.id,tn.work_identification_code,tm.bid_id,invoice.invoice_no,invoice.invoice_date, epm.tender_id,epm.tender_notice_id ";
            }
            if (expenditureListDto.getWorkType() == 2) {
                queryString += " group by cm.id,mhd.id,ed.agency_name ";
            }
            if (expenditureListDto.getWorkType() == 3) {
                queryString +=" group by mhd.id";
            }
        }

        if(expenditureListDto.getWorkType()==null || expenditureListDto.getWorkType()==0) {
            queryString += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        }
        resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ExpenditureInfo> expenditureInfos = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(ExpenditureInfo.class));
        return new PageImpl<>(expenditureInfos, pageable, resultCount);

    }


    public Boolean deactivateInvoiceItem(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE invoice_item SET is_active = CASE WHEN is_active = false THEN true WHEN is_active = true THEN false END WHERE id=:id";
        sqlParam.addValue("id", id);
        int update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }

    public List<InvoiceItemDto> getInvoiceItemByInvoiceId(Integer invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select im.item_serial_no,im.description,im.quantity,im.rate,(im.quantity*im.rate)as total " +
                "from oiipcra_oltp.invoice_item as im " +
                "left join oiipcra_oltp.invoice_m as invoice on invoice.id = im.invoice_id ";

        if (invoiceId > 0) {
            qry += " WHERE invoice.id=:invoiceId ";
            sqlParam.addValue("invoiceId", invoiceId);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(InvoiceItemDto.class));
    }

    public List<PaymentTypeDto> getAllPaymentType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllPaymentType = "Select id,name from oiipcra_oltp.invoice_payment_type ";
        return namedJdbc.query(GetAllPaymentType, sqlParam, new BeanPropertyRowMapper<>(PaymentTypeDto.class));
    }

    public List<ExpenditureMappingDto> getExpenditureMappingByExpId(Integer expenditureId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetExpenditureMappingByExpId = "SELECT emp.expenditure_id,emp.contract_id,contract.contract_name as contractName, emp.activity_id, \n" +
                "emp.invoice_id,invoice.invoice_no,agency.id as agencyId, agency.name as agencyName, \n" +
                "emp.estimate_id, emp.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id as projectId, emp.tender_id,\n" +
                "tender.bid_id,emp.tender_notice_id, notice.name_of_work,notice.work_identification_code,\n" +
                "notice.work_sl_no_in_tcn  ,division.mi_division_name as divisionName,emp.division_id as divisionId ," +
                "emp.district_id as districtId,emp.block_id as blockId,district.district_name as districtName,block.block_name as blockName\n" +
                "from oiipcra_oltp.expenditure_mapping as emp \n" +
                "left join oiipcra_oltp.invoice_m as invoice on invoice.id= emp.invoice_id \n" +
                "left join oiipcra_oltp.contract_m as contract on contract.id = emp.contract_id  \n" +
                "left join oiipcra_oltp.agency_m as agency on agency.id = contract.agency_id  \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id = emp.tender_id \n" +
                "left join oiipcra_oltp.tender_notice as notice on notice.id = emp.tender_notice_id \n" +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=emp.division_id\n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=emp.district_id\n" +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=emp.block_id\n" +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id = emp.tank_id where emp.is_active=true   ";

        if (expenditureId > 0) {
            GetExpenditureMappingByExpId += " AND expenditure_id=:expenditureId ";
            sqlParam.addValue("expenditureId", expenditureId);
        }
        return namedJdbc.query(GetExpenditureMappingByExpId, sqlParam, new BeanPropertyRowMapper<>(ExpenditureMappingDto.class));
    }

    public ExpenditureDto getExpenditureValueByInvoiceId(Integer invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT ed.id, ed.value from oiipcra_oltp.expenditure_data as ed" +
                " left join oiipcra_oltp.expenditure_mapping as map on map.expenditure_id = ed.id" +
                " left join oiipcra_oltp.invoice_m as im on im.id = map.invoice_id" +
                " WHERE map.invoice_id=:invoiceId";
        sqlParam.addValue("invoiceId", invoiceId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ExpenditureDto.class));
    }

    public List<Integer> getContractIdsByExpenditureId(Integer expenditureId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetContractIdsByExpenditureId = "SELECT DISTINCT em.contract_id from oiipcra_oltp.expenditure_mapping as em " +
                "WHERE em.expenditure_id=:expenditureId";
        sqlParam.addValue("expenditureId", expenditureId);
        return namedJdbc.queryForList(GetContractIdsByExpenditureId, sqlParam, Integer.class);
    }

    public List<Integer> getContractIdsByInvoiceId(Integer invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetContractIdsByInvoiceId = "SELECT DISTINCT im.contract_id from oiipcra_oltp.invoice_m as im  " +
                "WHERE im.id =:invoiceId";
        sqlParam.addValue("invoiceId", invoiceId);
        return namedJdbc.queryForList(GetContractIdsByInvoiceId, sqlParam, Integer.class);
    }

    public InvoiceInfo getInvoiceId(int inVoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetInvoiceId = "SELECT im.id, im.invoice_no,im.invoice_amount,im.invoice_date,im.invoice_document from oiipcra_oltp.invoice_m as im " +
                "WHERE im.id =:inVoiceId ";
        sqlParam.addValue("inVoiceId", inVoiceId);
        return namedJdbc.queryForObject(GetInvoiceId, sqlParam, new BeanPropertyRowMapper<>(InvoiceInfo.class));
    }
    public Integer getDistrictByDivisionId(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String sqlStr = "select dist_id from oiipcra_oltp.mi_division_m where mi_division_id=:divisionId";

        sqlParam.addValue("divisionId", divisionId);
         return namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
    }
    public List<Integer> getExpenditureIds() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String sqlStr = " select distinct expenditure_id from oiipcra_oltp.expenditure_mapping where estimate_id is not null and estimate_id !=0 and is_active=true ";

        return namedJdbc.queryForList(sqlStr, sqlParam, Integer.class);
    }
    public Integer getEstimateByContractId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String sqlStr = "select estimate_id from oiipcra_oltp.contract_m where id=:contractId ";

        sqlParam.addValue("contractId", contractId);
        try {
            return namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }
    public List<TenderInfo> getBidIdByProjectId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTender = "select tender_id as id,bid_id,notice.id as tenderNoticeId,work_identification_code  from oiipcra_oltp.tender_notice as notice \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=notice.tender_id where notice.id in" +
                "(select tender_notice_id from oiipcra_oltp.tender_notice_level_mapping where tank_id=:tankId and is_active=true) ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.query(getTender, sqlParam, new BeanPropertyRowMapper<>(TenderInfo.class));
        }
        catch (Exception e){
            return null;
        }
    }
    public List<ContractDto> getContractNoByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getContract = "select * from oiipcra_oltp.contract_m where tender_id=:tenderId and is_active=true ";
        sqlParam.addValue("tenderId", tenderId);
        try{
            return namedJdbc.query(getContract, sqlParam, new BeanPropertyRowMapper<>(ContractDto.class));
        }
        catch (Exception e){
            return null;
        }
        }

    public  List<ExpenditureInfo> getExpenditureDataByWorkTypeId(ExpenditureListDto expenditureListDto) {
        List<Integer> userIdList = new ArrayList<>();

        userIdList.add(expenditureListDto.getUserId());

        userIdList = userQryRepo.getSubUsers(expenditureListDto.getUserId());


        UserInfoDto userInfoById = userQryRepo.getUserById(expenditureListDto.getUserId());
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select ed.id,value,ed.value,ed.payment_date, ip.name as paymentType,ed.description as description,epm.tank_id as tankId,tankM.name_of_the_m_i_p as tankName\n" +
                "from oiipcra_oltp.expenditure_data as ed \n" +
                "left join oiipcra_oltp.expenditure_mapping as epm on epm.expenditure_id = ed.id \n" +
                "left join oiipcra_oltp.tank_m_id as tankM on tankM.id=epm.tank_id\n" +
                "left join oiipcra_oltp.invoice_payment_type as ip on ip.id= ed.payment_type ";
        if(expenditureListDto.getWorkType()==1){
            queryString +=" where  epm.is_active=true and ed.type in (3,2,5)";
            if(expenditureListDto.getContractId()!=null && expenditureListDto.getContractId()>0){
                queryString +=" and ed.contract_id=:contractId ";
                sqlParam.addValue("contractId",expenditureListDto.getContractId());
            }
            if(expenditureListDto.getTenderId()!=null && expenditureListDto.getTenderId()>0){
                queryString +=" and epm.tender_id=:tenderId ";
                sqlParam.addValue("tenderId",expenditureListDto.getTenderId());
            }
            if(expenditureListDto.getTenderNoticeId()!=null && expenditureListDto.getTenderNoticeId()>0){
                queryString +=" and epm.tender_notice_id=:tenderNoticeId ";
                sqlParam.addValue("tenderNoticeId",expenditureListDto.getTenderNoticeId());
            }
        }
        if(expenditureListDto.getWorkType()==2){
            queryString +=" where  epm.is_active=true and  ed.type in (3,5)";
            if(expenditureListDto.getContractId()!=null && expenditureListDto.getContractId()>0){
                queryString +=" and ed.contract_id=:contractId ";
                sqlParam.addValue("contractId",expenditureListDto.getContractId());
            }
            if(expenditureListDto.getSubActivityId()!=null && expenditureListDto.getSubActivityId()>0){
                queryString +=" and epm.activity_id=:activityId ";
                sqlParam.addValue("activityId",expenditureListDto.getSubActivityId());
            }
        }
        if(expenditureListDto.getWorkType()==3){
            queryString +=" where  epm.is_active=true and  ed.type in (1,4)";
            if(expenditureListDto.getActivityId()!=null && expenditureListDto.getActivityId()>0){
                queryString +=" and epm.activity_id=:activityId ";
                sqlParam.addValue("activityId",expenditureListDto.getActivityId());
            }

        }
        if (userInfoById.getRoleId() > 4) {
            if (userInfoById.getSurveyor()) {
                queryString += " AND ed.created_by IN (:userId)";
                sqlParam.addValue("userId", expenditureListDto.getUserId());
            } else {
                queryString += " AND ed.created_by IN (:userIdList)";
                sqlParam.addValue("userIdList", userIdList);
            }
        }

        try{
            return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(ExpenditureInfo.class));
        }
        catch (Exception e){
            return null;
        }
    }


}




