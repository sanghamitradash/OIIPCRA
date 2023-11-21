package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.ActivityEstimateTankMappingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class ActivityQryRepository {
    @Value("${dbschema}")
    private String DBSCHEMA;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    private MasterQryRepository masterQryRepository;

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

    public List<ComponentResponse> getChildList(int parentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String childQuery = "SELECT mhd.id, mhd.name, mhd.code, mhd.parent_id, mhd.master_head_id,mhd.is_terminal as terminal FROM oiipcra_oltp.master_head_details mhd " +
                "left join oiipcra_oltp.pip_mapping_details pmd ON mhd.id=pmd.master_head_dtls_id " +
                "where pmd.pip_id=1 and mhd.parent_id =:parentId order by mhd.id ";
        sqlParam.addValue ("parentId", parentId);
        return namedJdbc.query(childQuery, sqlParam, new BeanPropertyRowMapper<>(ComponentResponse.class));
    }

    public ActivityInformationDto getActivityInformation(int activityId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id, mhd.name, mhd.code, mhd.parent_id as parentId, mhd.master_head_id as masterHeadId, mhd.is_terminal as isTerminal, mhd.description" +
                " FROM oiipcra_oltp.master_head_details mhd " +
                " LEFT JOIN oiipcra_oltp.pip_mapping_details pmd ON mhd.id=pmd.master_head_dtls_id " +
                " WHERE mhd.id=:activityId AND mhd.is_active = true order by mhd.id ";
        sqlParam.addValue ("activityId", activityId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityInformationDto.class));
    }
    public List<ComponentInfo> getAllComponentsByPipId(int pipId, int parentId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT name, code, level_id, approval_id, status_id FROM oiipcra_oltp.master_head_details" +
                " LEFT JOIN oiipcra_oltp.pip_mapping_details as pipdetails ON pipdetails.master_head_dtls_id = master_head_details.id" +
                " WHERE pipdetails.pip_id =:pipId AND master_head_details.parent_id =:parentId";
        sqlParam.addValue("pipId",pipId);
        sqlParam.addValue("parentId",parentId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentInfo.class));
    }

    public Boolean deactivateEstimateMapping(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE activity_estimate_tank_mapping SET is_active = false WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update > 0;

    }

