package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.ActivityOrTerminalInfo;
import com.orsac.oiipcra.dto.ContractCountDto;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.dto.FinanceDashboardInvoiceBlockWise;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.repository.FinanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Slf4j
public class FinanceRepositoryImpl implements FinanceRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;
    @Override
    public List<ContractCountDto> getContractStatusCount() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(contract.id),status.id as statusId,status.name as statusName from oiipcra_oltp.contract_m as contract " +
                "left join oiipcra_oltp.contract_status as status on status.id=contract.contract_status_id " +
                "group by contract.contract_status_id,status.name,status.id ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }

    @Override
    public List<ContractCountDto> getDistrictWiseGrievanceCount() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct tank.dist_id as districtId,tank.dept_dist_name as districtName,count(grievance.id)" +
                "from oiipcra_oltp.tank_m_id as tank " +
                "left join oiipcra_oltp.grievance as  grievance on tank.id=grievance.tank_id " +
                "group by dept_dist_name,dist_id order by dept_dist_name ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
    @Override
    public List<ContractCountDto> getBlockWiseGrievanceCount() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct  tank.block_id as blockId,tank.dept_dist_name as districtName,tank.dist_id as districtId,"+
                "tank.dept_block_name as blockName,count(grievance.id) from oiipcra_oltp.tank_m_id as tank "+
                "left join oiipcra_oltp.grievance as grievance on tank.id=grievance.tank_id   group by tank.dept_dist_name,tank.dist_id,tank.dept_block_name,tank.block_id order by dist_id,dept_block_name ";
       // sqlParam.addValue("distId",distId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
    @Override
    public List<ContractCountDto> getDistrictWiseIssueCountByTender() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct district.dist_id as districtId,district.district_name as districtName,count(issue.id) as count " +
                "from oiipcra_oltp.district_boundary as district " +
                "left join oiipcra_oltp.tender_notice_level_mapping as noticeMapping on noticeMapping.dist_id=district.dist_id " +
                "left join oiipcra_oltp.tender_notice as noticeMaster on noticeMaster.id=noticeMapping.tender_notice_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.tender_id=noticeMaster.tender_id " +
                "where district.in_oiipcra=1 " +
                "group by district.dist_id,district.district_name Order By district.district_name  ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
    @Override
    public List<ContractCountDto> districtWiseIssueCountByContract() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct district.dist_id as districtId,district.district_name as districtName,count(issue.id) as count " +
                "from oiipcra_oltp.district_boundary as district " +
                "left join oiipcra_oltp.contract_mapping as contractMapping on contractMapping.dist_id=district.dist_id " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=contractMapping.contract_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.contract_id=contract.id " +
                "where district.in_oiipcra=1  " +
                "group by district.dist_id,district.district_name Order By district.district_name  ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
    @Override
    public List<ContractCountDto> districtWiseIssueCountByTank() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct district.dist_id as districtId,district.district_name as districtName,count(issue.id) as count " +
                "from oiipcra_oltp.district_boundary as district " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.dist_id=district.dist_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.tank_id=tank.id " +
                "where district.in_oiipcra=1  " +
                "group by district.dist_id,district.district_name Order By district.district_name" ;
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
    @Override
    public List<ContractCountDto> getBlockWiseIssueCountForTender() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct block.block_id as blockId,block.block_name as blockName,block.dist_id as districtId,block.district_name as districtName,count(issue.id) as count " +
                "from oiipcra_oltp.block_boundary as block " +
                "left join oiipcra_oltp.tender_notice_level_mapping as noticeMapping on noticeMapping.block_id=block.block_id  " +
                "left join oiipcra_oltp.tender_notice as noticeMaster on noticeMaster.id=noticeMapping.tender_notice_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.tender_id=noticeMaster.tender_id " +
                "where block.in_oiipcra=1  " +
                "group by block.dist_id,block.district_name,block.block_id,block.block_name Order By block.dist_id,block.block_name ";
       // sqlParam.addValue("distId",distId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
    @Override
    public List<ContractCountDto> getBlockWiseIssueCountForContract() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct block.block_id as blockId,block.block_name as blockName,block.dist_id as districtId,block.district_name as districtName,count(issue.id) as count " +
                "from oiipcra_oltp.block_boundary as block " +
                "left join oiipcra_oltp.contract_mapping as contractMapping on contractMapping.block_id=block.block_id " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=contractMapping.contract_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.contract_id=contract.id " +
                "where block.in_oiipcra=1  " +
                "group by block.dist_id,block.district_name,block.block_id,block.block_name Order By block.dist_id,block.block_name ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
    @Override
    public List<ContractCountDto> getBlockWiseIssueCountForTank() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct block.block_id as blockId,block.block_name as blockName,block.dist_id as districtId,block.district_name as districtName,count(issue.id) as count " +
                "from oiipcra_oltp.block_boundary as block " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.block_id=block.block_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.tank_id=tank.id " +
                "where block.in_oiipcra=1  " +
                "group by block.dist_id,block.district_name,block.block_id,block.block_name Order By block.dist_id,block.block_name ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }

    @Override
    public List<ContractCountDto> getBlockAndDeptWiseIssueCountForTender() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct block.block_id as blockId,block.block_name as blockName,issue.dept_id as departmentId,department.name as departmentName,count(issue.id) as count " +
                "from oiipcra_oltp.block_boundary as block " +
                "left join oiipcra_oltp.tender_notice_level_mapping as noticeMapping on noticeMapping.block_id=block.block_id  " +
                "left join oiipcra_oltp.tender_notice as noticeMaster on noticeMaster.id=noticeMapping.tender_notice_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.tender_id=noticeMaster.tender_id " +
                "inner join oiipcra_oltp.dept_m as department on department.id=issue.dept_id " +
                "where block.in_oiipcra=1 " +
                "group by issue.dept_id,department.name,block.block_id,block.block_name Order By block.block_name ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }

    @Override
    public List<ContractCountDto> getBlockAndDeptWiseIssueCountForContract() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct block.block_id as blockId,block.block_name as blockName,issue.dept_id as departmentId,department.name as departmentName,count(issue.id) as count " +
                "from oiipcra_oltp.block_boundary as block " +
                "left join oiipcra_oltp.contract_mapping as contractMapping on contractMapping.block_id=block.block_id " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=contractMapping.contract_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.contract_id=contract.id " +
                "inner join oiipcra_oltp.dept_m as department on department.id=issue.dept_id " +
                "where block.in_oiipcra=1 " +
                "group by block.block_id,block.block_name,issue.dept_id,department.name Order By block.block_name ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }

    @Override
    public List<ContractCountDto> getBlockAndDeptWiseIssueCountForTank() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct block.block_id as blockId,block.block_name as blockName,issue.dept_id as departmentId,department.name as departmentName,count(issue.id) as count " +
                " from oiipcra_oltp.block_boundary as block " +
                " left join oiipcra_oltp.tank_m_id as tank on tank.block_id=block.block_id " +
                " left join oiipcra_oltp.issue_tracker as issue on issue.tank_id=tank.id " +
                " inner join oiipcra_oltp.dept_m as department on department.id=issue.dept_id " +
                " where block.in_oiipcra=1 " +
                " group by block.block_id,block.block_name,issue.dept_id,department.name Order By block.block_name ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }

   /* @Override
    public List<ContractCountDto> getBlockAndDepartmentWiseIssueCount() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select Distinct issue.dept_id as departmentId, block.block_id as blockId,block.block_name,count(issue.id) as count ,dept.name as departmentName from oiipcra_oltp.block_boundary as block " +
                "left join oiipcra_oltp.tender_notice_level_mapping as noticeMapping on noticeMapping.block_id=block.block_id  " +
                "left join oiipcra_oltp.tender_notice as noticeMaster on noticeMaster.id=noticeMapping.tender_notice_id " +
                "left join oiipcra_oltp.issue_tracker as issue on issue.tender_id=noticeMaster.tender_id " +
                "left join oiipcra_oltp.dept_m as dept on dept.id=issue.dept_id " +
                "where block.in_oiipcra=1 " +
                "group by block.block_id,issue.dept_id,dept.name,block.block_name Order By  block.block_id,dept.name ";
        // sqlParam.addValue("distId",distId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }
*/

    @Override
    public List<ContractCountDto> getInvoiceStatusCount() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(invoice.id), status.id as statusId,status.name as statusName from oiipcra_oltp.invoice_m as invoice " +
                "left join oiipcra_oltp.invoice_status as status on status.id=invoice.status " +
                "group by invoice.status,status.name,status.id ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractCountDto.class));
    }

    //District Wise Invoice Count
    @Override
    public List<FinanceDashBoardInvoiceDistWise> getDistrictWiseInvoiceCount() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id as distId, dist.district_name as distName, count(im.id) over(partition by dist.dist_id) as count" +
                "    from oiipcra_oltp.district_boundary as dist" +
                "    left join oiipcra_oltp.contract_mapping as cm on cm.dist_id=dist.dist_id" +
                "    left join oiipcra_oltp.invoice_m as im on im.contract_id=cm.contract_id" +
                "    WHERE dist.in_oiipcra = 1";
       /* if(distId>0){
            qry+= " and dist.dist_id =:distId";
            sqlParam.addValue("distId",distId);
        }*/
        qry+= " order by district_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinanceDashBoardInvoiceDistWise.class));
    }

    public List<FinanceDashboardInvoiceBlockWise> getBlockWiseInvoiceCount(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct block.dist_id as distId, block.district_name as distName, block.block_id as blockId, block.block_name as blockName, count(im.id) over(partition by block.block_id) as count" +
                " from oiipcra_oltp.block_boundary as block" +
                " left join oiipcra_oltp.district_boundary as dist on  dist.dist_id = block.dist_id" +
                " left join oiipcra_oltp.contract_mapping as cm on cm.dist_id=dist.dist_id" +
                " left join oiipcra_oltp.invoice_m as im on im.contract_id=cm.contract_id" +
                " WHERE block.in_oiipcra = 1";
       /* if(distId>0){
            qry+= " AND block.dist_id =:distId";
            sqlParam.addValue("distId",distId);
        }*/
        qry+= " order by block.dist_id,block_name";
       /* }
        if(blockId>0){
            qry+= " AND block.block_id =:blockId";
            sqlParam.addValue("blockId",blockId);
        }
        qry+= " order by block_name";*/
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinanceDashboardInvoiceBlockWise.class));
    }

    @Override
    public List<FinanceDashBoardInvoiceDistWise> getDistrictWiseSurveyCount(int distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="SELECT distinct dist.dist_id as distId, dist.district_name as distName, count(tsd.tank_id) over(partition by dist.dist_id) as count" +
                " FROM oiipcra_oltp.district_boundary as dist" +
                " LEFT JOIN oiipcra_oltp.tank_survey_data as tsd on tsd.district_id = dist.dist_id" +
                " WHERE dist.in_oiipcra = 1";
        if(distId>0){
            qry+= " AND dist.dist_id =:distId";
            sqlParam.addValue("distId",distId);
        }
        qry+= " order by district_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinanceDashBoardInvoiceDistWise.class));
    }

    public List<FinanceDashboardInvoiceBlockWise> getBlockWiseSurveyCount(int distId, int blockId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct block.dist_id as distId, block.district_name as distName, block.block_id as blockId, block.block_name as blockName, count(tsd.tank_id) over(partition by block.block_id) as count" +
                " from oiipcra_oltp.block_boundary as block" +
                " left join oiipcra_oltp.district_boundary as dist on  dist.dist_id = block.dist_id" +
                " LEFT JOIN oiipcra_oltp.tank_survey_data as tsd on tsd.district_id = dist.dist_id" +
                " WHERE block.in_oiipcra = 1";
        if(distId>0){
            qry+= " AND block.dist_id =:distId";
            sqlParam.addValue("distId",distId);
        }
        if(blockId>0){
            qry+= " AND block.block_id =:blockId";
            sqlParam.addValue("blockId",blockId);
        }
        qry+= " order by block_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinanceDashboardInvoiceBlockWise.class));
    }
}