/*    public TerminalOrActivityInfo getTerminalInfoById(int id, int financialYr){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id, mhd.name, mhd.level_id, mhd.approval_id, pd.unit_id, pd.physical_target, pd.unit_cost_rs, pd.financial_target, ed.month_id, ed.value, " +
                " c.dist_id, c.division_id, c.area_id, c.procurement_made_for, c.file_no, c.approval_order, c.technical_sanction_no, c.work_id, c.work_type_id, c.work_description, " +
                " c.rfb_id, c.rfd_issued_date, c.rfd_received_date, c.estimated_cost, c.eoi_id, c.eoi_date, c.agency_id, c.agency_name, c.agency_address, c.pan_no, c.mobile, c.awarded_to, c.rate_quoted, " +
                " c.loa_issued_no, c.loa_issued_date, c.contract_no, c.contract_date, c.contract_amt, c.total_contract_amt, c.contract_signed, c.agreement_no," +
                " c.agreement_amt, c.gst, c.comencement_date, c.completion_date, c.completion_period, c.eot_1_sanctioned_upto, c.eot_2_sanctioned_upto FROM master_head_details AS mhd" +
                " LEFT JOIN pip_mapping_details AS pmd ON mhd.id = pmd.master_head_dtls_id" +
                " LEFT JOIN pip_details AS pd ON pmd.id = pd.pip_mapping_id" +
                " LEFT JOIN contract_m as c on c.id=pd.contract_id" +
                " LEFT JOIN expenditure_data as ed on ed.contract_id=c.id" +
                " WHERE pd.finyr_id =:financialYr  AND mhd.is_terminal = true and mhd.id=:id";
        sqlParam.addValue("id",id);
        sqlParam.addValue("financialYr",financialYr);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TerminalOrActivityInfo.class));
    }*/

    public ActivityOrTerminalInfo getTerminalInfoById(int id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id, mhd.name, mhd.level_id, mhd.approval_id" +
                " FROM master_head_details as mhd" +
                " LEFT JOIN pip_mapping_details AS pmd ON mhd.id = pmd.master_head_dtls_id" +
                " WHERE mhd.is_terminal = true AND mhd.id =:id";
        sqlParam.addValue("id",id);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityOrTerminalInfo.class));
    }

    public List<ActivityTargetInfo> getActivityTargetInfo(int id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id as terminalId,mhd.name as name,pd.unit_id as unitId,unit.name as unitName,pd.physical_target as physicalTarget," +
                "pd.unit_cost_rs as unitCostRs, pd.financial_target as financialTarget,year.name as financialYear, pd.contract_amount as contractAmount " +
                " FROM oiipcra_oltp.master_head_details as mhd " +
                " LEFT JOIN oiipcra_oltp.pip_mapping_details AS pmd ON mhd.id = pmd.master_head_dtls_id " +
                " LEFT JOIN oiipcra_oltp.pip_details AS pd ON pmd.id = pd.pip_mapping_id " +
                " LEFT JOIN oiipcra_oltp.unit_m as unit on unit.id=pd.unit_id " +
                " LEFT JOIN oiipcra_oltp.fin_year_m as year on year.id=pd.finyr_id " +
                " WHERE mhd.is_terminal = true AND mhd.id=:id AND mhd.is_active = true AND pd.is_active = true ";
        sqlParam.addValue("id",id);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityTargetInfo.class));
    }

    public List<ActivityExpenditureInfo> getActivityExpenditure(int id, int financialYr){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT ed.month_id, ed.value FROM oiipcra_oltp.expenditure_data AS ed" +
                " LEFT JOIN oiipcra_oltp.master_head_details AS mhd ON mhd.id = ed.activity_id" +
                " WHERE mhd.is_terminal = true AND mhd.id =:id";
        sqlParam.addValue("id",id);
        if(financialYr>0){
            qry+= " AND ed.finyr_id =:financialYr";
            sqlParam.addValue("financialYr",financialYr);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityExpenditureInfo.class));
    }

    public List<ActivityContractInfo> getActivityContractInfo(int id, int financialYr){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT c.dist_id, c.division_id, c.area_id, c.procurement_made_for, c.file_no, c.approval_order, c.technical_sanction_no, c.work_id, c.work_type_id, c.work_description," +
                " c.rfb_id, c.rfd_issued_date, c.rfd_received_date, c.estimated_cost, c.eoi_id, c.eoi_date, c.agency_id, c.mobile, c.awarded_to, c.rate_quoted," +
                " c.loa_issued_no, c.loa_issued_date, c.contract_no, c.contract_date, c.contract_amt, c.total_contract_amt, c.contract_signed, c.agreement_no," +
                " c.agreement_amt, c.gst, c.comencement_date, c.completion_date, c.completion_period, c.eot_1_sanctioned_upto, c.eot_2_sanctioned_upto FROM contract_m as c" +
                " LEFT JOIN pip_details as pd ON c.activity_id = pd.id";
        sqlParam.addValue("id",id);
        if(financialYr>0){
            qry+= " AND c.fin_yr_id =:financialYr";
            sqlParam.addValue("financialYr",financialYr);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityContractInfo.class));
    }

    public List<ActivityTreeTargetInfo> getActivityTreeTargetInfo(int parentId, int financialYr){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "with hierarchy1 as(" +
                "SELECT parent_id,id as childId,name as mhdName FROM oiipcra_oltp.master_head_details " +
                "GROUP BY parent_id,id) " +
                "SELECT mh.id,mh.name,t.childId,m.name as type, " +
                "CASE WHEN t.mhdName is null then '' ELSE t.mhdName end, pd.finyr_id, " +
                "CASE WHEN pd.physical_target is null then cast(0 as integer) ELSE pd.physical_target end, " +
                "CASE WHEN pd.unit_cost_rs is null then cast(0 as integer) ELSE pd.unit_cost_rs end, " +
                "CASE WHEN pd.financial_target is null then cast(0 as integer) ELSE pd.financial_target end " +
                "FROM hierarchy1 t " +
                "LEFT JOIN oiipcra_oltp.master_head_details mh on t.parent_id=mh.id " +
                "LEFT JOIN oiipcra_oltp.master_head m on mh.master_head_id = m.id " +
                "LEFT JOIN oiipcra_oltp.pip_mapping_details AS pmd ON t.childId = pmd.master_head_dtls_id " +
                "LEFT JOIN oiipcra_oltp.pip_details AS pd ON pmd.id = pd.pip_mapping_id " +
                "WHERE t.parent_id =:parentId";
        sqlParam.addValue("parentId",parentId);
        if(financialYr>0){
            qry+= " AND finyr_id =:financialYr";
            sqlParam.addValue("financialYr",financialYr);
        }
        qry += " ORDER BY t.childId";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityTreeTargetInfo.class));
    }

    public List<Integer> getTerminalId(int parentId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "(WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id =:parentId UNION ALL " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true);";
            sqlParam.addValue("parentId",parentId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getContractIdsByWorkType(Integer  workTypeId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select id from oiipcra_oltp.contract_m where work_type_id=:workTypeId";
        sqlParam.addValue("workTypeId",workTypeId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getEstimateIdsByWorkType(Integer  workTypeId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select id from oiipcra_oltp.activity_estimate_mapping where work_type=:workTypeId";
        sqlParam.addValue("workTypeId",workTypeId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }


    public List<Integer> getCompletedEstimatesSubactivity(List<Integer> activityList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select distinct activity_id from activity_estimate_mapping where activity_id in (:activityList) and approved_status=2";
        sqlParam.addValue("activityList",activityList);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getCompletedEstimatesSubactivityComp1(List<Integer> activityList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select distinct activity_id from agriculture_estimate where activity_id in (:activityList)";
        sqlParam.addValue("activityList",activityList);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getPhysicalProgressSAComp1(List<Integer> activityList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select distinct activity_id from denormalized_achievement where activity_id in (:activityList)";
        sqlParam.addValue("activityList",activityList);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getCompletedContractSubactivity(List<Integer> activityList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select distinct activity_id from contract_mapping where activity_id in (:activityList)";
        sqlParam.addValue("activityList",activityList);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getCompletedEstimatesActivity(List<Integer> activityList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select distinct parent_id from oiipcra_oltp.master_head_details where id in (:activityList)";
        sqlParam.addValue("activityList",activityList);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Activity> getAllUpperData(List<Integer> activityList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select * from oiipcra_oltp.master_head_details where id in " +
                "(Select parent_id from  oiipcra_oltp.master_head_details where id in (:activityList))";
        sqlParam.addValue("activityList",activityList);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(Activity.class));
    }

    public List<ActivityUpperHierarchyInfo> getUpperHierarchyInfoById(Integer activityId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal, name, code, master_head_id, description FROM oiipcra_oltp.master_head_details WHERE id =:activityId UNION ALL" +
                " SELECT mhd.id, mhd.parent_id,mhd.is_terminal,mhd.name,mhd.code,mhd.master_head_id, mhd.description FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.parent_id = mhd.id)" +
                " SELECT * FROM terminal_Ids ORDER BY id;";
        sqlParam.addValue ("activityId", activityId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityUpperHierarchyInfo.class));
    }

    public ComponentInfo getParentInfo(int nodeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String parentQuery = "SELECT mhd.id, mhd.name, mhd.code, mhd.parent_id, mhd.master_head_id,mhd.is_terminal as terminal " +
                " FROM oiipcra_oltp.master_head_details mhd" +
                " where mhd.parent_id =:parentId";
        sqlParam.addValue ("parentId", nodeId);
        return namedJdbc.queryForObject(parentQuery, sqlParam, new BeanPropertyRowMapper<>(ComponentInfo.class));
    }

    public NameCodeTree getNameCodeTree(int nodeId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String parentQry = " SELECT string_agg(t.name, '-') as nameTree, string_agg(t.code,'-') as codeTree from (" +
                "(WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id,is_terminal,name,code FROM oiipcra_oltp.master_head_details WHERE id =:nodeId UNION ALL " +
                " SELECT mhd.id, mhd.parent_id,mhd.is_terminal,mhd.name,mhd.code FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.parent_id = mhd.id) " +
                " SELECT id,name, code FROM terminal_Ids order by id))t;";

        sqlParam.addValue ("nodeId", nodeId);
        return namedJdbc.queryForObject(parentQry, sqlParam, new BeanPropertyRowMapper<>(NameCodeTree.class));
    }

    public List<IdNameCodeTree> getNameCodeStruct(int nodeId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String parentQry = "(WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id,is_terminal,name,code FROM oiipcra_oltp.master_head_details WHERE id =:nodeId UNION ALL " +
                " SELECT mhd.id, mhd.parent_id,mhd.is_terminal,mhd.name,mhd.code FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.parent_id = mhd.id) " +
                " SELECT id,name, code FROM terminal_Ids order by id)";

        sqlParam.addValue ("nodeId", nodeId);
        return namedJdbc.query(parentQry, sqlParam, new BeanPropertyRowMapper<>(IdNameCodeTree.class));
    }
    public List<ActivityLevelDto> getAllActivityLevel(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String parentQry = "SELECT id, level_name as name FROM oiipcra_oltp.activity_level_master where is_active=true";

        return namedJdbc.query(parentQry, sqlParam, new BeanPropertyRowMapper<>(ActivityLevelDto.class));
    }
    public List<ActivityStatusDto> getAllMasterHeadDropDown(Integer parentId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String parentQry = "WITH RECURSIVE hierarchyId AS (SELECT id, parent_id,name FROM oiipcra_oltp.master_head WHERE parent_id =:parentId  UNION ALL " +
                "SELECT mh.id, mh.parent_id,mh.name FROM oiipcra_oltp.master_head mh INNER JOIN hierarchyId t ON t.id = mh.parent_id)" +
                "SELECT distinct(name),id FROM hierarchyId as t ";
        sqlParam.addValue("parentId",parentId);
        return namedJdbc.query(parentQry, sqlParam, new BeanPropertyRowMapper<>(ActivityStatusDto.class));
    }

    public List<TerminalIdName> getTermialActivityNameCode(int terminalId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String parentQry = "WITH RECURSIVE terminal_Ids (id,parent_id,path,name) AS " +
                " (SELECT m.id, m.parent_id,m.code,m.name FROM oiipcra_oltp.master_head_details as m where is_terminal=true" +
                " UNION ALL " +
                " SELECT t.id, mhd.parent_id,concat(mhd.code,'_',path),t.name FROM  " +
                " oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.parent_id = mhd.id ) " +
                " SELECT id,concat(name,'(',path,')') as name FROM terminal_Ids where parent_id=0 order by id";

        //sqlParam.addValue ("nodeId", terminalId);
        return namedJdbc.query(parentQry, sqlParam, new BeanPropertyRowMapper<>(TerminalIdName.class));
    }

    public Integer checkUniqueCodeByMasterHeadId(String code, int masterHeadId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT count(*) as code FROM oiipcra_oltp.master_head_details WHERE code LIKE :code AND master_head_id=:masterHeadId";
        sqlParam.addValue("masterHeadId",masterHeadId);
        sqlParam.addValue("code",code);
        Integer update = namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        if(null != update){
            return update;
        }
        return 0;
    }

    public List<ComponentDetailsDto> getComponentActivity(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select master.id as id,finyr.name as financialYear,coalesce(sum(pipd.physical_target),0.0) as physicalTarget,coalesce(sum(pipd.financial_target),0.0) as financialTarget," +
                "coalesce(sum(ex.value),0.0) as totalExpenditure,master.is_terminal as isTerminal " +
                "from  oiipcra_oltp.master_head_details as master " +
                "left join oiipcra_oltp.pip_mapping_details as pipmd on pipmd.master_head_dtls_id=master.id " +
                "left join oiipcra_oltp.pip_details as pipd on pipd.pip_mapping_id=pipmd.id " +
                "left join oiipcra_oltp.fin_year_m as finyr on finyr.id=pipd.finyr_id " +
                "left join oiipcra_oltp.expenditure_data as ex on ex.activity_id=master.id and finyr.id=ex.finyr_id " +
                "where master.id=:id group by master.id,finyr.name order by finyr.name";
        sqlParam.addValue("id",id);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDetailsDto.class));
    }
    public List<ComponentDetailsDto> getComponentActivityDetails(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select master.id,coalesce(sum(mapping.estimated_amount),0.0) as totalEstimatedAmount,coalesce(sum(t1.totalOngoing),0) as totalOngoing,coalesce(sum(t2.totalCompleted),0) as totalCompleted," +
                "coalesce(sum(t3.totalSubmitted),0) as totalEstimatePending,coalesce(sum(t4.totalRejected),0) as totalEstimateRejected,coalesce(sum(t5.totalApproved),0) as totalEstimateApproved," +
                "coalesce(sum(ex.totalExpeniture),0.0) as totalExpenditure from " +
                "oiipcra_oltp.master_head_details as master " +
                "left join (select activity_id,sum(value) as totalExpeniture from oiipcra_oltp.expenditure_data group by activity_id) as ex on ex.activity_id=master.id " +
                "left join (select activity_id,sum(estimated_amount) as estimated_amount from oiipcra_oltp.activity_estimate_mapping group by activity_id) as mapping on mapping.activity_id=master.id " +
                "left join (select activity_id,count(*) as totalOngoing from oiipcra_oltp.activity_estimate_mapping where status_id=1 group by activity_id) as t1 on t1.activity_id=master.id " +
                "left join (select activity_id,count(*) as totalCompleted from oiipcra_oltp.activity_estimate_mapping where status_id=2 group by activity_id) as t2 on t2.activity_id=master.id " +
                "left join (select activity_id,count(*) as totalSubmitted from oiipcra_oltp.activity_estimate_mapping where status_id=3 group by activity_id) as t3 on t3.activity_id=master.id " +
                "left join (select activity_id,count(*) as totalRejected from oiipcra_oltp.activity_estimate_mapping where approved_status=3 group by activity_id) as t4 on t4.activity_id=master.id " +
                "left join (select activity_id,count(*) as totalApproved from oiipcra_oltp.activity_estimate_mapping where approved_status=2 group by activity_id) as t5 on t5.activity_id=master.id " +
                "where mapping.activity_id in(WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id,is_terminal,name,code FROM oiipcra_oltp.master_head_details WHERE id =:id  UNION ALL " +
                " SELECT mhd.id, mhd.parent_id,mhd.is_terminal,mhd.name,mhd.code FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                " SELECT id FROM terminal_Ids order by id) group by master.id ";
        sqlParam.addValue("id",id);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDetailsDto.class));
    }
    public CountDto getCount(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(case when master_head_id=2 then 1 else null end) as  totalSubComponent," +
                "count(case when master_head_id in (3,6) then 1 else null end) as  totalActivity," +
                "count(case when master_head_id in(4,5,7,8) then 1 else null end) as  totalSubActivity " +
                "from " +
                "(WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id,is_terminal,name,code,master_head_id FROM oiipcra_oltp.master_head_details WHERE parent_id =:id  UNION ALL " +
                " SELECT mhd.id, mhd.parent_id,mhd.is_terminal,mhd.name,mhd.code,mhd.master_head_id FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                " SELECT id,master_head_id FROM terminal_Ids) as t";
        sqlParam.addValue("id",id);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(CountDto.class));
    }
    public boolean updateEstimateApproval(Integer id, ApprovalDto approvalRequest){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry =" ";
        if(approvalRequest.getApprovalStatusId()==2) {
            qry += " UPDATE oiipcra_oltp.activity_estimate_mapping " +
                    "SET approved_status=:approveStatus,approval_date=:approvalDate,approved_by=:approvedBy,approval_order=:approvalOrder,status_id=1 " +
                    " WHERE id=:id  ";
        }
        else{
            qry+="UPDATE oiipcra_oltp.activity_estimate_mapping " +
                 " SET approved_status=:approveStatus,approval_date=:approvalDate,approved_by=:approvedBy,approval_order=:approvalOrder,status_id=4 ,estimate_type=3 " +
                    "  WHERE id=:id ";
        }
        sqlParam.addValue("id",id);
        sqlParam.addValue("approveStatus",approvalRequest.getApprovalStatusId());
        sqlParam.addValue("approvalDate",approvalRequest.getApprovalDate());
        sqlParam.addValue("approvedBy",approvalRequest.getApprovedBy());
        sqlParam.addValue("approvalOrder",approvalRequest.getApprovalOrder());

        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }
    public boolean deactivateEstimate(Integer estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.activity_estimate_mapping " +
                " SET is_active=false WHERE id=:id";
        sqlParam.addValue("id",estimateId);

        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }
    public boolean updateEstimate(Integer id, ActivityEstimateAddDto activityRequest, MultipartFile file){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "UPDATE oiipcra_oltp.activity_estimate_mapping " +
//                "SET  activity_id=:activityId, level_id=:levelId, status_id=:statusId,work_type=:workType, approval_order=:approvalOrder, name_of_work=:nameOfWork, technical_sanction_no=:technicalSanctionNo," +
//                "project_id=:projectId, procurement_type=:procurementType, district_zone_identification=:districtZoneIdentification, nol_of_tor_by_wb=:nolOfTorByWb, approval_ref=:approvalRef, correspondance_file_no=:fileNo," +
//                "period_of_completion=:periodOfCompletion, start_date=:startDate, end_date=:endDate, estimated_amount=:estimatedAmount, approved_status=:approvedStatus, updated_by=:updatedBy,estimate_type=:typeId,is_active=true ";

        String qry = "UPDATE oiipcra_oltp.activity_estimate_mapping  " +
                "SET  activity_id=:activityId, level_id=:levelId, status_id=:statusId,work_type=:workType, approval_order=:approvalOrder, name_of_work=:nameOfWork, technical_sanction_no=:technicalSanctionNo,  " +
                "project_id=:projectId, procurement_type=:procurementType, district_zone_identification=:districtZoneIdentification, nol_of_tor_by_wb=:nolOfTorByWb, approval_ref=:approvalRef, correspondance_file_no=:fileNo,  " +
                "period_of_completion=:periodOfCompletion, start_date=:startDate, end_date=:endDate, estimated_amount=:estimatedAmount,  " +
                "review_type=:reviewType,market_approach=:marketApproach,loan_credit_no=:loanCreditNo,procurement_document_type=:procurementDocumentType,  " +
                "high_sea_sh_risk=:highSeaShRisk,procurement_process=:procurementProcess,evaluation_options=:evaluationOptions,  " +
                "approved_status=:approvedStatus, updated_by=:updatedBy,estimate_type=:typeId,is_active=true ";
        if(file!=null && !file.isEmpty() ){
            qry+=",document_name=:docName,document_path=:docPath ";
            sqlParam.addValue("docName",file.getOriginalFilename());
            sqlParam.addValue("docPath","https://ofarisbucket.s3.ap-south-1.amazonaws.com/EstimateDocument");
        }
        qry+=" where id=:id";
        sqlParam.addValue("id",id);
        sqlParam.addValue("activityId",activityRequest.getActivityId());
        sqlParam.addValue("levelId",activityRequest.getLevelId());

        sqlParam.addValue("workType",activityRequest.getWorkType());
        sqlParam.addValue("approvalOrder",activityRequest.getApprovalOrder());
        sqlParam.addValue("nameOfWork",activityRequest.getNameOfWork());
        sqlParam.addValue("technicalSanctionNo",activityRequest.getTechnicalSanctionNo());
        sqlParam.addValue("projectId",activityRequest.getProjectId());
        sqlParam.addValue("procurementType",activityRequest.getProcurementType());
        sqlParam.addValue("districtZoneIdentification",activityRequest.getDistrictZoneIdentification());
        sqlParam.addValue("nolOfTorByWb",activityRequest.getNolOfTorByWb());
        sqlParam.addValue("approvalRef",activityRequest.getApprovalRef());
        sqlParam.addValue("fileNo",activityRequest.getCorrespondanceFileNo());
        sqlParam.addValue("periodOfCompletion",activityRequest.getPeriodOfCompletion());
        sqlParam.addValue("startDate",activityRequest.getStartDate());
        sqlParam.addValue("endDate",activityRequest.getEndDate());
        if(activityRequest.getEstimateType()==3){
            sqlParam.addValue("typeId",3);
            sqlParam.addValue("approvedStatus",3);
            sqlParam.addValue("statusId",4);
        }
        else{
            sqlParam.addValue("typeId",activityRequest.getEstimateType());
            sqlParam.addValue("approvedStatus",activityRequest.getApprovedStatus());
            sqlParam.addValue("statusId",activityRequest.getStatusId());
        }

        sqlParam.addValue("estimatedAmount",activityRequest.getEstimatedAmount());

        sqlParam.addValue("reviewType",activityRequest.getReviewType());
        sqlParam.addValue("marketApproach",activityRequest.getMarketApproach());
        sqlParam.addValue("loanCreditNo",activityRequest.getLoanCreditNo());
        sqlParam.addValue("procurementDocumentType",activityRequest.getProcurementDocumentType());
        sqlParam.addValue("highSeaShRisk",activityRequest.getHighSeaShRisk());
        sqlParam.addValue("procurementProcess",activityRequest.getProcurementProcess());
        sqlParam.addValue("evaluationOptions",activityRequest.getEvaluationOptions());
        sqlParam.addValue("updatedBy",activityRequest.getUpdatedBy());

        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }
    public boolean updateActivityEstimateTank(ActivityEstimateTankMappingEntity activityEstimateTankMapping){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.activity_estimate_tank_mapping " +
                "SET estimate_id=:estimateId, dist_id=:distId, block_id=:blockId, tank_id=:tankId, updated_by=:updatedBy " +
                "WHERE id=:id ";
        sqlParam.addValue("id",activityEstimateTankMapping.getId());
        sqlParam.addValue("estimateId",activityEstimateTankMapping.getEstimateId());
        sqlParam.addValue("distId",activityEstimateTankMapping.getDistId());
        sqlParam.addValue("blockId",activityEstimateTankMapping.getBlockId());
        sqlParam.addValue("tankId",activityEstimateTankMapping.getTankId());
        sqlParam.addValue("updatedBy",activityEstimateTankMapping.getUpdatedBy());
        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }

    public List<Activity> getActivityByParentId(Integer parentId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id, mhd.name, code, mhd.parent_id as parentId, master_head_id as masterHeadId, is_terminal as isTerminal, mh.name as type, mhd.description FROM oiipcra_oltp.master_head_details AS mhd" +
                " LEFT JOIN oiipcra_oltp.master_head AS mh ON mhd.master_head_id=mh.id" +
                //" LEFT JOIN oiipcra_oltp.activity_dept_mapping AS adm ON mhd.id=adm.activity_id" +
                " WHERE mhd.parent_id =:parentId ORDER BY mhd.id";
                //" AND adm.dept_id =:deptIdByUserId";
        sqlParam.addValue("parentId",parentId);
        //sqlParam.addValue("deptIdByUserId",deptIdByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(Activity.class));
    }

    public List<Activity> getActivityByParentIdAndDeptId(Integer deptIdByUserId, Integer parentId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id, mhd.name, code, mhd.parent_id as parentId, master_head_id as masterHeadId, is_terminal as isTerminal, mh.name as type, mhd.description FROM oiipcra_oltp.master_head_details AS mhd" +
                " LEFT JOIN oiipcra_oltp.master_head AS mh ON mhd.master_head_id=mh.id" +
                " LEFT JOIN oiipcra_oltp.activity_dept_mapping AS adm ON mhd.id=adm.activity_id" +
                " WHERE mhd.parent_id =:parentId "+
                " AND adm.dept_id =:deptIdByUserId ORDER BY mhd.id";
        sqlParam.addValue("parentId",parentId);
        sqlParam.addValue("deptIdByUserId",deptIdByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(Activity.class));
    }
    public List<Activity> getActivityByDeptId(Integer deptIdByUserId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id, mhd.name, code, mhd.parent_id as parentId, master_head_id as masterHeadId, is_terminal as isTerminal, mh.name as type, mhd.description FROM oiipcra_oltp.master_head_details AS mhd" +
                " LEFT JOIN oiipcra_oltp.master_head AS mh ON mhd.master_head_id=mh.id" +
                " LEFT JOIN oiipcra_oltp.activity_dept_mapping AS adm ON mhd.id=adm.activity_id" +
                " WHERE  master_head_id=4 and adm.dept_id =:deptIdByUserId ORDER BY mhd.id";

        sqlParam.addValue("deptIdByUserId",deptIdByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(Activity.class));
    }
    public List<Activity> getSubActivity(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id, mhd.name, code, mhd.parent_id as parentId, master_head_id as masterHeadId, is_terminal as isTerminal, mh.name as type, mhd.description FROM oiipcra_oltp.master_head_details AS mhd" +
                " LEFT JOIN oiipcra_oltp.master_head AS mh ON mhd.master_head_id=mh.id" +
                " WHERE master_head_id=4 ORDER BY mhd.id";



        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(Activity.class));
    }

    public List<String> getActivityDeptId(Integer activityId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dm.name as deptName FROM oiipcra_oltp.activity_dept_mapping AS adm" +
                " LEFT JOIN oiipcra_oltp.dept_m AS dm ON adm.dept_id=dm.id" +
                " WHERE adm.activity_id =:activityId and adm.is_active=true ORDER BY adm.id";

        sqlParam.addValue("activityId",activityId);
        List<String> deptName = namedJdbc.queryForList(qry, sqlParam, String.class);
        return deptName;
    }

    public Page<ActivityEstimateListingResponseDto> getAllActivityEstimate(ActivityEstimateRequestDto activityRequest, List<Integer> estimateIds){

        PageRequest pageable = PageRequest.of(activityRequest.getPage(), activityRequest.getSize(), Sort.Direction.fromString(activityRequest.getSortOrder()), activityRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) :
                new Sort.Order(Sort.Direction.DESC, "workName");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry ="";
        Integer  resultCount = 0;

         qry += "SELECT distinct estimate.activity_id as activityId ,md.name as activityName,estimate.level_id,level.level_name as levelName," +
                 "estimate.id as estimateId, estimate.name_of_work as workName, estimate.start_date as startDate,  " +
                "estimate.end_date as endDate,estimate.created_on as createdOn,estimate.estimated_amount as estimateAmount,  " +
                "status.name as status,approvalStatus.name as approvalStatus,estimate.created_by  as createdBy,   " +
                "type.name as estimateType,approvalStatus.id as approvalId,estimate.approval_date as approvalDate,  " +
                " estimate.work_type as workTypeId,work.name as workType " +
                "FROM oiipcra_oltp.activity_estimate_mapping as estimate  " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as tm on estimate.id = tm.estimate_id   " +
                "left join oiipcra_oltp.activity_status as status on status.id=estimate.status_id  " +
                "left join oiipcra_oltp.approval_status_m as approvalStatus on approvalStatus.id=estimate.approved_status   " +
                "left join oiipcra_oltp.estimate_type as type on type.id=estimate.estimate_type  " +
                "left join oiipcra_oltp.activity_level_master as level on level.id=estimate.level_id  " +
                "left join oiipcra_oltp.master_head_details as md on md.id =estimate.activity_id  " +
                "left join oiipcra_oltp.expenditure_mapping as em on em.tank_id=tm.tank_id   " +
                "left join oiipcra_oltp.invoice_m as im on im.id = em.invoice_id   " +
                 "left join oiipcra_oltp.procurement_type_m as ptm on ptm.id=estimate.procurement_type "+
                 "left join oiipcra_oltp.contract_mapping as cm on cm.estimate_id = estimate.id "+
                 "left join oiipcra_oltp.work_type_m as work on work.id=estimate.work_type   " +
                "where estimate.is_active=true ";

//         By Sp
        Integer userLevelIdByUserId = 0;
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserAreaMappingDto> userAreaMappingList = new ArrayList<>();

        UserInfoDto userInfoById = null;
        if (activityRequest.getUserId() != null) {
            //User level Get
            userInfoById = userQryRepo.getUserById(activityRequest.getUserId());
            userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(activityRequest.getUserId());
            //User Area Id List
            userAreaMappingList = userQryRepo.getUserAuthority(activityRequest.getUserId());
        }

        List<Integer> distId =new ArrayList<>();
        List<Integer> blockId = new ArrayList<>();
        List<Integer> gpId = new ArrayList<>();
        List<Integer> villageId = new ArrayList<>();
        List<Integer> divisionId = new ArrayList<>();

        userAreaMappingList.forEach(area -> distId.add(area.getDistrict_id()));
        userAreaMappingList.forEach(area -> blockId.add(area.getBlock_id()));
        userAreaMappingList.forEach(area -> gpId.add(area.getGp_id()));
        userAreaMappingList.forEach(area -> villageId.add(area.getVillage_id()));
        userAreaMappingList.forEach(area -> divisionId.add(area.getDivision_id()));
        userAreaMappingList.forEach(area -> divisionId.add(area.getSub_division_id()));
        userAreaMappingList.forEach(area -> divisionId.add(area.getSection_id()));

        if (userAreaMappingList!=null && userAreaMappingList.size()>0) {
            if (userInfoById.getRoleId() <= 4) {
                qry += " ";
            } else {
                switch (userLevelIdByUserId) {

                    case 2:
                        userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                        qry += " AND tm.dist_id IN (:authorityIdList)";
                        sqlParam.addValue("authorityIdList", authorityIdList);
                        break;

                    case 3:
                        userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                        qry += "AND  tm.block_id IN (:authorityIdList)";
                        sqlParam.addValue("authorityIdList", authorityIdList);
                        sqlParam.addValue("distId", distId);
                        break;

//                    case 4:
//                        userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
//                        qry += " AND tm.dist_id IN (:distId) AND tm.block_id IN (:blockId) AND gp_id IN (:authorityIdList)";
//                        sqlParam.addValue("authorityIdList", authorityIdList);
//                        sqlParam.addValue("distId", distId);
//                        sqlParam.addValue("blockId", blockId);
//                        break;

//                    case 5:
//                        userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
//                        qry += "  AND dist_id IN (:distId) AND block_id IN (:blockId) AND gp_id IN (:gpId) AND village_id IN (:authorityIdList)";
//                        sqlParam.addValue("authorityIdList", authorityIdList);
//                        sqlParam.addValue("distId", distId);
//                        sqlParam.addValue("blockId", blockId);
//                        sqlParam.addValue("gpId", gpId);
//                        break;
                    case 6:
                        userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                        qry += "  AND tm.division_id IN (:authorityIdList)";
                        sqlParam.addValue("authorityIdList", authorityIdList);
                        break;
//                    case 7:
//                        userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
//                        qry += "  AND dist_id IN (:distId) AND block_id IN (:blockId) AND gp_id IN (:gpId) AND village_id IN (:authorityIdList)";
//                        sqlParam.addValue("authorityIdList", authorityIdList);
//                        sqlParam.addValue("distId", distId);
//                        sqlParam.addValue("blockId", blockId);
//                        sqlParam.addValue("gpId", gpId);
//                        break;

                    case 1:
                    default:
                        break;
                }
            }

        }



//        By Sp

        if( activityRequest.getTypeId()!=null &&  activityRequest.getTypeId()>0 ){
            qry+=" AND estimate.estimate_type=:typeId ";
            sqlParam.addValue("typeId",activityRequest.getTypeId());
        }
        if( activityRequest.getWorkType()!=null && activityRequest.getWorkType()>0  ){
            qry+=" AND estimate.work_type=:workTypeId ";
            sqlParam.addValue("workTypeId",activityRequest.getWorkType());
        }
        if(activityRequest.getProcurementType()!=null && activityRequest.getProcurementType()>0 ){
            qry+=" AND estimate.procurement_type=:procurementType ";
            sqlParam.addValue("procurementType",activityRequest.getProcurementType());
        }
        if(activityRequest.getApprovalStatus()!=null && activityRequest.getApprovalStatus()>0 ){
            qry+=" AND estimate.approved_status=:approveStatus ";
            sqlParam.addValue("approveStatus",activityRequest.getApprovalStatus());
        }
        if( activityRequest.getActivityId()!=null && activityRequest.getActivityId()>0 ){
            qry+=" AND estimate.activity_id=:activityId ";
            sqlParam.addValue("activityId",activityRequest.getActivityId());
        }
        if( activityRequest.getContractId()!=null && activityRequest.getContractId()>0 ){
            qry+=" AND cm.contract_id=:contractId ";
            sqlParam.addValue("contractId",activityRequest.getContractId());
        }
        if(activityRequest.getDistId() !=null && activityRequest.getDistId()>0 ){
            qry+=" AND tm.dist_id =:distId ";
            sqlParam.addValue("distId",activityRequest.getDistId());
        }
        if(activityRequest.getTankId() !=null && activityRequest.getTankId()>0 ){
            qry+=" AND tm.tank_id =:tankId ";
            sqlParam.addValue("tankId",activityRequest.getTankId());
        }
        if(activityRequest.getExpenditureId() !=null && activityRequest.getExpenditureId()>0 && estimateIds ==null ){
            qry+=" AND em.expenditure_id =:expenditureId ";
            sqlParam.addValue("expenditureId",activityRequest.getExpenditureId());
        }
        if(estimateIds !=null && estimateIds.size() > 0 ){
            qry+=" AND estimate.id in (:estimateIds) ";
            sqlParam.addValue("estimateIds",estimateIds);
        }

        if(activityRequest.getInvoiceId() !=null && activityRequest.getInvoiceId()>0 ){
            qry+=" AND em.invoice_id =:invoiceId ";
            sqlParam.addValue("invoiceId",activityRequest.getInvoiceId());
        }

//        if(activityRequest.getActivityId()==-1){
//            qry+=" AND estimate.activity_id=:activityId ";
//            sqlParam.addValue("activityId",activityRequest.getActivityId());
//        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (activityRequest.getFromDate() != null && !activityRequest.getFromDate().isEmpty()) {
            qry+= " AND date(estimate.created_on) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(activityRequest.getFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (activityRequest.getToDate() != null && !activityRequest.getToDate().isEmpty()) {
            qry+= " AND date(estimate.created_on) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(activityRequest.getToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }


        qry += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(qry, sqlParam);
        qry += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ActivityEstimateListingResponseDto>activityEstimateInfo = namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimateListingResponseDto.class));
        return new PageImpl<>(activityEstimateInfo,pageable,resultCount);

        //return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimateListingResponseDto.class));
    }
    public Integer getTechnicalSanctionNo(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select last_value from oiipcra_oltp.activity_estimate_mapping_id_seq ";

        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }
    public ActivityEstimateResponseDto getActivityEstimateByID(Integer estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT  estimate.id as  estimateId,estimate.activity_id,estimate.level_id,estimate.status_id,estimate.work_type," +
//                "estimate.approval_order,estimate.name_of_work as workName,estimate.technical_sanction_no," +
//                "estimate.project_id,estimate.procurement_type,estimate.district_zone_identification,estimate.nol_of_tor_by_wb," +
//                "estimate.approval_ref,estimate.correspondance_file_no,estimate.period_of_completion,estimate.start_date,estimate.end_date," +
//                "estimate.estimated_amount as estimateAmount,estimate.approved_status,estimate.approved_by,estimate.is_active,estimate.approval_date," +
//                "activity.name as activityName,level.level_name as levelName,estimateType.name as estimateType,estimate.estimate_type as estimateTypeId," +
//                "work.name as workTypeName,procurement.name as procurement,approve.name as approval,estimate.created_by as createdBy," +
//                "estimate.document_name as docName,estimate.document_path as docPath,estimate.status_id as statusId,status.name as status " +
//                "FROM oiipcra_oltp.activity_estimate_mapping as estimate " +
//                "left join oiipcra_oltp.master_head_details as activity on activity.id=estimate.activity_id " +
//                "left join  oiipcra_oltp.activity_level_master as level on level.id=estimate.level_id " +
//                "left join oiipcra_oltp.estimate_type as estimateType on estimateType.id=estimate.estimate_type " +
//                "left join oiipcra_oltp.activity_status as status on status.id=estimate.status_id " +
//                "left join oiipcra_oltp.work_type_m as work on work.id=estimate.work_type " +
//                "left join oiipcra_oltp.procurement_type_m as procurement on procurement.id=estimate.procurement_type " +
//                "left join oiipcra_oltp.approval_status_m as approve on approve.id=estimate.approved_status " +
//                "where estimate.is_active=true AND estimate.id=:id ";

        String qry = "SELECT  estimate.id as  estimateId,estimate.activity_id,estimate.level_id,estimate.status_id,estimate.work_type,  " +
                "estimate.approval_order,estimate.name_of_work as workName,estimate.technical_sanction_no,  " +
                "estimate.project_id,estimate.procurement_type,estimate.district_zone_identification,estimate.nol_of_tor_by_wb,  " +
                "estimate.approval_ref,estimate.correspondance_file_no,estimate.period_of_completion,estimate.start_date,estimate.end_date,  " +
                "estimate.estimated_amount as estimateAmount,estimate.approved_status,estimate.approved_by,estimate.is_active,estimate.approval_date,  " +
                "activity.name as activityName,level.level_name as levelName,estimateType.name as estimateType,estimate.estimate_type as estimateTypeId,  " +
                "work.name as workTypeName,procurement.name as procurement,approve.name as approval,estimate.created_by as createdBy,  " +
                "estimate.document_name as docName,estimate.document_path as docPath,estimate.status_id as statusId,status.name as status,   " +
                "estimate.review_type,rvt.name as reviewTypeName, estimate.market_approach, mp.name as marketApproachName,estimate.loan_credit_no,  " +
                "estimate.procurement_document_type,pd.name as documentType,estimate.high_sea_sh_risk,estimate.procurement_process,pp.name as procurementTypeName,  " +
                "estimate.evaluation_options,ev.name as evaluationOptionName " +
                "FROM oiipcra_oltp.activity_estimate_mapping as estimate  " +
                "left join oiipcra_oltp.master_head_details as activity on activity.id=estimate.activity_id  " +
                "left join  oiipcra_oltp.activity_level_master as level on level.id=estimate.level_id   " +
                "left join oiipcra_oltp.estimate_type as estimateType on estimateType.id=estimate.estimate_type   " +
                "left join oiipcra_oltp.activity_status as status on status.id=estimate.status_id   " +
                "left join oiipcra_oltp.work_type_m as work on work.id=estimate.work_type   " +
                "left join oiipcra_oltp.procurement_type_m as procurement on procurement.id=estimate.procurement_type   " +
                "left join oiipcra_oltp.approval_status_m as approve on approve.id=estimate.approved_status   " +
                "left join oiipcra_oltp.review_type as rvt on rvt.id = estimate.review_type  " +
                "left join oiipcra_oltp.market_approach as mp on mp.id =estimate.market_approach  " +
                "left join oiipcra_oltp.procurement_document_type as pd on pd.id =estimate.procurement_document_type  " +
                "left join oiipcra_oltp.procurement_process as pp on pp.id = estimate.procurement_process  " +
                "left join oiipcra_oltp.evaluation_options as ev on ev.id= estimate.evaluation_options   " +
                "where estimate.is_active=true AND estimate.id=:id  ";
        sqlParam.addValue("id",estimateId);

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimateResponseDto.class));
    }
    public List<ActivityEstimateTankMappingDto> getActivityEstimateTankById(Integer estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select estimate.id as id,estimate.estimate_id as estimateId,district.dist_id as districtId,\n" +
                "district.district_name as districtName,block.block_id as blockId,block.block_name as blockName,tankM.id as tankId ,\n" +
                "tankM.name_of_the_m_i_p as tankName ,tankM.project_id  as projectId,estimate.division_id as divisionId,division.mi_division_name as divisionName,estimate.tank_wise_amount as tankWiseAmount\n" +
                "from oiipcra_oltp.activity_estimate_tank_mapping as estimate \n" +
                " left join oiipcra_oltp.district_boundary as district on district.dist_id=estimate.dist_id \n" +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=estimate.block_id \n" +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=estimate.division_id\n" +
                "left join oiipcra_oltp.tank_m_id as tankM on tankM.id=estimate.tank_id where estimate.estimate_id=:estimateId AND estimate.is_active=true";

        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimateTankMappingDto.class));
    }

    public Boolean deactivatePipDetailsByPipMappingId(Integer pipMappingId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE pip_details SET is_active = false WHERE pip_mapping_id =:pipMappingId";
        sqlParam.addValue("pipMappingId",pipMappingId);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }

    public String getCode(Integer parentId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT code FROM oiipcra_oltp.master_head_details WHERE parent_id =:parentId ORDER BY code DESC LIMIT 1";
        sqlParam.addValue("parentId",parentId);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public List<Integer> getAllComponentIds(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id FROM oiipcra_oltp.master_head_details WHERE parent_id = 0;";
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<ReviewTypeDto> getAllReviewType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,name from oiipcra_oltp.review_type ";
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(ReviewTypeDto.class));
    }

    public List<MarketApproachDto> getAllMarketApproach() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,name from oiipcra_oltp.market_approach  ";
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(MarketApproachDto.class));
    }

    public List<ProcurementDocumentTypeDto> getAllProcurementDocumentType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,name from oiipcra_oltp.procurement_document_type ";
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(ProcurementDocumentTypeDto.class));
    }

    public List<EvaluationOptionsDto> getAllEvaluationOptions() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,name from oiipcra_oltp.evaluation_options ";
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(EvaluationOptionsDto.class));
    }

    public List<ProcurementProcessDto> getAllProcurementProcess() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,name from oiipcra_oltp.procurement_process ";
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(ProcurementProcessDto.class));
    }

   public List<Integer> getEstimateByExpdr(Integer expenditureId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select estimate_id from oiipcra_oltp.expenditure_mapping where expenditure_id in (:expenditureId) ";
       sqlParam.addValue("expenditureId", expenditureId);
       return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

     public Integer getDistinctActivityId(Integer estimateId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT Distinct activity_id from oiipcra_oltp.activity_estimate_mapping where id=:estimateId and is_active=true ";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForObject(qry,sqlParam,Integer.class);

    }
    public Double getEstimateValueTankWiseSum(Integer estimateId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select sum(tank_wise_amount) from oiipcra_oltp.activity_estimate_tank_mapping where estimate_id=:estimateId and is_active=true ";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForObject(qry,sqlParam,Double.class);

    }
    public AdaptFinancialDto getAdaptFinancialDataByTerminalIds(List<Integer> terminalIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT count(id) as count, case when sum(financial_allocation_in_app) is null then 0.00 else sum(financial_allocation_in_app) end  as financialAllocationInApp, sum(actual_fund_allocated) as actualFundAllocated, case when sum(expenditure) is null then 0.00 else  sum(expenditure) end  as expenditure" +
                "\tFROM oiipcra_oltp.denormalized_financial_achievement ";
        if(terminalIds!=null){
            qry+=" where activity_id in (:activityIds) ";
        }
        sqlParam.addValue("activityIds",terminalIds);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
        }catch(Exception e){
            return null;
        }
    }
}
