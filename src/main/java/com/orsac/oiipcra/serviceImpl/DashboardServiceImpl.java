package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.ActivityUpperHierarchyInfo;
import com.orsac.oiipcra.dto.*;

import com.orsac.oiipcra.entities.DenormalizedAchievement;
import com.orsac.oiipcra.repository.ActivityQryRepository;
import com.orsac.oiipcra.repository.DashboardQryRepository;
import com.orsac.oiipcra.repository.MasterQryRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.repositoryImpl.DashboardRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.MasterRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.service.ActivityService;
import com.orsac.oiipcra.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ActivityQryRepository activityQryRepository;
    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;
    @Autowired
    private MasterServiceImpl masterServiceImpl;
    @Autowired
    private MasterRepositoryImpl masterRepositoryImpl;
    @Autowired
    private DashboardRepositoryImpl dashboardRepositoryImpl;

    @Autowired
    private DashboardQryRepository dashboardQryRepository;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;
    @Autowired
    private UserQueryRepository userQryRepo;

    // TODO: 22-08-2022 Values are static
    @Override
    public List<DashboardStatusOfMIP> getCountForExecutionOfMip() {
        List<DashboardStatusOfMIP> dashboardStatusOfMIP1 = new ArrayList<>();
        for (int i = 0; i < 8; i++) {

            DashboardStatusOfMIP dashboardStatusOfMIP = new DashboardStatusOfMIP();
            if (i == 0) {
                dashboardStatusOfMIP.setName("Taken Up");
                dashboardStatusOfMIP.setValue(20);
            }
            if (i == 1) {
                dashboardStatusOfMIP.setName("Completed");
                dashboardStatusOfMIP.setValue(20);
            }
            if (i == 2) {
                dashboardStatusOfMIP.setName("Progress");
                dashboardStatusOfMIP.setValue(420);
            }
            if (i == 3) {
                dashboardStatusOfMIP.setName("Agreement Awaited");
                dashboardStatusOfMIP.setValue(5420);
            }
            if (i == 4) {
                dashboardStatusOfMIP.setName("Tender Scrutiny");
                dashboardStatusOfMIP.setValue(620);
            }
            if (i == 5) {
                dashboardStatusOfMIP.setName("Tender Invited");
                dashboardStatusOfMIP.setValue(202);
            }
            if (i == 6) {
                dashboardStatusOfMIP.setName("Estimate Approved");
                dashboardStatusOfMIP.setValue(2330);
            }
            if (i == 7) {
                dashboardStatusOfMIP.setName("To Be Estimated");
                dashboardStatusOfMIP.setValue(30);
            }
            dashboardStatusOfMIP1.add(dashboardStatusOfMIP);
        }
        return dashboardStatusOfMIP1;
    }

    @Override
    public List<DashboardComponentEstExp> getComponentListEstExpList() {
        List<Integer> allComponentIds = activityQryRepository.getAllComponentIds();
        List<Integer> activityIdsByComponentId = new ArrayList<>();
        List<Integer> estimateIdsByActivityId = new ArrayList<>();
        List<Integer> tenderIdsByEstimateId = new ArrayList<>();
        List<Integer> contractIdsByTenderId = new ArrayList<>();
        List<DashboardComponentEstExp> finalDashboardComponentExp = new ArrayList<>();

        for (Integer componentId : allComponentIds) {
            DashboardComponentEstExp dashboardComponentEstExp = new DashboardComponentEstExp();
            List<Integer> finalEstimateIds = new ArrayList<>();
            List<Integer> finalTenderIds = new ArrayList<>();
            List<Integer> finalContractIds = new ArrayList<>();
            activityIdsByComponentId = activityQryRepository.getTerminalId(componentId);
            String componentNameDesc = dashboardQryRepository.getComponentNameDesc(componentId);

            //Getting Information Per Activity Id
            if (activityIdsByComponentId.size() > 0) {
                for (Integer activityId : activityIdsByComponentId) {
                    estimateIdsByActivityId = surveyRepositoy.getEstimatesByActivityId(activityId);
                    if (estimateIdsByActivityId.size() > 0) {
                        tenderIdsByEstimateId = surveyRepositoy.getTendersByEstimateIds(estimateIdsByActivityId);
                        finalTenderIds.addAll(tenderIdsByEstimateId);
                        if (tenderIdsByEstimateId.size() > 0) {
                            contractIdsByTenderId = surveyRepositoy.getContractIdsByTenderIds(tenderIdsByEstimateId);
                            finalContractIds.addAll(contractIdsByTenderId);
                        }

                    }
                    finalEstimateIds.addAll(estimateIdsByActivityId);
                }
            }
            if (activityIdsByComponentId.size() > 0) {
                Double estimateAmount = dashboardQryRepository.getEstimateAmount(activityIdsByComponentId, finalEstimateIds);
                Double expenditureAmount = dashboardQryRepository.getExpenditureAmount(activityIdsByComponentId, finalEstimateIds, finalContractIds);
                dashboardComponentEstExp.setEstimated(estimateAmount);
                dashboardComponentEstExp.setExpenditure(expenditureAmount);
            }
            dashboardComponentEstExp.setComponentName(componentNameDesc);
            finalDashboardComponentExp.add(dashboardComponentEstExp);
        }

        return finalDashboardComponentExp;
    }


    @Override
    public Double getExpenditureByPreviousMonth(Integer tankId) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormat.format(new Date());
        Date date = dateFormat.parse(currentDateTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int previousMonth = cal.get(Calendar.MONTH);
        Integer year = dashboardRepositoryImpl.getFinancialYear(currentDateTime);
        Double amount = dashboardRepositoryImpl.getExpenditureByPreviousMonth(year, previousMonth, tankId);
        return amount;
    }

    @Override
    public Double getExpenditureThisMonth(Integer tankId) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormat.format(new Date());
        Date date = dateFormat.parse(currentDateTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int previousMonth = cal.get(Calendar.MONTH);
        int month = previousMonth + 1;
        Integer year = dashboardRepositoryImpl.getFinancialYear(currentDateTime);
        Double amount = dashboardRepositoryImpl.getExpenditureThisMonth(year, month, tankId);
        return amount;
    }

    // TODO: 22-08-2022 Static Value Like Completed, Progress, Tender Scrutiny, Tender Invited need to be dynamic
    @Override
    public List<DashboardStatusOfMIPByDist> getDashboardStatusOfMIPByDist(Integer districtId) {
        List<DashboardStatusOfMIPByDist> finalListForSatusOfMip = new ArrayList<>();
        List<DashboardStatusOfMIPByDist> mipStatusEstimateApprovedByDist = dashboardQryRepository.getMIPStatusDistEstimateApproved(districtId);

        //Repository calls for Agreement Awaited
        List<Integer> tenderIdsStatusCompleted = dashboardQryRepository.getTenderIdsStatusCompleted();
        List<Integer> agreementAwaited = new ArrayList<>();
        if (tenderIdsStatusCompleted.size() > 0) {
            agreementAwaited = dashboardQryRepository.getAgreementAwaited(tenderIdsStatusCompleted, districtId);
        }
        //Repository calls for Taken Up
        List<DashboardStatusEstimateIdsByDist> getEstimateIdsByDist = dashboardQryRepository.getEstimateIdsByDist(districtId);

        List<DashboardStatusToBeEstimatedByDist> toBeEstimated = dashboardQryRepository.getToBeEstimated(districtId);
        for (int i = 0; i < mipStatusEstimateApprovedByDist.size(); i++) {
            DashboardStatusOfMIPByDist dashboardStatusOfMIPByDistDto = new DashboardStatusOfMIPByDist();
            dashboardStatusOfMIPByDistDto.setDistId(mipStatusEstimateApprovedByDist.get(i).getDistId());
            dashboardStatusOfMIPByDistDto.setDistName(mipStatusEstimateApprovedByDist.get(i).getDistName());
            dashboardStatusOfMIPByDistDto.setCompleted(60);
            dashboardStatusOfMIPByDistDto.setProgress(46);
            if (agreementAwaited.size() == 0) {
                dashboardStatusOfMIPByDistDto.setAgrmtAwaited(0);
            } else {
                dashboardStatusOfMIPByDistDto.setAgrmtAwaited(agreementAwaited.get(i));
            }
            dashboardStatusOfMIPByDistDto.setTenderScrutiny(48);
            dashboardStatusOfMIPByDistDto.setTenderInvited(23);
            dashboardStatusOfMIPByDistDto.setEstimateApproved(mipStatusEstimateApprovedByDist.get(i).getEstimateApproved());
            if (mipStatusEstimateApprovedByDist.get(i).getDistId() == toBeEstimated.get(i).getDistId()) {
                dashboardStatusOfMIPByDistDto.setToBeEstimated(toBeEstimated.get(i).getToBeEstimated());
            }
            int count = 0;
            for (int j = 0; j < getEstimateIdsByDist.size(); j++) {
                if (mipStatusEstimateApprovedByDist.get(i).getDistId() == getEstimateIdsByDist.get(j).getDistId()) {
                    if (getEstimateIdsByDist.get(j).getEstimateIds() != null) {
                        int countByContract = dashboardQryRepository.getTankIdCountFromContract(getEstimateIdsByDist.get(j).getEstimateIds());
                        count = count + countByContract;
                        int countByExpenditure = dashboardQryRepository.getTankIdCountFromExpenditure(getEstimateIdsByDist.get(j).getEstimateIds());
                        count = count + countByExpenditure;
                        int countByTender = dashboardQryRepository.getTankIdCountFromTender(getEstimateIdsByDist.get(j).getEstimateIds());
                        count = count + countByTender;
                    }
                }
            }
            dashboardStatusOfMIPByDistDto.setTakenUp(count);
            finalListForSatusOfMip.add(dashboardStatusOfMIPByDistDto);
        }
        return finalListForSatusOfMip;
    }

    // TODO: 23-08-2022 Values are static need to make it dynamic except takenUp
    @Override
    public List<DashboardStatusOfMIPByDivision> getDashboardStatusOfMIPByDivision(int districtId, int divisionId) {
        List<DashboardStatusOfMIPByDivision> finalListForSatusOfMipDivisionWise = new ArrayList<>();
        List<DashboardStatusOfMIPByDivision> mipStatusDivision = dashboardQryRepository.getMIPStatusDivision(districtId, divisionId);
        for (int i = 0; i < mipStatusDivision.size(); i++) {
            DashboardStatusOfMIPByDivision dashboardStatusOfMIPByDivisionDto = new DashboardStatusOfMIPByDivision();
            dashboardStatusOfMIPByDivisionDto.setDivisionId(mipStatusDivision.get(i).getDivisionId());
            dashboardStatusOfMIPByDivisionDto.setDivisionName(mipStatusDivision.get(i).getDivisionName());
            dashboardStatusOfMIPByDivisionDto.setTakenUp(mipStatusDivision.get(i).getTakenUp());
            dashboardStatusOfMIPByDivisionDto.setCompleted(60);
            dashboardStatusOfMIPByDivisionDto.setProgress(46);
            dashboardStatusOfMIPByDivisionDto.setAgrmtAwaited(343);
            dashboardStatusOfMIPByDivisionDto.setTenderScrutiny(48);
            dashboardStatusOfMIPByDivisionDto.setTenderInvited(23);
            dashboardStatusOfMIPByDivisionDto.setEstimateApproved(63);
            dashboardStatusOfMIPByDivisionDto.setToBeEstimated(78);
            finalListForSatusOfMipDivisionWise.add(dashboardStatusOfMIPByDivisionDto);
        }
        return finalListForSatusOfMipDivisionWise;
    }

    @Override
    public List<DashboardExpenditureOfMIPByDistrict> getDashboardExpenditureInMIPByDistrict(int districtId) {
        List<DashboardExpenditureOfMIPByDistrict> finalListForExpenditureInMIPDistrictWise = new ArrayList<>();
        List<DashboardExpenditureOfMIPByDistrict> mipExpenditureDist = dashboardQryRepository.getMIPExpenditureDistrict(districtId);
        for (int i = 0; i < mipExpenditureDist.size(); i++) {
            DashboardExpenditureOfMIPByDistrict dashboardExpenditureOfMIPByDistrict = new DashboardExpenditureOfMIPByDistrict();
            dashboardExpenditureOfMIPByDistrict.setDistId(mipExpenditureDist.get(i).getDistId());
            dashboardExpenditureOfMIPByDistrict.setDistName(mipExpenditureDist.get(i).getDistName());
            dashboardExpenditureOfMIPByDistrict.setEstimatedCost(mipExpenditureDist.get(i).getEstimatedCost());
            dashboardExpenditureOfMIPByDistrict.setAgrmtCost(2632.00);
            dashboardExpenditureOfMIPByDistrict.setExpByPrevMonth(2232.00);
            dashboardExpenditureOfMIPByDistrict.setExpCurrentMonth(1232.00);
            dashboardExpenditureOfMIPByDistrict.setCumExp(6335.4);
            finalListForExpenditureInMIPDistrictWise.add(dashboardExpenditureOfMIPByDistrict);
        }
        return finalListForExpenditureInMIPDistrictWise;
    }

    @Override
    public List<DashboardExpenditureOfMIPByDivision> getDashboardExpenditureInMIPByDivision(int districtId, int divisionId) {
        List<DashboardExpenditureOfMIPByDivision> finalListForExpenditureInMIPDivisionWise = new ArrayList<>();
        List<DashboardExpenditureOfMIPByDivision> mipExpenditureDivision = dashboardQryRepository.getMIPExpenditureDivision(districtId, divisionId);
        for (int i = 0; i < mipExpenditureDivision.size(); i++) {
            DashboardExpenditureOfMIPByDivision dashboardExpenditureOfMIPByDivision = new DashboardExpenditureOfMIPByDivision();
            dashboardExpenditureOfMIPByDivision.setDivisionId(mipExpenditureDivision.get(i).getDivisionId());
            dashboardExpenditureOfMIPByDivision.setDivisionName(mipExpenditureDivision.get(i).getDivisionName());
            dashboardExpenditureOfMIPByDivision.setEstimatedCost(mipExpenditureDivision.get(i).getEstimatedCost());
            dashboardExpenditureOfMIPByDivision.setAgrmtCost(5454.4);
            dashboardExpenditureOfMIPByDivision.setExpByPrevMonth(7454.4);
            dashboardExpenditureOfMIPByDivision.setExpCurrentMonth(6655.4);
            dashboardExpenditureOfMIPByDivision.setCumExp(4543.5);
            finalListForExpenditureInMIPDivisionWise.add(dashboardExpenditureOfMIPByDivision);
        }
        return finalListForExpenditureInMIPDivisionWise;
    }


    @Override
    public List<CivilWorkStatusDivisionDto> getCivilWorkStatusDivisionWise() {
        List<CivilWorkStatusDivisionDto> civilStatus = new ArrayList<>();
      /*  for(int i=0;i<2;i++) {
            CivilWorkStatusDivisionDto cd = new CivilWorkStatusDivisionDto();
            if (i == 0) {
                cd.setDivisionName("Anandapur");
                cd.setNoOfTanks(37);
                cd.setCcaInHa(9754.00);
                cd.setWorksTakenInTanks(14);
                cd.setContractAmount(2455.76);
                cd.setWorksCompleted(0);
                cd.setUpToDateExpenditure(426.44);
                cd.setNoOfTanksOngoing(14);
                cd.setBalanceContractValue(2029.32);
                cd.setNoOfTanksDropped(5);
                cd.setNoOfTankTakenUp(18);
                cd.setTotalEstimateCost(15660.56);
                cd.setBalanceWorkForContract(13204.80);
                civilStatus.add(cd);
            }
            if (i == 1) {
                cd.setDivisionName("Balangir");
                cd.setNoOfTanks(21);
                cd.setCcaInHa(4809.00 );
                cd.setWorksTakenInTanks(9);
                cd.setContractAmount( 2374.49 );
                cd.setWorksCompleted(0);
                cd.setUpToDateExpenditure(771.90 );
                cd.setNoOfTanksOngoing(9);
                cd.setBalanceContractValue(1602.59);
                cd.setNoOfTanksDropped(1);
                cd.setNoOfTankTakenUp(11);
                cd.setTotalEstimateCost( 5688.29 );
                cd.setBalanceWorkForContract(3313.80);
                civilStatus.add(cd);
            }
        }*/
        civilStatus = dashboardQryRepository.getCivilWorkStatusDivisionWise();
        for (int i = 0; i < civilStatus.size(); i++) {
            civilStatus.get(i).setNoOfTanksOngoing(civilStatus.get(i).getWorksTakenInTanks() - civilStatus.get(i).getWorksCompleted());
            // civilStatus.get(i).setBalanceContractValue(civilStatus.get(i).getContractAmount()-civilStatus.get(i).getUpToDateExpenditure());
            civilStatus.get(i).setNoOfTankTakenUp(civilStatus.get(i).getNoOfTanks() - civilStatus.get(i).getWorksTakenInTanks() - civilStatus.get(i).getNoOfTanksDropped());
//             civilStatus.get(i).setTotalEstimateCost(0.00);
//            civilStatus.get(i).setBalanceWorkForContract(0.00-civilStatus.get(i).getContractAmount());
        }
        return civilStatus;
    }

    @Override
    public List<CropCycleAyacutDto> getCropCycleAyacut(Integer projectId, String year) {
       /* Ayacut2 ayacut2=new Ayacut2();
       List<AyacutDto> aya=new ArrayList<>();
    // List<List<CropCycleAyacutDto>> crop=new ArrayList<>();
       for(int i=0;i<year.size();i++){
          AyacutDto  ayacut=new AyacutDto();*/
        List<CropCycleAyacutDto> civilStatus = dashboardQryRepository.getCropCycleAyacut(projectId, year);
           /*ayacut.setAyacut(civilStatus);
           aya.add(ayacut);
        }
       ayacut2.setAyacut(aya);*/

        return civilStatus;
    }

    @Override
    public List<CropCycleAyacutDto> getCropCycleAyacut1(Integer projectId, List<String> year) {
        List<CropCycleAyacutDto> civilStatus = dashboardQryRepository.getCropCycleAyacut1(projectId, year);

        return civilStatus;
    }

    @Override
    public List<CivilWorkDto> getCivilWorkStatus(Integer typeId, Integer yearId) {
        List<CivilWorkDto> civil = new ArrayList<>();
        List<CivilWorkDto> allDivision = dashboardRepositoryImpl.getAllDivision();

        if (typeId == 2 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountValue(2, "TOTAL NO OF TANKS", yearId));

//            for (CivilWorkDto item : allDivision) {
//                //Integer getCountValue=;
//                CivilWorkDto c = new CivilWorkDto();
//                c.setTypeId(2);
//                c.setTypeName("TOTAL NO OF TANKS");
//                c.setDivisionName(item.getDivisionName());
//                c.setDivisionId(item.getDivisionId());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValue(item.getDivisionId())));
//                civil.add(c);
//
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(2);
//                c.setTypeName("TOTAL NO OF TANKS");
//                if(i==0){
//                  c.setDivisionName("Anandapur");
//                  c.setDivisionId(7);
//                  c.setValue("37");
//                  civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("21");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("19");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("68");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("89");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("10");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("3");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("47");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("114");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("39");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("36");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("12");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("3");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("24");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("6");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("5");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 3 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountValue(3, "CCA IN HA.", yearId));
//            for (CivilWorkDto item : allDivision) {
//                CivilWorkDto c = new CivilWorkDto();
//                c.setTypeId(3);
//                c.setTypeName("CCA IN HA.");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValueOfCcaInHa(item.getDivisionId())));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(3);
//                c.setTypeName("CCA IN HA.");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("9754.00");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("4809.00");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("1812.00");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("7405.00");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("6591.00");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("755.00");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("351.00");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("3893.00");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("7582.30");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("3921.00");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("3555.00");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("2249.00");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("125.00");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("2226.00");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("452.00");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("598.00");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue("86.00");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("94.00");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 4 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountValue(4, "WORKS TAKEN UP IN TANKS(NOS.)", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(4);
//                c.setTypeName("WORKS TAKEN UP IN TANKS(NOS.)");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValueOfWorksTakenUp(item.getDivisionId())));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(4);
//                c.setTypeName("WORKS TAKEN UP IN TANKS(NOS.)");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("14");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("9");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("7");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("18");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("25");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("9");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("16");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("32");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("9");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("8");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("6");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("3");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("7");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("5");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 5 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountValue(5, "CONTRACT AMOUNT", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(5);
//                c.setTypeName("CONTRACT AMOUNT");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValueOfContractAmount(item.getDivisionId())));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(5);
//                c.setTypeName("CONTRACT AMOUNT");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("24.55");
//                    civil.add(c);
//                }
//
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("23.74");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("7.72");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("36.83");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("27.04");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("8.55");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("3.89");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("23.83");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("35.07");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("15.80");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("7.70");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("6.42");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("1.86");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("6.77");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("6.14");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 6 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountValue(6, "WORKS IN TANKS COMPLETED", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(6);
//                c.setTypeName("WORKS IN TANKS COMPLETED");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValueOfWorksInTankCompleted(item.getDivisionId())));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(6);
//                c.setTypeName("WORKS IN TANKS COMPLETED");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("6");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("5");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("3");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("3");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("3");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 7 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountValue(7, "UP TO DATE EXPENDITURE", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(7);
//                c.setTypeName("UP TO DATE EXPENDITURE");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValueOfUptodateExpd(item.getDivisionId())));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(7);
//                c.setTypeName("UP TO DATE EXPENDITURE");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("4.26");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("7.71");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("2.26");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("8.38");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("15.29");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("2.83");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("1.62");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("7.94");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("18.04");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("2.47");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("5.60");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("4.27");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("1.74");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("1.75");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("5.11");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 8 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountBalTankOngoing(8, "BALANCE NO OF TANKS ONGOING"));

//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(8);
//                c.setTypeName("BALANCE NO OF TANKS ONGOING");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValueOfWorksTakenUp(item.getDivisionId()).intValue()-dashboardRepositoryImpl.getCountValueOfWorksInTankCompleted(item.getDivisionId()).intValue()));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(8);
//                c.setTypeName("BALANCE NO OF TANKS ONGOING");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("14");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("9");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("7");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("18");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("19");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("9");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("16");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("27");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("9");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("5");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("6");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("7");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 9 || typeId == 14) {
            DecimalFormat df = new DecimalFormat("0.00");
            civil.addAll(dashboardRepositoryImpl.getBalanceContractValue(9, "BALANCE CONTRACT VALUE", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(9);
//                c.setTypeName("BALANCE CONTRACT VALUE");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(df.format(dashboardRepositoryImpl.getCountValueOfContractAmount(item.getDivisionId())-dashboardRepositoryImpl.getCountValueOfUptodateExpd(item.getDivisionId()))));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(9);
//                c.setTypeName("BALANCE CONTRACT VALUE");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("20.29");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("16.02");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("5.45");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("28.44");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("11.74");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("5.72");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("2.27");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("15.89");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("17.03");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("13.32");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("2.09");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("2.15");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("0.11");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("5.01");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("1.02");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//
//            }

        }
        if (typeId == 10 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getCountValue(10, "NOS. OF TANKS PROPOSED TO BE DROPPED", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(10);
//                c.setTypeName("NOS. OF TANKS PROPOSED TO BE DROPPED");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValueTankDropped(item.getDivisionId())));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(10);
//                c.setTypeName("NOS. OF TANKS PROPOSED TO BE DROPPED");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("5");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("1");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("1");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("14");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("1");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("1");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("10");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("4");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("4");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("4");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("1");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("4");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 11 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getNoOfTankTakenUp(11, "NOS. OF TANKS  TO BE TAKEN UP"));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c = new CivilWorkDto();
//                c.setTypeId(11);
//                c.setTypeName("NOS. OF TANKS  TO BE TAKEN UP");
//                c.setDivisionId(item.getDivisionId() );
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getCountValue(item.getDivisionId()).intValue()-dashboardRepositoryImpl.getCountValueOfWorksTakenUp(item.getDivisionId()).intValue()-dashboardRepositoryImpl.getCountValueTankDropped(item.getDivisionId()).intValue()));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(11);
//                c.setTypeName("NOS. OF TANKS  TO BE TAKEN UP");
//                if(i==0){
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("18");
//                    civil.add(c);
//                }
//                if(i==1){
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("11");
//                    civil.add(c);
//                }
//                if(i==2){
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("10");
//                    civil.add(c);
//                }
//                if(i==3){
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("49");
//                    civil.add(c);
//                }
//                if(i==4){
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("50");
//                    civil.add(c);
//                }
//                if(i==5){
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("1");
//                    civil.add(c);
//                }
//                if(i==6){
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==7){
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("30");
//                    civil.add(c);
//                }
//                if(i==8){
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("72");
//                    civil.add(c);
//                }
//                if(i==9){
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("28");
//                    civil.add(c);
//                }
//                if(i==10){
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("24");
//                    civil.add(c);
//                }
//                if(i==11){
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//                if(i==12){
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==13){
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("13");
//                    civil.add(c);
//                }
//                if(i==14){
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==15){
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("1");
//                    civil.add(c);
//                }
//                if(i==16){
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue("0");
//                    civil.add(c);
//                }
//                if(i==17){
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("2");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 12 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getAppEst(12, "TOTAL APPROX ESTIMATE COST", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c = new CivilWorkDto();
//                c.setTypeId(12);
//                c.setTypeName("TOTAL APPROX ESTIMATE COST");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getAppEst(item.getDivisionId())));
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(12);
//                c.setTypeName("TOTAL APPROX ESTIMATE COST");
//                if (i == 0) {
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("156.60");
//                    civil.add(c);
//                }
//                if (i == 1) {
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("56.88");
//                    civil.add(c);
//                }
//                if (i == 2) {
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("25.97");
//                    civil.add(c);
//                }
//                if (i == 3) {
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("101.97");
//                    civil.add(c);
//                }
//                if (i == 4) {
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("84.59");
//                    civil.add(c);
//                }
//                if (i == 5) {
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("9.51");
//                    civil.add(c);
//                }
//                if (i == 6) {
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue("3.89");
//                    civil.add(c);
//                }
//                if (i == 7) {
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("49.37");
//                    civil.add(c);
//                }
//                if (i == 8) {
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("100.10");
//                    civil.add(c);
//                }
//                if (i == 9) {
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("58.35");
//                    civil.add(c);
//                }
//                if (i == 10) {
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("35.83");
//                    civil.add(c);
//                }
//                if (i == 11) {
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("29.00");
//                    civil.add(c);
//                }
//                if (i == 12) {
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue("1.86");
//                    civil.add(c);
//                }
//                if (i == 13) {
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("31.95");
//                    civil.add(c);
//                }
//                if (i == 14) {
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue("6.14");
//                    civil.add(c);
//                }
//                if (i == 15) {
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("3.02");
//                    civil.add(c);
//                }
//                if (i == 16) {
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if (i == 17) {
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("1.69");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 13 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getBalanceWorkForContract(13, "BALANCE WORK FOR CONTRACT", yearId));
//            for (CivilWorkDto item:allDivision) {
//                CivilWorkDto c = new CivilWorkDto();
//                c.setTypeId(13);
//                c.setTypeName("BALANCE WORK FOR CONTRACT");
//                c.setDivisionId(item.getDivisionId());
//                c.setDivisionName(item.getDivisionName());
//                c.setValue(String.valueOf(dashboardRepositoryImpl.getAppEst(item.getDivisionId()).doubleValue()-(dashboardRepositoryImpl.getCountValueOfContractAmount(item.getDivisionId()).doubleValue()) ) );
//                civil.add(c);
//            }
//            for(int i=0;i<18;i++){
//                CivilWorkDto c=new CivilWorkDto();
//                c.setTypeId(13);
//                c.setTypeName("BALANCE WORK FOR CONTRACT");
//                if (i == 0) {
//                    c.setDivisionName("Anandapur");
//                    c.setDivisionId(7);
//                    c.setValue("132.04");
//                    civil.add(c);
//                }
//                if (i == 1) {
//                    c.setDivisionName("Balangir");
//                    c.setDivisionId(1);
//                    c.setValue("33.13");
//                    civil.add(c);
//                }
//                if (i == 2) {
//                    c.setDivisionName("Balasore");
//                    c.setDivisionId(18);
//                    c.setValue("18.25");
//                    civil.add(c);
//                }
//                if (i == 3) {
//                    c.setDivisionName("Baripada");
//                    c.setDivisionId(11);
//                    c.setValue("65.14");
//                    civil.add(c);
//                }
//                if (i == 4) {
//                    c.setDivisionName("Bhanjanagar");
//                    c.setDivisionId(20);
//                    c.setValue("57.54");
//                    civil.add(c);
//                }
//                if (i == 5) {
//                    c.setDivisionName("Boudh");
//                    c.setDivisionId(3);
//                    c.setValue("0.95");
//                    civil.add(c);
//                }
//                if (i == 6) {
//
//                    c.setDivisionName("Gajpati");
//                    c.setDivisionId(4);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if (i == 7) {
//                    c.setDivisionName("Ganjam-I");
//                    c.setDivisionId(21);
//                    c.setValue("25.54");
//                    civil.add(c);
//                }
//                if (i == 8) {
//                    c.setDivisionName("Ganjam-II");
//                    c.setDivisionId(22);
//                    c.setValue("65.03");
//                    civil.add(c);
//                }
//                if (i == 9) {
//                    c.setDivisionName("Jashipur");
//                    c.setDivisionId(12);
//                    c.setValue("42.55");
//                    civil.add(c);
//                }
//                if (i == 10) {
//                    c.setDivisionName("Kalahandi");
//                    c.setDivisionId(24);
//                    c.setValue("28.13");
//                    civil.add(c);
//                }
//                if (i == 11) {
//
//                    c.setDivisionName("Keonjhar");
//                    c.setDivisionId(8);
//                    c.setValue("22.57");
//                    civil.add(c);
//                }
//                if (i == 12) {
//
//                    c.setDivisionName("Nawarangapur");
//                    c.setDivisionId(13);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if (i == 13) {
//
//                    c.setDivisionName("Padampur");
//                    c.setDivisionId(2);
//                    c.setValue("25.18");
//                    civil.add(c);
//                }
//                if (i == 14) {
//                    c.setDivisionName("Phulbani");
//                    c.setDivisionId(6);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if (i == 15) {
//
//                    c.setDivisionName("Jajpur");
//                    c.setDivisionId(23);
//                    c.setValue("3.02");
//                    civil.add(c);
//                }
//                if (i == 16) {
//
//                    c.setDivisionName("Kharial");
//                    c.setDivisionId(14);
//                    c.setValue(" ");
//                    civil.add(c);
//                }
//                if (i == 17) {
//
//                    c.setDivisionName("Sonepur");
//                    c.setDivisionId(17);
//                    c.setValue("1.69");
//                    civil.add(c);
//                }
//
//            }
        }
        if (typeId == 101 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getDivisionWiseTypeCount(101, "Res"));
//            CivilWorkDto c = new CivilWorkDto();
//            for (CivilWorkDto item : civilWorkDtoList) {
//                c.setTypeId(101);
//                c.setTypeName(item.getTypeName());
//                c.setDivisionName(item.getDivisionName());
//                c.setDivisionId(item.getDivisionId());
//                c.setValue(item.getValue());
//                civil.add(c);
//            }
        }
        if (typeId == 102 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getDivisionWiseTypeCount(102, "D/W"));
//            CivilWorkDto c = new CivilWorkDto();
//            for (CivilWorkDto item : civilWorkDtoList) {
//                c.setTypeId(102);
//                c.setTypeName(item.getTypeName());
//                c.setDivisionName(item.getDivisionName());
//                c.setDivisionId(item.getDivisionId());
//                c.setValue(item.getValue());
//                civil.add(c);
//            }
        }
        if (typeId == 103 || typeId == 14) {
            civil.addAll(dashboardRepositoryImpl.getDivisionWiseTypeCount(103, "Creek"));
//            CivilWorkDto c = new CivilWorkDto();
//            for (CivilWorkDto item : civilWorkDtoList) {
//                c.setTypeId(103);
//                c.setTypeName(item.getTypeName());
//                c.setDivisionName(item.getDivisionName());
//                c.setDivisionId(item.getDivisionId());
//                c.setValue(item.getValue());
//                civil.add(c);
//            }
        }


        return civil;
    }

    @Override
    public List<ValueDto> getValue(Integer yearId) {
        List<ValueDto> list = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            ValueDto valueDto = new ValueDto();
            if (i == 0) {
                //  valueDto.setValue("537");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(2, yearId).intValue()));
                valueDto.setTypeId(2);
                valueDto.setTypeName("Total No. of Tanks");
                list.add(valueDto);
            }
            if (i == 1) {
                //valueDto.setValue("56258.30");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(3, yearId)));
                valueDto.setTypeId(3);
                valueDto.setTypeName("CCA in Ha.");
                list.add(valueDto);
            }
            if (i == 2) {
                //valueDto.setValue("170");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(4, yearId).intValue()));
                valueDto.setTypeId(4);
                valueDto.setTypeName("Works taken up in tanks (Nos.)");
                list.add(valueDto);
            }
            if (i == 3) {
                //valueDto.setValue("235.97");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(5, yearId).doubleValue()));
                valueDto.setTypeId(5);
                valueDto.setTypeName("Contract Amount");
                list.add(valueDto);
            }
            if (i == 4) {
                //valueDto.setValue("20");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(6, yearId).intValue()));
                valueDto.setTypeId(6);
                valueDto.setTypeName("Works in tanks completed");
                list.add(valueDto);
            }
            if (i == 5) {
                //  valueDto.setValue("89.34");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(7, yearId)));
                valueDto.setTypeId(7);
                valueDto.setTypeName("Up to Date Expdr.");
                list.add(valueDto);
            }
            if (i == 6) {
                //   valueDto.setValue("150");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(4, yearId).intValue() - dashboardRepositoryImpl.getStateWiseValue(6, yearId).intValue()));
                valueDto.setTypeId(8);
                valueDto.setTypeName("Balance No. of Tanks on-going");
                list.add(valueDto);
            }
            if (i == 7) {
                DecimalFormat df = new DecimalFormat("0.00");
//                valueDto.setValue("146.62");
                valueDto.setValue(String.valueOf(df.format(dashboardRepositoryImpl.getStateWiseValue(5, yearId) - dashboardRepositoryImpl.getStateWiseValue(7, yearId))));
                valueDto.setTypeId(9);
                valueDto.setTypeName("Balance Contract value");
                list.add(valueDto);
            }

            if (i == 8) {
                //  valueDto.setValue("56");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(10, yearId).intValue()));
                valueDto.setTypeId(10);
                valueDto.setTypeName("No. of Tanks proposed to be dropped");
                list.add(valueDto);
            }
            if (i == 9) {
                //  valueDto.setValue("311");
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(2, yearId).intValue() - dashboardRepositoryImpl.getStateWiseValue(4, yearId).intValue() - dashboardRepositoryImpl.getStateWiseValue(10, yearId).intValue()));

                valueDto.setTypeId(11);
                valueDto.setTypeName("No. of Tanks to be takenup");
                list.add(valueDto);
            }
            if (i == 10) {
                valueDto.setValue(String.valueOf(dashboardRepositoryImpl.getStateWiseValue(12, yearId).doubleValue()));
                valueDto.setTypeId(12);
                valueDto.setTypeName("Total Approx Est. Cost");
                list.add(valueDto);
            }
            if (i == 11) {
                DecimalFormat df = new DecimalFormat("0.00");
                //  valueDto.setValue("520.81");
                BigDecimal d = BigDecimal.valueOf(dashboardRepositoryImpl.getStateWiseValue(12, yearId) - dashboardRepositoryImpl.getStateWiseValue(5, yearId));
                if (d.compareTo(BigDecimal.ZERO) < 0) {
                    d = new BigDecimal(0.00);
                }
                valueDto.setValue(String.valueOf(df.format(d)));
                valueDto.setTypeId(13);
                valueDto.setTypeName("Balance work for contract");
                list.add(valueDto);
            }
            if (i == 12) {
                List<ValueDto> valueDtoList = dashboardRepositoryImpl.getCountType();

                valueDto.setValue(valueDtoList.get(0).getValue());
                valueDto.setTypeId(101);
                valueDto.setTypeName(valueDtoList.get(0).getTypeName());
                list.add(valueDto);
            }
            if (i == 13) {
                List<ValueDto> valueDtoList = dashboardRepositoryImpl.getCountType();
                valueDto.setValue(valueDtoList.get(1).getValue());
                valueDto.setTypeId(102);
                valueDto.setTypeName(valueDtoList.get(1).getTypeName());
                list.add(valueDto);
            }
            if (i == 14) {
                List<ValueDto> valueDtoList = dashboardRepositoryImpl.getCountType();
                valueDto.setValue(valueDtoList.get(2).getValue());
                valueDto.setTypeId(103);
                valueDto.setTypeName(valueDtoList.get(2).getTypeName());
                list.add(valueDto);
            }


        }
        return list;
    }

    @Override
    public TankWiseCountDto getTankWiseCount(Integer tankId) {
        List<TankWiseCountDto> tankWiseCountDtos = new ArrayList<>();
        TankWiseCountDto tankWiseCountDto = new TankWiseCountDto();
        tankWiseCountDto.setEstimateAmount(dashboardRepositoryImpl.getEstimateCostByTankId(tankId));
        tankWiseCountDto.setExpenditureAmount(dashboardRepositoryImpl.getExpenditureByTankId(tankId));
        tankWiseCountDto.setContractAmount(dashboardRepositoryImpl.getContractAmountByTankId(tankId));
        return tankWiseCountDto;

    }

    @Override
    public BigDecimal getTankWiseCatchmentArea(Integer tankId) {
        return dashboardRepositoryImpl.getTankWiseCatchmentArea(tankId);
    }

    @Override
    public List<CropCycleAyacutDto> getTankWiseAyaCut(Integer tankId) {
        return dashboardRepositoryImpl.getTankWiseAyaCut(tankId);
    }

    @Override
    public Double getWSA(Integer tankId, String divisionName) {
        return dashboardRepositoryImpl.getWSA(tankId, divisionName);

    }

    @Override
    public String getWSAtillMonth(Integer projectId, String divisionName) {
        return dashboardRepositoryImpl.getWSAtillMonth(projectId, divisionName);
    }

    @Override
    public Object getExpenditureEstimateContractYearWise(Integer userId, Integer componentId) {
        List<FinYrDto> year = masterRepositoryImpl.getAllFinancialYear();
        //  List<ComponentDto> cmp=new ArrayList<>();
        year.forEach(
                item -> {
                    item.setExpenditureAmount(dashboardRepositoryImpl.getExpenditureByYear(item.getId(), componentId));
                    item.setContractAmount(dashboardRepositoryImpl.getContractAmountByYear(item.getId(), componentId));
                    BigDecimal b = dashboardRepositoryImpl.getContractAmountByYear(item.getId(), componentId).subtract(dashboardRepositoryImpl.getExpenditureByYear(item.getId(), componentId));
                    item.setBalanceContractAmount(b.compareTo(BigDecimal.ZERO) > 0 ? b : new BigDecimal(0.0));
                    item.setEstimateAmount(BigDecimal.valueOf(0.0));
                }
        );

        return year;
    }

    @Override
    public Object getTankSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getTankSurveyedCount(circleId, distId, divisionId, blockId);
    }

    @Override
    public Object getCadSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getCadSurveyedCount(circleId, distId, divisionId, blockId);
    }

    @Override
    public Object getFeederSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getFeederSurveyedCount(circleId, distId, divisionId, blockId);
    }

    @Override
    public Object getDepthCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getDepthCount(circleId, distId, divisionId, blockId);
    }

    @Override
    public Object getValueByComponentIdAndDistId(Integer componentId, Integer yearId, Integer distId, Integer typeId) {
        Map<String, Object> components = new HashMap<>();

        if (typeId == 1) {
            List<ComponentDto> cmp = dashboardRepositoryImpl.getcompontDetails(null);
            for (ComponentDto item : cmp) {
                if (item.getComponentId() == 1) {
                    AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item.getComponentId(), distId,yearId);
                    Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item.getComponentId(), distId,yearId);
                    Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId2(item.getComponentId(), yearId, null,distId);
                    Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item.getComponentId(), yearId,distId);
                    if (adaptData != null) {
                        if (adaptData.getFinancialAllocationInApp() != null) {
                            item.setAdaptFinancialAllocationInApp(adaptData.getFinancialAllocationInApp() + estimateCost);
                        } else {
                            item.setAdaptFinancialAllocationInApp(estimateCost);
                        }
                        if (adaptData.getActualFundAllocated() != null) {
                            item.setAdaptActualFundAllocated(adaptData.getActualFundAllocated() + estimateCost);
                        } else {
                            item.setAdaptActualFundAllocated(estimateCost);
                        }
                        if (adaptData.getExpenditure() != null) {
                            item.setAdaptExpenditure(adaptData.getExpenditure() + expenditureCost);
                        } else {
                            item.setAdaptExpenditure(expenditureCost);
                        }
                        item.setAdaptBeneficiaries(beneficiaries);
                        item.setNoOfContractComplete(dashboardRepositoryImpl.NoOfContractCompleteByComponentId(item.getComponentId(), yearId,distId));
                        item.setNoOfContractOnGoing(dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(item.getComponentId(), yearId,distId));
                        if (adaptData.getExpenditure() != null && adaptData.getActualFundAllocated() != null) {
                           item.setAdaptAchievementPercentage ((Double.valueOf(adaptData.getExpenditure()) * 100) / Double.valueOf(adaptData.getActualFundAllocated()));
                        }
                    }
                }

                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);

                components.put("component", cmp);
            }

        }

        if (typeId == 2) {
            List<ComponentDto> subComponent = dashboardRepositoryImpl.getSubcomponentDetails(componentId);

            for (ComponentDto item2 : subComponent) {
                if (componentId == 1) {
                    AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item2.getComponentId(), distId,yearId);
                    Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item2.getComponentId(), distId,yearId);
                    Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId2(item2.getComponentId(), yearId, null,distId);
                    Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item2.getComponentId(), yearId,distId);
                    if (adaptData != null) {
                        if (adaptData.getFinancialAllocationInApp() != null) {
                            item2.setAdaptFinancialAllocationInApp(adaptData.getFinancialAllocationInApp() + estimateCost);
                        } else {
                            item2.setAdaptFinancialAllocationInApp(estimateCost);
                        }
                        if (adaptData.getActualFundAllocated() != null) {
                            item2.setAdaptActualFundAllocated(adaptData.getActualFundAllocated() + estimateCost);
                        } else {
                            item2.setAdaptActualFundAllocated(estimateCost);
                        }
                        if (adaptData.getExpenditure() != null) {
                            item2.setAdaptExpenditure(adaptData.getExpenditure() + expenditureCost);
                        } else {
                            item2.setAdaptExpenditure(expenditureCost);
                        }
                        item2.setAdaptBeneficiaries(beneficiaries);
                        item2.setNoOfContractComplete(dashboardRepositoryImpl.NoOfContractCompleteByComponentId(item2.getComponentId(), yearId,distId));
                        item2.setNoOfContractOnGoing(dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(item2.getComponentId(),yearId,distId));
                        if (adaptData.getExpenditure() != null && adaptData.getActualFundAllocated() != null) {
                            item2.setAdaptAchievementPercentage ((Double.valueOf(adaptData.getExpenditure()) * 100) / Double.valueOf(adaptData.getActualFundAllocated()));
                        }
                    }
                }

                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item2.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item2.setCode(code);

                components.put("component", subComponent);
            }

        }


        if (typeId == 3) {
            List<ComponentDto> activity = dashboardRepositoryImpl.getActivityDetails(componentId);

            for (ComponentDto item3 : activity) {

                    AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item3.getComponentId(), distId,yearId);
                    Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item3.getComponentId(), distId,yearId);
                    Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId2(item3.getComponentId(), yearId, null,distId);
                    Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item3.getComponentId(), yearId,distId);
                    if (adaptData != null) {
                        if (adaptData.getFinancialAllocationInApp() != null) {
                            item3.setAdaptFinancialAllocationInApp(adaptData.getFinancialAllocationInApp() + estimateCost);
                        } else {
                            item3.setAdaptFinancialAllocationInApp(estimateCost);
                        }
                        if (adaptData.getActualFundAllocated() != null) {
                            item3.setAdaptActualFundAllocated(adaptData.getActualFundAllocated() + estimateCost);
                        } else {
                            item3.setAdaptActualFundAllocated(estimateCost);
                        }
                        if (adaptData.getExpenditure() != null) {
                            item3.setAdaptExpenditure(adaptData.getExpenditure() + expenditureCost);
                        } else {
                            item3.setAdaptExpenditure(expenditureCost);
                        }
                        item3.setAdaptBeneficiaries(beneficiaries);
                        item3.setNoOfContractComplete(dashboardRepositoryImpl.NoOfContractCompleteByComponentId(item3.getComponentId(), yearId,distId));
                        item3.setNoOfContractOnGoing(dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(item3.getComponentId(),yearId,distId));
                        if (adaptData.getExpenditure() != null && adaptData.getActualFundAllocated() != null) {
                            item3.setAdaptAchievementPercentage ((Double.valueOf(adaptData.getExpenditure()) * 100) / Double.valueOf(adaptData.getActualFundAllocated()));
                        }
                    }
                        item3.setNoOfEstimateApproved(dashboardRepositoryImpl.NoOfEstimateApprovedByComponentId(item3.getComponentId(),distId,yearId));
                        item3.setTotalApproxEstCost(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item3.getComponentId(), yearId,null,distId));

                        item3.setWorkWiseEstimateAmount(dashboardRepositoryImpl.estimateAmountByComponentIdForWork(item3.getComponentId(),yearId,1,distId));
                        item3.setConsultancyWiseEstimateAmount(dashboardRepositoryImpl.estimateAmountByComponentIdForConsultancy(item3.getComponentId(),yearId,2,distId));
                        item3.setGoodsWiseEstimateAmount(dashboardRepositoryImpl.estimateAmountByComponentIdForConsultancy(item3.getComponentId(), yearId,3,distId));


                        item3.setTotalCanalLength(dashboardRepositoryImpl.TotalCanalLengthByComponentId(item3.getComponentId(),distId));
                        item3.setCanalImproved(dashboardRepositoryImpl.CanalImprovedByComponentId(item3.getComponentId(),distId));

                        item3.setCdStructurePrepared(dashboardRepositoryImpl.CdStructurePreparedByComponentId(item3.getComponentId(),distId));
                        item3.setCdStructureToBePrepared(dashboardRepositoryImpl.CdStructureToBePreparedByComponentId(item3.getComponentId(),distId));

                        item3.setTotalCadLength(dashboardRepositoryImpl.TotalCadLengthByComponentId(item3.getComponentId(),distId));
                        item3.setCadConstructed(dashboardRepositoryImpl.CadConstructedByComponentId(item3.getComponentId(),distId));
                        item3.setNoOfOutletConstructed(dashboardRepositoryImpl.NoOfOutletConstructedByComponentId(item3.getComponentId(),distId));


                        item3.setNoOfContractComplete(dashboardRepositoryImpl.NoOfContractCompleteByComponentId(item3.getComponentId(), yearId,distId));
                        item3.setNoOfContractOnGoing(dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(item3.getComponentId(),yearId,distId));
                        item3.setContractAmount(dashboardRepositoryImpl.contractAmountByComponentId(item3.getComponentId(),yearId));
                        item3.setConsultancyWiseContractAmount(dashboardRepositoryImpl.contractAmountByComponentIdForConsultancy(item3.getComponentId(),yearId,2));
                        item3.setWorkWiseContractAmount(dashboardRepositoryImpl.contractAmountByComponentIdForWork(item3.getComponentId(), yearId,1));
                        item3.setGoodsWiseContractAmount(dashboardRepositoryImpl.contractAmountByComponentIdForWork(item3.getComponentId(),yearId,3));

                        item3.setUpToDateExpenditure(dashboardRepositoryImpl.UPtoDateExpenditureByComponentId(item3.getComponentId(),yearId,distId));
                        item3.setGoodsWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentId(item3.getComponentId(), yearId,3));
                        item3.setWorkWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentId(item3.getComponentId(), yearId,1));
                        item3.setConsultancyWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentId(item3.getComponentId(),yearId,2));
                        item3.setAgricultureWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentIdForAgriculture(item3.getComponentId(), yearId,4,null));



                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item3.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item3.setCode(code);


            }
            components.put("component", activity);
        }

        if (typeId == 4) {
            List<ComponentDto> subActivity = dashboardRepositoryImpl.getsubActivityDetails(componentId);

            for (ComponentDto item4 : subActivity) {

                AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item4.getComponentId(), distId,yearId);
                Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item4.getComponentId(), distId,yearId);
                Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId2(item4.getComponentId(), yearId, null,distId);
                Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item4.getComponentId(), yearId,distId);
                if (adaptData != null) {
                    if (adaptData.getFinancialAllocationInApp() != null) {
                        item4.setAdaptFinancialAllocationInApp(adaptData.getFinancialAllocationInApp() + estimateCost);
                    } else {
                        item4.setAdaptFinancialAllocationInApp(estimateCost);
                    }
                    if (adaptData.getActualFundAllocated() != null) {
                        item4.setAdaptActualFundAllocated(adaptData.getActualFundAllocated() + estimateCost);
                    } else {
                        item4.setAdaptActualFundAllocated(estimateCost);
                    }
                    if (adaptData.getExpenditure() != null) {
                        item4.setAdaptExpenditure(adaptData.getExpenditure() + expenditureCost);
                    } else {
                        item4.setAdaptExpenditure(expenditureCost);
                    }
                    item4.setAdaptBeneficiaries(beneficiaries);
                    item4.setNoOfContractComplete(dashboardRepositoryImpl.NoOfContractCompleteByComponentId(item4.getComponentId(), yearId,distId));
                    item4.setNoOfContractOnGoing(dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(item4.getComponentId(),yearId,distId));
                    if (adaptData.getExpenditure() != null && adaptData.getActualFundAllocated() != null) {
                        item4.setAdaptAchievementPercentage ((Double.valueOf(adaptData.getExpenditure()) * 100) / Double.valueOf(adaptData.getActualFundAllocated()));
                    }
                }

                item4.setNoOfEstimateApproved(dashboardRepositoryImpl.NoOfEstimateApprovedByComponentId(item4.getComponentId(),distId,yearId));
                item4.setTotalApproxEstCost(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item4.getComponentId(), yearId,null,distId));

                item4.setWorkWiseEstimateAmount(dashboardRepositoryImpl.estimateAmountByComponentIdForWork(item4.getComponentId(),yearId,1,distId));
                item4.setConsultancyWiseEstimateAmount(dashboardRepositoryImpl.estimateAmountByComponentIdForConsultancy(item4.getComponentId(),yearId,2,distId));
                item4.setGoodsWiseEstimateAmount(dashboardRepositoryImpl.estimateAmountByComponentIdForConsultancy(item4.getComponentId(),yearId,3,distId));


                item4.setTotalCanalLength(dashboardRepositoryImpl.TotalCanalLengthByComponentId(item4.getComponentId(),distId));
                item4.setCanalImproved(dashboardRepositoryImpl.CanalImprovedByComponentId(item4.getComponentId(),distId));

                item4.setCdStructurePrepared(dashboardRepositoryImpl.CdStructurePreparedByComponentId(item4.getComponentId(),distId));
                item4.setCdStructureToBePrepared(dashboardRepositoryImpl.CdStructureToBePreparedByComponentId(item4.getComponentId(),distId));

                item4.setTotalCadLength(dashboardRepositoryImpl.TotalCadLengthByComponentId(item4.getComponentId(),distId));
                item4.setCadConstructed(dashboardRepositoryImpl.CadConstructedByComponentId(item4.getComponentId(),distId));
                item4.setNoOfOutletConstructed(dashboardRepositoryImpl.NoOfOutletConstructedByComponentId(item4.getComponentId(),distId));

                item4.setNoOfContractComplete(dashboardRepositoryImpl.NoOfContractCompleteByComponentId(item4.getComponentId(), yearId,distId));
                item4.setNoOfContractOnGoing(dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(item4.getComponentId(),yearId,distId));

                item4.setContractAmount(dashboardRepositoryImpl.contractAmountByComponentId(item4.getComponentId(),yearId));
                item4.setConsultancyWiseContractAmount(dashboardRepositoryImpl.contractAmountByComponentIdForConsultancy(item4.getComponentId(), yearId,2));
                item4.setWorkWiseContractAmount(dashboardRepositoryImpl.contractAmountByComponentIdForWork(item4.getComponentId(), yearId,1));
                item4.setGoodsWiseContractAmount(dashboardRepositoryImpl.contractAmountByComponentIdForWork(item4.getComponentId(),yearId,3));

                item4.setUpToDateExpenditure(dashboardRepositoryImpl.UPtoDateExpenditureByComponentId(item4.getComponentId(),yearId,distId));
                item4.setGoodsWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentId(item4.getComponentId(), yearId,3));
                item4.setWorkWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentId(item4.getComponentId(),yearId,1));
                item4.setConsultancyWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentId(item4.getComponentId(), yearId,2));
                item4.setAgricultureWiseExpenditureAmount(masterServiceImpl.expenditureAmountByComponentIdForAgriculture(item4.getComponentId(), yearId,4,null));

                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item4.getComponentId());

                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item4.setCode(code);


            }
            components.put("component", subActivity);
        }

        if(typeId == 5) {
            List<ComponentDto> dist = dashboardRepositoryImpl.getDistWiseExpenditureDetails(componentId,yearId);
            List<ComponentDto> adapt = dashboardRepositoryImpl.getDistWiseAdaptData(componentId,yearId);
            List<ComponentDto> benefi = dashboardRepositoryImpl.getDistWiseBenifi(componentId,yearId);

            List<ComponentDto> contractComplete= dashboardRepositoryImpl.NoOfContractComplete(componentId,yearId);
            List<ComponentDto> contractOngoing = dashboardRepositoryImpl.NoOfContractOngoing(componentId,yearId);

            List<DistrictBoundaryDto> distData= masterQryRepository.getAllDistrict();

            List<ComponentDto> estimateApproved = dashboardRepositoryImpl.getNoOfEstimateApproved(componentId,yearId);
            List<ComponentDto> totalCanalLength = dashboardRepositoryImpl.getTotalCanalLength(componentId);
            List<ComponentDto> canalImproved = dashboardRepositoryImpl.getTotalCanalImproved(componentId);
            List<ComponentDto> cdRestored =dashboardRepositoryImpl.getCdRestored(componentId);
            List<ComponentDto> cdToBeRestored = dashboardRepositoryImpl.getCdToBeRestored(componentId);
            List<ComponentDto> cadLength = dashboardRepositoryImpl.getCadLength(componentId);
            List<ComponentDto> cadConstructed = dashboardRepositoryImpl.getCadConstructedDistWise(componentId);
            List<ComponentDto> outlets = dashboardRepositoryImpl.getOutlet(componentId);

            List<ComponentDto> contractAmount = dashboardRepositoryImpl.getContractAmount(componentId,yearId);
            List<ComponentDto> consultancyWieContractAmount = dashboardRepositoryImpl.getConsultancyWieContractAmount(componentId,yearId);
            List<ComponentDto> workWiseContractAmount = dashboardRepositoryImpl.getWorkWiseContractAmount(componentId,yearId);
            List<ComponentDto> goodsWiseContractAmount = dashboardRepositoryImpl.getGoodWiseContractAmount(componentId,yearId);

            List<ComponentDto> upToDateExpenditure = dashboardRepositoryImpl.getUpToDateExpenditure(componentId,yearId);
            List<ComponentDto> goodWiseExpenditure = masterServiceImpl.expenditureAmountDistWise(componentId,yearId);
            List<ComponentDto> workWiseExpenditure = masterServiceImpl.getWorkWiseExpenditureDistWise(componentId,yearId);
            List<ComponentDto> consultancyWiseExpenditureAmount = masterServiceImpl.getConsultancyWiseExpDistWise(componentId,yearId);

            List<ComponentDto> totalApproxEstCost = dashboardRepositoryImpl.getTotalApproxEstCostByComponentId(componentId,yearId);
            List<ComponentDto> workWiseEstimateAmount = dashboardRepositoryImpl.getEstimateAmountByComponentIdForWork(componentId,yearId);
            List<ComponentDto> consultancyWiseEstimateAmount =dashboardRepositoryImpl.getEstimateAmountByComponentIdForConsultancy(componentId,yearId);
            List<ComponentDto> goodsWiseEstimateAmount = dashboardRepositoryImpl.getEstimateAmountByComponentIdForGoods(componentId,yearId);


            for(ComponentDto cd:dist){
              for(int i=0;i<adapt.size();i++){
                  if(cd.getDistId()==adapt.get(i).getDistId()){
                      cd.setAdaptActualFundAllocated(cd.getEstimate()+(adapt.get(i).getAdaptActualFundAllocated()/100));
                      cd.setAdaptFinancialAllocationInApp(cd.getEstimate()+(adapt.get(i).getAdaptFinancialAllocationInApp()/100));
                      cd.setAdaptExpenditure(cd.getExpenditure()+(adapt.get(i).getAdaptExpenditure()/100));

                  }
              }
              for(int j=0;j< benefi.size();j++){
                  if(cd.getDistId()==benefi.get(j).getDistId()){
                      cd.setAdaptBeneficiaries(benefi.get(j).getAdaptBeneficiaries());

                  }
              }

                for(int exp=0;exp< upToDateExpenditure.size();exp++){
                    if(cd.getDistId()==upToDateExpenditure.get(exp).getDistId()){
                        cd.setUpToDateExpenditure(upToDateExpenditure.get(exp).getUpToDateExpenditure());

                    }
                }

                for(int es=0;es< totalApproxEstCost.size();es++){
                    if(cd.getDistId()==totalApproxEstCost.get(es).getDistId()){
                        cd.setTotalApproxEstCost(totalApproxEstCost.get(es).getTotalApproxEstCost());

                    }
                }

                for(int est=0;est< workWiseEstimateAmount.size();est++){
                    if(cd.getDistId()==workWiseEstimateAmount.get(est).getDistId()){
                        cd.setWorkWiseEstimateAmount(workWiseEstimateAmount.get(est).getWorkWiseEstimateAmount());

                    }
                }

                for(int est1=0;est1< consultancyWiseEstimateAmount.size();est1++){
                    if(cd.getDistId()==consultancyWiseEstimateAmount.get(est1).getDistId()){
                        cd.setConsultancyWiseEstimateAmount(consultancyWiseEstimateAmount.get(est1).getConsultancyWiseEstimateAmount());

                    }
                }


                for(int est2=0;est2< goodsWiseEstimateAmount.size();est2++){
                    if(cd.getDistId()==goodsWiseEstimateAmount.get(est2).getDistId()){
                        cd.setGoodsWiseEstimateAmount(goodsWiseEstimateAmount.get(est2).getGoodsWiseEstimateAmount());

                    }
                }

                for(int exp1=0;exp1< goodWiseExpenditure.size();exp1++){
                    if(cd.getDistId()==goodWiseExpenditure.get(exp1).getDistId()){
                        cd.setGoodsWiseExpenditureAmount(goodWiseExpenditure.get(exp1).getGoodsWiseExpenditureAmount());

                    }
                }

                for(int exp2=0;exp2< workWiseExpenditure.size();exp2++){
                    if(cd.getDistId()==workWiseExpenditure.get(exp2).getDistId()){
                        cd.setWorkWiseExpenditureAmount(workWiseExpenditure.get(exp2).getWorkWiseExpenditureAmount());

                    }
                }

                for(int exp3=0;exp3< consultancyWiseExpenditureAmount.size();exp3++){
                    if(cd.getDistId()==consultancyWiseExpenditureAmount.get(exp3).getDistId()){
                        cd.setConsultancyWiseExpenditureAmount(consultancyWiseExpenditureAmount.get(exp3).getConsultancyWiseExpenditureAmount());

                    }
                }

              for(int j1=0; j1<contractComplete.size();j1++){
                  if(cd.getDistId()==contractComplete.get(j1).getDistId()){
                      cd.setNoOfContractComplete(contractComplete.get(j1).getNoOfContractComplete());
                  }
              }

              for(int j2=0; j2<contractOngoing.size();j2++){
                  if(cd.getDistId()==contractOngoing.get(j2).getDistId()){
                      cd.setNoOfContractOnGoing(contractOngoing.get(j2).getNoOfContractOnGoing());
                  }
              }

              for(int j3=0; j3<consultancyWieContractAmount.size();j3++){
                  if(cd.getDistId()==consultancyWieContractAmount.get(j3).getDistId()){
                      cd.setConsultancyWiseContractAmount(consultancyWieContractAmount.get(j3).getConsultancyWiseContractAmount());
                  }
              }

              for(int work=0;work< workWiseContractAmount.size();work++){
                  if(cd.getDistId()==workWiseContractAmount.get(work).getDistId()){
                      cd.setWorkWiseContractAmount(workWiseContractAmount.get(work).getWorkWiseContractAmount());
                  }
              }

              for(int goods=0;goods< goodsWiseContractAmount.size();goods++){
                  if(cd.getDistId()==goodsWiseContractAmount.get(goods).getDistId()){
                      cd.setGoodsWiseContractAmount(goodsWiseContractAmount.get(goods).getGoodsWiseContractAmount());
                  }
              }


              // For Estimate Approved

              for(int k=0;k< estimateApproved.size();k++){
                  if(cd.getDistId()==estimateApproved.get(k).getDistId()){
                      cd.setNoOfEstimateApproved(estimateApproved.get(k).getNoOfEstimateApproved());

                  }
              }

              // For Total CanalLength

              for(int k1=0;k1< totalCanalLength.size();k1++){
                  if(cd.getDistId()==totalCanalLength.get(k1).getDistId()){
                      cd.setTotalCanalLength(totalCanalLength.get(k1).getTotalCanalLength());

                  }
              }

              // For Total CanalImproved

              for(int k2=0;k2< canalImproved.size();k2++){
                  if(cd.getDistId()==canalImproved.get(k2).getDistId()){
                      cd.setCanalImproved(canalImproved.get(k2).getCanalImproved());

                  }
              }

              // For No. of CD structures restored

              for(int cr=0;cr< cdRestored.size();cr++){
                  if(cd.getDistId()==cdRestored.get(cr).getDistId()){
                      cd.setCdStructurePrepared(cdRestored.get(cr).getCdStructurePrepared());

                  }
              }

              // For No. of CD structures to be restored

              for(int crd=0;crd< cdToBeRestored.size();crd++){
                  if(cd.getDistId()==cdToBeRestored.get(crd).getDistId()){
                      cd.setCdStructureToBePrepared(cdToBeRestored.get(crd).getCdStructureToBePrepared());

                  }
              }

              // For Total CAD length (m.)

              for(int cad=0;cad< cadLength.size();cad++){
                  if(cd.getDistId()==cadLength.get(cad).getDistId()){
                      cd.setTotalCadLength(cadLength.get(cad).getTotalCadLength());

                  }
              }

              // For CAD constructed (m.)

              for(int cad1=0;cad1< cadConstructed.size();cad1++){
                  if(cd.getDistId()==cadConstructed.get(cad1).getDistId()){
                      cd.setCadConstructed(cadConstructed.get(cad1).getCadConstructed());

                  }
              }

              // For No. of outlets constructed

              for(int out=0;out< outlets.size();out++){
                  if(cd.getDistId()==outlets.get(out).getDistId()){
                      cd.setNoOfOutletConstructed(outlets.get(out).getNoOfOutletConstructed());

                  }
              }
              // For Total ContractAmount
              for(int cont=0;cont< contractAmount.size();cont++){
                  if(cd.getDistId()==contractAmount.get(cont).getDistId()){
                      cd.setContractAmount(contractAmount.get(cont).getContractAmount());

                  }
              }

          }
          for(ComponentDto cd1:dist){
              cd1.setMasterHeadId(5);
          }
            components.put("component", dist);

        }

        return components;
    }


    @Override
    public Double getAyaCut(Integer tankId, String divisionName) {
        return dashboardRepositoryImpl.getAyaCut(tankId, divisionName);
    }

    @Override
    public Integer workAwarded(Integer tankId, String divisionName) {
        return dashboardRepositoryImpl.workAwarded(tankId, divisionName);
    }

    @Override
    public Object croppingIntensity(Integer tankId, String divisionName) {
        return null;
    }


    @Override
    public List<ValueDto> tanksByCategory(Integer projectId, String divisionName) {
        return dashboardRepositoryImpl.tanksByCategory(projectId, divisionName);
    }

    @Override
    public BigDecimal getTankWiseAyacutArea(Integer projectId) {
        return dashboardRepositoryImpl.getTankWiseAyacutArea(projectId);
    }

    @Override
    public Double TotalWaterSpreadArea(Integer projectId) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String yearId = formatter.format(date);
        List<WaterSpreadDto> water = dashboardRepositoryImpl.getWaterSpreadByProjectId(projectId, yearId);
        Double sumOfMonthArea = dashboardRepositoryImpl.getWaterSpreadSumOfMonth(projectId, yearId);
        if (sumOfMonthArea == 0.00) {
            return 0.00;
        } else {
            return sumOfMonthArea / water.size();
        }
    }

//    @Override
//    public Object tanksByCategory(Integer projectId, Integer divisionId) {
//        return null;
//    }

    @Override
    public List<String> getDistinctYearBYProjectIdAndDivisionId(Integer projectId, String divisionName) {
        return dashboardRepositoryImpl.getDistinctYearBYProjectId(projectId, divisionName);
    }

    @Override
    public List<CropCycleAyacutDto> getCropCycleIntensity(Integer projectId, String year, String divisionName) {
        List<CropCycleAyacutDto> civilStatus = dashboardRepositoryImpl.getCropCycleIntensity(projectId, year, divisionName);

        return civilStatus;
    }

    @Override
    public List<DivisionWiseExpenditureDto> commulativeExpenditure(Integer projectId, String divisionName) {
        return dashboardRepositoryImpl.commulativeExpenditure(projectId, divisionName);
    }

    @Override
    public Object getComponents(Integer userId, Integer typeId, Integer workTypeId, Integer componentId, Integer yearId) {
        Map<String, Object> components = new HashMap<>();


        if (typeId == 1) {
            List<ComponentDto> cmp = dashboardRepositoryImpl.getcompontDetails(null);

            List<Integer> tenderIds = dashboardRepositoryImpl.getTenderIdsByWorkTypeId(workTypeId);
            List<Integer> contractIds = dashboardRepositoryImpl.getContractIdsByWorkTypeId(workTypeId);
            List<Integer> estimateIds = dashboardRepositoryImpl.getEstimateIdsByWorkTypeId(workTypeId);

            for (ComponentDto item : cmp) {
                if (item.getComponentId() == 1)
                {
                    if(workTypeId == null) {
                        AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item.getComponentId(),null,yearId);
                        Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item.getComponentId(),null,yearId);
                        Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId(item.getComponentId(), yearId, null);
                        Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item.getComponentId(), yearId,null);
                        if (adaptData != null) {
                            if(adaptData.getFinancialAllocationInApp()!=null) {
                                BigDecimal a = new BigDecimal(adaptData.getFinancialAllocationInApp() + estimateCost);
                                item.setEstimatedAmount(a.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            }
                            if(adaptData.getExpenditure()!= null) {
                                BigDecimal b = new BigDecimal(adaptData.getExpenditure() + expenditureCost);
                                item.setExpenditureAmount(b.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            }
                            item.setContractAmount(new BigDecimal(0.00));
                        }
                    }  else {
                        item.setEstimatedAmount(new BigDecimal(0.00));
                        item.setExpenditureAmount(new BigDecimal(0.00));
                        item.setContractAmount(new BigDecimal(0.00));
                    }
                } else {
                    if (tenderIds != null && tenderIds.size() > 0) {
                        item.setExpenditureVal1(dashboardRepositoryImpl.
                                getExpenditureByTenderId(activityService.getTerminalIds(item.getComponentId()), yearId, tenderIds));
                    }
                    if (contractIds != null && contractIds.size() > 0) {
                        item.setExpenditureVal2(dashboardRepositoryImpl.getExpenditureByContractId(activityService.
                                getTerminalIds(item.getComponentId()), yearId, contractIds));
                    }
                    if (estimateIds != null && estimateIds.size() > 0) {
                        item.setExpenditureVal3(dashboardRepositoryImpl.getExpenditureByEstimateIds(activityService.
                                getTerminalIds(item.getComponentId()), yearId, estimateIds));
                    }

                    if (workTypeId != null) {
                        BigDecimal total = BigDecimal.ZERO;
                        for (BigDecimal bd : Arrays.asList(item.getExpenditureVal1(), item.getExpenditureVal2(),
                                item.getExpenditureVal3())) {
                            if (null != bd) {
                                total = total.add(bd);
                            }
                        }
                        item.setExpenditureAmount(total);
                    } else {
                        item.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item.getComponentId()), yearId, workTypeId));
                    }

//                BigDecimal total = BigDecimal.ZERO;
//                for(BigDecimal bd: Arrays.asList(item.getExpenditureVal1(),item.getExpenditureVal2(),item.getExpenditureVal3())){
//                    if(null != bd){
//                        total =total .add(bd);
//                    }
//                }
//                if(total.equals(BigDecimal.ZERO)) {
//                    item.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item.getComponentId()), yearId, workTypeId));
//                }
//                else {
//                    item.setExpenditureAmount(total);
//
//                }
                    item.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(
                            activityService.getTerminalIds(item.getComponentId()), yearId, workTypeId));

                    item.setEstimatedAmount(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(
                            item.getComponentId(), yearId, workTypeId,null));

                    if (item.getExpenditureAmount() == null) {
                        item.setExpenditureAmount(new BigDecimal(0.00));
                    }
                }

                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                    String code = "";
                    for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                        code += abc.getCode();
                        if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item.setCode(code);

                    components.put("component", cmp);

            }
        }
        if (typeId == 2) {
            List<ComponentDto> subComponent = dashboardRepositoryImpl.getSubcomponentDetails(componentId);

            List<Integer> tenderIds = dashboardRepositoryImpl.getTenderIdsByWorkTypeId(workTypeId);
            List<Integer> contractIds = dashboardRepositoryImpl.getContractIdsByWorkTypeId(workTypeId);
            List<Integer> estimateIds = dashboardRepositoryImpl.getEstimateIdsByWorkTypeId(workTypeId);

            for (ComponentDto item2 : subComponent) {

                if (componentId == 1) {
                    if(workTypeId == null) {
                        AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item2.getComponentId(),null,yearId);
                        Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item2.getComponentId(),null,yearId);
                        Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId(item2.getComponentId(), yearId, null);
                        Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item2.getComponentId(), yearId,null);
                        if (adaptData != null) {
                            if (adaptData.getFinancialAllocationInApp() != null) {
                                BigDecimal a = new BigDecimal(adaptData.getFinancialAllocationInApp() + estimateCost);
                                item2.setEstimatedAmount(a.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            } else {
                                BigDecimal a = new BigDecimal(estimateCost);
                                item2.setEstimatedAmount(a.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            }
                            if (adaptData.getExpenditure() != null) {
                                BigDecimal b = new BigDecimal(adaptData.getExpenditure() + expenditureCost);
                                item2.setExpenditureAmount(b.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            } else {
                                BigDecimal b = new BigDecimal( expenditureCost);
                                item2.setExpenditureAmount(b.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            }
                            item2.setContractAmount(new BigDecimal(0.00));
                        }
                    }else {
                        item2.setEstimatedAmount(new BigDecimal(0.00));
                        item2.setExpenditureAmount(new BigDecimal(0.00));
                        item2.setContractAmount(new BigDecimal(0.00));
                    }

                } else {
                    if (tenderIds != null && tenderIds.size() > 0) {
                        item2.setExpenditureVal1(dashboardRepositoryImpl.getExpenditureByTenderId(activityService.getTerminalIds(item2.getComponentId()), yearId, tenderIds));
                    }
                    if (contractIds != null && contractIds.size() > 0) {
                        item2.setExpenditureVal2(dashboardRepositoryImpl.getExpenditureByContractId(activityService.getTerminalIds(item2.getComponentId()), yearId, contractIds));
                    }
                    if (estimateIds != null && estimateIds.size() > 0) {
                        item2.setExpenditureVal3(dashboardRepositoryImpl.getExpenditureByEstimateIds(activityService.getTerminalIds(item2.getComponentId()), yearId, estimateIds));
                    }

                    if (workTypeId != null) {
                        BigDecimal total = BigDecimal.ZERO;
                        for (BigDecimal bd : Arrays.asList(item2.getExpenditureVal1(), item2.getExpenditureVal2(),
                                item2.getExpenditureVal3())) {
                            if (null != bd) {
                                total = total.add(bd);
                            }
                        }
                        item2.setExpenditureAmount(total);
                    } else {
                        item2.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId()), yearId, workTypeId));
                    }

//                BigDecimal total = BigDecimal.ZERO;
//                for(BigDecimal bd: Arrays.asList(item2.getExpenditureVal1(),item2.getExpenditureVal2(),item2.getExpenditureVal3())){
//                    if(null != bd){
//                        total =total .add(bd);
//                    }
//                }
//                if(total.equals(BigDecimal.ZERO)) {
//                    item2.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId()), yearId, workTypeId));
//
//                }
//                else {
//                    item2.setExpenditureAmount(total);
//
//                }
                    // item2.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId()), yearId,workTypeId));
                    item2.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item2.getComponentId()), yearId, workTypeId));
                    item2.setEstimatedAmount(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item2.getComponentId(), yearId, workTypeId,null));
                    if (item2.getExpenditureAmount() == null) {
                        item2.setExpenditureAmount(new BigDecimal(0.00));
                    }
                }

                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item2.getComponentId());
                    String code = "";
                    for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                        code += abc.getCode();
                        if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item2.setCode(code);
                    //  item2.setExpenditureAmount((dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId())) == null) ? new BigDecimal(0.00) : dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId())));
                    //   item2.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item2.getComponentId())) == null) ? new BigDecimal(0.00) : dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId())));
                }


                components.put("subComponent", subComponent);


        }

        if (typeId == 3) {
            List<ComponentDto> activity = dashboardRepositoryImpl.getActivityDetails(componentId);

            List<Integer> tenderIds = dashboardRepositoryImpl.getTenderIdsByWorkTypeId(workTypeId);
            List<Integer> contractIds = dashboardRepositoryImpl.getContractIdsByWorkTypeId(workTypeId);
            List<Integer> estimateIds = dashboardRepositoryImpl.getEstimateIdsByWorkTypeId(workTypeId);

            for (ComponentDto item1 : activity) {
                if (componentId == 2) {
                    if(workTypeId == null) {
                        AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item1.getComponentId(),null,yearId);
                        Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item1.getComponentId(),null,yearId);
                        Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId(item1.getComponentId(), yearId, null);
                        Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item1.getComponentId(), yearId,null);
                        if (adaptData != null) {
                            if (adaptData.getFinancialAllocationInApp() != null) {
                                BigDecimal a = new BigDecimal(adaptData.getFinancialAllocationInApp() + estimateCost);
                                item1.setEstimatedAmount(a.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            } else {
                                BigDecimal a = new BigDecimal(estimateCost);
                                item1.setEstimatedAmount(a.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            }
                            if (adaptData.getExpenditure() != null) {
                                BigDecimal b = new BigDecimal(adaptData.getExpenditure() + expenditureCost);
                                item1.setExpenditureAmount(b.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            } else {
                                BigDecimal b = new BigDecimal(expenditureCost);
                                item1.setExpenditureAmount(b.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                            }
                            item1.setContractAmount(new BigDecimal(0.00));
                        }
                    }else {
                        item1.setEstimatedAmount(new BigDecimal(0.00));
                        item1.setExpenditureAmount(new BigDecimal(0.00));
                        item1.setContractAmount(new BigDecimal(0.00));
                    }
                    } else {
                    if (tenderIds != null && tenderIds.size() > 0) {
                        item1.setExpenditureVal1(dashboardRepositoryImpl.getExpenditureByTenderId(activityService.getTerminalIds(item1.getComponentId()), yearId, tenderIds));
                    }
                    if (contractIds != null && contractIds.size() > 0) {
                        item1.setExpenditureVal2(dashboardRepositoryImpl.getExpenditureByContractId(activityService.getTerminalIds(item1.getComponentId()), yearId, contractIds));
                    }
                    if (estimateIds != null && estimateIds.size() > 0) {
                        item1.setExpenditureVal3(dashboardRepositoryImpl.getExpenditureByEstimateIds(activityService.getTerminalIds(item1.getComponentId()), yearId, estimateIds));
                    }

                    if (workTypeId != null) {
                        BigDecimal total = BigDecimal.ZERO;
                        for (BigDecimal bd : Arrays.asList(item1.getExpenditureVal1(), item1.getExpenditureVal2(),
                                item1.getExpenditureVal3())) {
                            if (null != bd) {
                                total = total.add(bd);
                            }
                        }
                        item1.setExpenditureAmount(total);
                    } else {
                        item1.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId()), yearId, workTypeId));
                    }


//                BigDecimal total = BigDecimal.ZERO;
//                for(BigDecimal bd: Arrays.asList(item1.getExpenditureVal1(),item1.getExpenditureVal2(),item1.getExpenditureVal3())){
//                    if(null != bd){
//                        total =total .add(bd);
//                    }
//                }
//                if(total.equals(BigDecimal.ZERO)) {
//                    item1.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId()), yearId, workTypeId));
//
//                }
//                else {
//                    item1.setExpenditureAmount(total);
//
//                }

                    //item1.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId()), yearId,workTypeId));
                    item1.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item1.getComponentId()), yearId, workTypeId));
                    item1.setEstimatedAmount(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item1.getComponentId(), yearId, workTypeId,null));
                }
                    if (item1.getExpenditureAmount() == null) {
                        item1.setExpenditureAmount(new BigDecimal(0.00));
                    }
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getComponentId());
                    String code = "";
                    for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                        code += abc.getCode();
                        if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item1.setCode(code);
                    // item1.setExpenditureAmount((dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId())) == null) ? new BigDecimal(0.00): dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId())));
                    // item1.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item1.getComponentId())) == null) ? new BigDecimal(0.00): dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId())));
                }
                components.put("activity", activity);


        }
        if (typeId == 4) {
            List<ComponentDto> subActivity = dashboardRepositoryImpl.getsubActivityDetails(componentId);

            List<Integer> tenderIds = dashboardRepositoryImpl.getTenderIdsByWorkTypeId(workTypeId);
            List<Integer> contractIds = dashboardRepositoryImpl.getContractIdsByWorkTypeId(workTypeId);
            List<Integer> estimateIds = dashboardRepositoryImpl.getEstimateIdsByWorkTypeId(workTypeId);

            for (ComponentDto item3 : subActivity) {
                if (componentId == 15 || componentId ==19) {
                   if(workTypeId == null) {
                       AdaptFinancialDto adaptData = dashboardRepositoryImpl.getAdaptEstimateAmount(item3.getComponentId(),null,yearId);
                       Integer beneficiaries = dashboardRepositoryImpl.getBeneficiariesSum(item3.getComponentId(),null,yearId);
                       Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId(item3.getComponentId(), yearId, null);
                       Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(item3.getComponentId(), yearId,null);
                       if (adaptData != null) {
                           if (adaptData.getFinancialAllocationInApp() != null) {
                               BigDecimal a = new BigDecimal(adaptData.getFinancialAllocationInApp() + estimateCost);
                               item3.setEstimatedAmount(a.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                           } else {
                               BigDecimal a = new BigDecimal( estimateCost);
                               item3.setEstimatedAmount(a.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                           }
                           if (adaptData.getExpenditure() != null) {
                               BigDecimal b = new BigDecimal(adaptData.getExpenditure() + expenditureCost);
                               item3.setExpenditureAmount(b.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                           } else {
                               BigDecimal b = new BigDecimal(expenditureCost);
                               item3.setExpenditureAmount(b.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                           }
                           item3.setContractAmount(new BigDecimal(0.00));
                       }
                   }else {
                        item3.setEstimatedAmount(new BigDecimal(0.00));
                        item3.setExpenditureAmount(new BigDecimal(0.00));
                        item3.setContractAmount(new BigDecimal(0.00));
                    }
                } else {

                    if (tenderIds != null && tenderIds.size() > 0) {
                        item3.setExpenditureVal1(dashboardRepositoryImpl.getExpenditureByTenderId(activityService.getTerminalIds(item3.getComponentId()), yearId, tenderIds));
                    }
                    if (contractIds != null && contractIds.size() > 0) {
                        item3.setExpenditureVal2(dashboardRepositoryImpl.getExpenditureByContractId(activityService.getTerminalIds(item3.getComponentId()), yearId, contractIds));
                    }
                    if (estimateIds != null && estimateIds.size() > 0) {
                        item3.setExpenditureVal3(dashboardRepositoryImpl.getExpenditureByEstimateIds(activityService.getTerminalIds(item3.getComponentId()), yearId, estimateIds));
                    }
                    if (workTypeId != null) {
                        BigDecimal total = BigDecimal.ZERO;
                        for (BigDecimal bd : Arrays.asList(item3.getExpenditureVal1(), item3.getExpenditureVal2(),
                                item3.getExpenditureVal3())) {
                            if (null != bd) {
                                total = total.add(bd);
                            }
                        }
                        item3.setExpenditureAmount(total);
                    } else {
                        item3.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item3.getComponentId()), yearId, workTypeId));
                    }

//                BigDecimal total = BigDecimal.ZERO;
//                for(BigDecimal bd: Arrays.asList(item3.getExpenditureVal1(),item3.getExpenditureVal2(),item3.getExpenditureVal3())){
//                    if(null != bd){
//                        total =total .add(bd);
//                    }
//                }
//                if(total.equals(BigDecimal.ZERO)) {
//                    item3.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item3.getComponentId()), yearId, workTypeId));
//
//                }
//                else {
//                    item3.setExpenditureAmount(total);
//
//                }

                    //   item3.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item3.getComponentId()), yearId,workTypeId));
                    item3.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item3.getComponentId()), yearId, workTypeId));
                    item3.setEstimatedAmount(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item3.getComponentId(), yearId, workTypeId,null));
                    if (item3.getExpenditureAmount() == null) {
                        item3.setExpenditureAmount(new BigDecimal(0.00));
                    }
                }
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item3.getComponentId());
                    String code = "";
                    for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                        code += abc.getCode();
                        if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item3.setCode(code);
                    // item3.setExpenditureAmount((dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item3.getComponentId())) == null) ? new BigDecimal(0.00): dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item3.getComponentId())));
                    //item3.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item3.getComponentId())) == null) ? new BigDecimal(0.00): dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item3.getComponentId())));
                }
                components.put("subActivity", subActivity);

        }
        if (typeId == 5) {
            List<ComponentDto> division = dashboardRepositoryImpl.getDivisionWiseExpenditureDetails(componentId);
//
//
//            for (ComponentDto division1 : division) {
//                ComponentDto expenditureDetails = dashboardRepositoryImpl.getDivisionWiseExpenditureAmount(division1.getComponentId());
//                division1.setExpenditureAmount(expenditureDetails.getExpenditureAmount());
//                division1.setParentId(expenditureDetails.getParentId());
//            }
            // ComponentDto contingencyDetails = dashboardRepositoryImpl.getDivisionWiseContingencyDetails(division1.getComponentId());
//        for (ComponentDto c:division) {
//            dashboardRepositoryImpl.getDivisionWiseExpenditureDetails(c.getComponentId());
//            dashboardRepositoryImpl.getDivisionWiseContingencyDetails(c.getComponentId());
//        }
//        List<ComponentDto> division =  dashboardRepositoryImpl.getDivisionDetails();
            components.put("division", division);
        }


        return components;
    }

    @Override
    public Object getComponentWiseContractAmount(Integer userId, Integer typeId, Integer componentId, Integer yearId) {
        Map<String, Object> components = new HashMap<>();

        if (typeId == 1) {
            List<ComponentDto> cmp = dashboardRepositoryImpl.getcompontDetails(null);
            for (ComponentDto item : cmp) {
                item.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item.getComponentId()), yearId,null));
                if (item.getContractAmount() == null) {
                    item.setContractAmount(new BigDecimal(0.00));
                }
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
                //item.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item.getComponentId())) == null) ? new BigDecimal(0.00) : dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item.getComponentId())));
            }
            components.put("component", cmp);
        }
        if (typeId == 2) {
            List<ComponentDto> subComponent = dashboardRepositoryImpl.getSubcomponentDetails(componentId);
            for (ComponentDto item2 : subComponent) {
                item2.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item2.getComponentId()), yearId,null));
                if (item2.getContractAmount() == null) {
                    item2.setContractAmount(new BigDecimal(0.00));
                }
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item2.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item2.setCode(code);
                // item2.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item2.getComponentId())) == null) ? new BigDecimal(0.00) : dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item2.getComponentId())));
            }
            components.put("subComponent", subComponent);
        }
        if (typeId == 3) {
            List<ComponentDto> activity = dashboardRepositoryImpl.getActivityDetails(componentId);
            for (ComponentDto item1 : activity) {
                item1.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item1.getComponentId()), yearId,null));
                if (item1.getContractAmount() == null) {
                    item1.setContractAmount(new BigDecimal(0.00));
                }
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item1.setCode(code);
                // item1.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item1.getComponentId())) == null) ? new BigDecimal(0.00) : dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item1.getComponentId())));
            }
            components.put("activity", activity);
        }
        if (typeId == 4) {
            List<ComponentDto> subActivity = dashboardRepositoryImpl.getsubActivityDetails(componentId);
            for (ComponentDto item3 : subActivity) {
                item3.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item3.getComponentId()), yearId,null));
                if (item3.getContractAmount() == null) {
                    item3.setContractAmount(new BigDecimal(0.00));
                }
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item3.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item3.setCode(code);
                //item3.setExpenditureAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item3.getComponentId())) == null) ? new BigDecimal(0.00): dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item3.getComponentId())));
                //item3.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item3.getComponentId())) == null) ? new BigDecimal(0.00): dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item3.getComponentId())));
            }
            components.put("subActivity", subActivity);
        }
        if (typeId == 5) {
            List<ComponentDto> division = dashboardRepositoryImpl.getDivisionWiseContractAmountDetails(componentId);
//            List<ComponentDto> division = dashboardRepositoryImpl.getAllDivisionList(componentId);
//
//            for (ComponentDto division1 : division) {
//                ComponentDto contractDetails = dashboardRepositoryImpl.getDivisionWiseContractAmountDetails(division1.getComponentId());
//                division1.setContractAmount(contractDetails.getContractAmount());
//                division1.setParentId(contractDetails.getParentId());
//            }
            components.put("division", division);
        }


        return components;
    }


    @Override
    public BigDecimal getTotalLengthOfCanalAsPerEstimate(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getTotalLengthOfCanalAsPerEstimate(circleId, distId, divisionId, blockId);
    }


    @Override
    public BigDecimal getLengthOfCanalImproved(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getLengthOfCanalImproved(circleId, distId, divisionId, blockId);
    }

    @Override
    public BigDecimal getNoOfCdStructuresRepared(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getNoOfCdStructuresRepared(circleId, distId, divisionId, blockId);
    }

    @Override
    public BigDecimal getNoOfCdStructuresToBeRepared(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getNoOfCdStructuresToBeRepared(circleId, distId, divisionId, blockId);
    }

    @Override
    public BigDecimal getTotalLengthOfCad(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getTotalLengthOfCad(circleId, distId, divisionId, blockId);
    }

    @Override
    public BigDecimal getNoOfOutletConstructed(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getNoOfOutletConstructed(circleId, distId, divisionId, blockId);
    }

    @Override
    public BigDecimal getCadConstructed(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        return dashboardRepositoryImpl.getCadConstructed(circleId, distId, divisionId, blockId);
    }

    @Override
    public BigDecimal getTotalWaterSpreadArea(Integer projectId) {
        return dashboardRepositoryImpl.getTotalWaterSpreadArea(projectId);
    }

    @Override
    public DivisionWiseExpenditureDto totalCommulativeExpenditure(Integer projectId, String divisionName) {
        return dashboardRepositoryImpl.totalCommulativeExpenditure(projectId, divisionName);
    }

    @Override
    public BigDecimal getcontractAmount(Integer projectId, String divisionName) {
        return dashboardRepositoryImpl.getcontractAmount(projectId, divisionName);
    }

    @Override
    public BigDecimal getcatchmentArea(Integer projectId, String divisionName) {
        return dashboardRepositoryImpl.getcatchmentArea(projectId, divisionName);
    }

    @Override
    public List<Integer> getProjectIdByDivisionName(String divisionName) {
        return dashboardRepositoryImpl.getProjectIdByDivisionName(divisionName);
    }

    @Override
    public Object getComponentWiseContractStatus(Integer userId, Integer typeId, Integer statusId) {
        Map<String, Object> components = new HashMap<>();
        //For Array Push
        List<ComponentDto> result = new ArrayList<>();
        if (typeId == 1) {
            //All Status
            List<StatusDto> allStatus = dashboardRepositoryImpl.getcontractStatusall();
            components.put("allStatus", allStatus);
            return components;
        }
        //Component
        if (typeId == 2) {
            List<ComponentDto> cmp = dashboardRepositoryImpl.getcompontDetails(null);
            for (ComponentDto item : cmp) {
                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("component", cmp);
            return components;
        }

        //SubComponent
        if (typeId == 3) {
            List<ComponentDto> subComponent = dashboardRepositoryImpl.getSubcomponentDetails(null);
            for (ComponentDto item : subComponent) {
                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("subComponent", subComponent);
            return components;
        }
        //Activity
        if (typeId == 4) {
            List<ComponentDto> activity = dashboardRepositoryImpl.getActivityDetails(null);
            for (ComponentDto item : activity) {
                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("activity", activity);
            return components;
        }
        //SubActivity
        if (typeId == 5) {
            List<ComponentDto> subActivity = dashboardRepositoryImpl.getsubActivityDetails(null);
            for (ComponentDto item : subActivity) {
                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("subActivity", subActivity);
            return components;
        }


//
//        List<StatusDto> allStatus=dashboardRepositoryImpl.getcontractStatusall();
//        components.put("allStatus",allStatus);
//
//        //Component
//        for (StatusDto status:allStatus) {
//            List<ComponentDto> cmp = dashboardRepositoryImpl.getcompontDetails();
//            for (ComponentDto item : cmp) {
//                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(activityService.getTerminalIds(item.getComponentId()),status.getStatusId()));
//                item.setStatusId(status.getStatusId());
//                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
//                String code = "";
//                for(ActivityUpperHierarchyInfo abc : currentHierarchyInfoById){
//                    code += abc.getCode();
//                    if(!code.isEmpty() && abc.getMasterHeadId() <= 1){
//                        code = code+".";
//                    }
//                }
//                item.setCode(code);
//            }
//            result.addAll(cmp);
//        }
//        components.put("component",result);
//
//
//        List<ComponentDto> result1 = new ArrayList<>();
//        for (StatusDto status:allStatus) {
//            List<ComponentDto> subComponent = dashboardRepositoryImpl.getSubcomponentDetails();
//            for (ComponentDto item : subComponent) {
//                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(activityService.getTerminalIds(item.getComponentId()),status.getStatusId()));
//                item.setStatusId(status.getStatusId());
//                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
//                String code = "";
//                for(ActivityUpperHierarchyInfo abc : currentHierarchyInfoById){
//                    code += abc.getCode();
//                    if(!code.isEmpty() && abc.getMasterHeadId() <= 1){
//                        code = code+".";
//                    }
//                }
//                item.setCode(code);
//            }
//            result1.addAll(subComponent);
//        }
//        components.put("subComponent",result1);
//        List<ComponentDto> result2 = new ArrayList<>();
//        for (StatusDto status:allStatus) {
//            List<ComponentDto> activity = dashboardRepositoryImpl.getActivityDetails();
//            for (ComponentDto item : activity) {
//                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(activityService.getTerminalIds(item.getComponentId()),status.getStatusId()));
//                item.setStatusId(status.getStatusId());
//                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
//                String code = "";
//                for(ActivityUpperHierarchyInfo abc : currentHierarchyInfoById){
//                    code += abc.getCode();
//                    if(!code.isEmpty() && abc.getMasterHeadId() <= 1){
//                        code = code+".";
//                    }
//                }
//                item.setCode(code);
//            }
//            result2.addAll(activity);
//        }
//        components.put("Activity",result2);
//        List<ComponentDto> result3 = new ArrayList<>();
//        for (StatusDto status:allStatus) {
//            List<ComponentDto> subActivity = dashboardRepositoryImpl.getsubActivityDetails();
//            for (ComponentDto item : subActivity) {
//                item.setStatusCount(dashboardRepositoryImpl.getValueComponentWiseStatus(activityService.getTerminalIds(item.getComponentId()),status.getStatusId()));
//                item.setStatusId(status.getStatusId());
//                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
//                String code = "";
//                for(ActivityUpperHierarchyInfo abc : currentHierarchyInfoById){
//                    code += abc.getCode();
//                    if(!code.isEmpty() && abc.getMasterHeadId() <= 1){
//                        code = code+".";
//                    }
//                }
//                item.setCode(code);
//            }
//            result3.addAll(subActivity);
//        }
//        components.put("subActivity",result3);

//        List<StatusDto> contractStatusComponentWise=dashboardRepositoryImpl.getcontractStatusComponentWise();
//        List<ComponentDto> cmp = dashboardRepositoryImpl.getcompontDetails();
//        for (ComponentDto item : cmp) {
//            item.setContractAmount(dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item.getComponentId())));
//            if (item.getContractAmount()==null){
//                item.setContractAmount(new BigDecimal(0.00));
//            }
//            List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
//            String code = "";
//            for(ActivityUpperHierarchyInfo abc : currentHierarchyInfoById){
//                code += abc.getCode();
//                if(!code.isEmpty() && abc.getMasterHeadId() <= 1){
//                    code = code+".";
//                }
//            }
//            item.setCode(code);
//            //item.setContractAmount((dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item.getComponentId())) == null) ? new BigDecimal(0.00) : dashboardRepositoryImpl.getValueOfComponentsCA(activityService.getTerminalIds(item.getComponentId())));
//        }
//
//        List<StatusDto> contractStatusSubComponentWise=dashboardRepositoryImpl.getcontractStatusSubComponentWise();
//        List<StatusDto> contractStatusActivityComponentWise=dashboardRepositoryImpl.getcontractStatusActivityComponentWise();
//        List<StatusDto> contractStatusSubActComponentWise=dashboardRepositoryImpl.getcontractStatusSubActComponentWise();
        //components.put("component",cmp);
//        components.put("subComponent",contractStatusSubComponentWise);
//        components.put("activity",contractStatusActivityComponentWise);
//        components.put("subActivity",contractStatusSubActComponentWise);
        return components;
    }

    @Override
    public Object getComponentWiseEstimateStatus(Integer userId, Integer typeId, Integer statusId) {
        Map<String, Object> components = new HashMap<>();
        //For Array Push
        List<ComponentDto> result = new ArrayList<>();
        if (typeId == 1) {
            //All Status
            List<StatusDto> allStatus = dashboardRepositoryImpl.getestimateStatusall();
            components.put("allStatus", allStatus);
        }
        //Component
        if (typeId == 2) {
            List<ComponentDto> cmp = dashboardRepositoryImpl.getcompontDetails(null);
            for (ComponentDto item : cmp) {
                item.setStatusCount(dashboardRepositoryImpl.estimateStatusComponentWise(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code;
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("component", cmp);
            return components;
        }
        //SubComponent
        if (typeId == 3) {
//
            List<ComponentDto> subComponent = dashboardRepositoryImpl.getSubcomponentDetails(null);
            for (ComponentDto item : subComponent) {
                item.setStatusCount(dashboardRepositoryImpl.estimateStatusComponentWise(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("subComponent", subComponent);
            return components;
        }
        //Activity
        if (typeId == 4) {
            List<ComponentDto> activity = dashboardRepositoryImpl.getActivityDetails(null);
            for (ComponentDto item : activity) {
                item.setStatusCount(dashboardRepositoryImpl.estimateStatusComponentWise(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("activity", activity);
            return components;
        }
        //SubActivity
        if (typeId == 5) {
            List<ComponentDto> subActivity = dashboardRepositoryImpl.getsubActivityDetails(null);
            for (ComponentDto item : subActivity) {
                item.setStatusCount(dashboardRepositoryImpl.estimateStatusComponentWise(statusId, item.getComponentId()));
                item.setStatusId(statusId);
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item.getComponentId());
                String code = "";
                for (ActivityUpperHierarchyInfo abc : currentHierarchyInfoById) {
                    code += abc.getCode();
                    if (!code.isEmpty() && abc.getMasterHeadId() <= 1) {
                        code = code + ".";
                    }
                }
                item.setCode(code);
            }
            // result.addAll(cmp);
            components.put("subActivity", subActivity);
            return components;
        }


        return components;


        //       List<StatusDto> estimateComponentWise=dashboardRepositoryImpl.estimateStatusComponentWise();
//        List<StatusDto> estimateStatusSubComponentWise=dashboardRepositoryImpl.estimateStatusSubComponentWise();
//        List<StatusDto> estimateStatusActivityComponentWise=dashboardRepositoryImpl.estimateStatusActivityComponentWise();
//        List<StatusDto> estimateStatusSubActComponentWise=dashboardRepositoryImpl.estimateStatusSubActComponentWise();
//        components.put("component",estimateComponentWise);
//        components.put("subComponent",estimateStatusSubComponentWise);
//        components.put("activity",estimateStatusActivityComponentWise);
//        components.put("subActivity",estimateStatusSubActComponentWise);
        //return components;
    }


    @Override
    public Object getValueByComponentId(Integer componentId, Integer yearId) {
        Map<String, Object> result = new HashMap<>();
        result.put("NoOfEstimateApproved", dashboardRepositoryImpl.NoOfEstimateApprovedByComponentId(componentId,null,yearId));
        result.put("NoOfContractComplete", dashboardRepositoryImpl.NoOfContractCompleteByComponentId(componentId,yearId,null));
        result.put("TotalApproxEstCost", dashboardRepositoryImpl.TotalApproxEstCostByComponentId(componentId, yearId,null,null));
        result.put("contractAmount", dashboardRepositoryImpl.contractAmountByComponentId(componentId,yearId));
        result.put("BalanceWorkForContract", dashboardRepositoryImpl.BalanceWorkForContractByComponentId(componentId,yearId));
        result.put("UPtoDateExpenditure", dashboardRepositoryImpl.UPtoDateExpenditureByComponentId(componentId,yearId,null));
        result.put("BalanceContractValue", dashboardRepositoryImpl.BalanceContractValueByComponentId(componentId,yearId));
        result.put("TotalCanalLength", dashboardRepositoryImpl.TotalCanalLengthByComponentId(componentId,null));
        result.put("CanalImproved", dashboardRepositoryImpl.CanalImprovedByComponentId(componentId,null));
        result.put("CdStructurePrepared", dashboardRepositoryImpl.CdStructurePreparedByComponentId(componentId,null));
        result.put("CdStructureToBePrepared", dashboardRepositoryImpl.CdStructureToBePreparedByComponentId(componentId,null));
        result.put("TotalCadLength", dashboardRepositoryImpl.TotalCadLengthByComponentId(componentId,-1));
        result.put("CadConstructed", dashboardRepositoryImpl.CadConstructedByComponentId(componentId,null));
        result.put("NoOfOutletConstructed", dashboardRepositoryImpl.NoOfOutletConstructedByComponentId(componentId,null));
        result.put("WorkWiseContractAmount", dashboardRepositoryImpl.contractAmountByComponentIdForWork(componentId,yearId,1));
        result.put("ConsultancyWiseContractAmount", dashboardRepositoryImpl.contractAmountByComponentIdForConsultancy(componentId,yearId,2));
        result.put("WorkWiseEstimateAmount", dashboardRepositoryImpl.estimateAmountByComponentIdForWork(componentId,yearId,1,null));
        result.put("ConsultancyWiseEstimateAmount", dashboardRepositoryImpl.estimateAmountByComponentIdForConsultancy(componentId,yearId,2,null));
        result.put("NoOfContractOnGoing", dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(componentId,yearId,null));
        if(componentId==134 || componentId==177){
            result.put("GoodsWiseEstimateAmount", dashboardRepositoryImpl.estimateAmountByComponentIdForConsultancy(componentId,yearId,3,null));
            result.put("GoodsWiseExpenditureAmount",masterServiceImpl.expenditureAmountByComponentId2(componentId,yearId,3));
            result.put("GoodsWiseContractAmount",dashboardRepositoryImpl.contractAmountByComponentIdForWork(componentId,yearId,3));
        }
        result.put("AgricultureWiseEstimateAmount",dashboardRepositoryImpl.estimateAmountByComponentIdForAgriculture(componentId,yearId,4));
        result.put("WorkWiseExpenditureAmount", masterServiceImpl.expenditureAmountByComponentId2(componentId,yearId,1));
        result.put("ConsultancyWiseExpenditureAmount", masterServiceImpl.expenditureAmountByComponentId2(componentId,yearId,2));
        result.put("AgricultureWiseExpenditureAmount",masterServiceImpl.expenditureAmountByComponentIdForAgriculture(componentId,yearId,4,null));

        if(componentId==1 || componentId==2 || componentId==15 || componentId ==19){
            AdaptFinancialDto adaptData= dashboardRepositoryImpl.getAdaptEstimateAmount(componentId,null,yearId);
            Integer beneficiaries=dashboardRepositoryImpl.getBeneficiariesSum(componentId,null,yearId);
            Double estimateCost = dashboardRepositoryImpl.TotalEstCostByComponentId2(componentId, yearId,null,null);
            Double expenditureCost = dashboardRepositoryImpl.UPtoDateExpenditureAmountByComponentId(componentId,yearId,null
            );
            if(adaptData!=null) {
                if(adaptData.getFinancialAllocationInApp() != null){
                    result.put("AdaptFinancialAllocationInApp", adaptData.getFinancialAllocationInApp() + estimateCost);
                }else {
                    result.put("AdaptFinancialAllocationInApp",estimateCost);
                }
                if(adaptData.getActualFundAllocated() != null) {
                    result.put("AdaptActualFundAllocated", adaptData.getActualFundAllocated() + estimateCost);
                }
                else{
                    result.put("AdaptActualFundAllocated",estimateCost);
                }
                if (adaptData.getExpenditure() != null) {
                    result.put("AdaptExpenditure", adaptData.getExpenditure() + expenditureCost);
                }else {
                    result.put("AdaptExpenditure",expenditureCost);
                }
                result.put("AdaptBeneficiaries", beneficiaries);
                result.put("NoOfContractComplete", dashboardRepositoryImpl.NoOfContractCompleteByComponentId(componentId,yearId,null));
                result.put("NoOfContractOnGoing", dashboardRepositoryImpl.NoOfContractOnGoingByComponentId(componentId,yearId,null));
                if(adaptData.getExpenditure() != null && adaptData.getActualFundAllocated()!= null) {
                    result.put("AdaptAchievementPercentage", ((Double.valueOf(adaptData.getExpenditure()) * 100) / Double.valueOf(adaptData.getActualFundAllocated())));
                }
            }
            else{
                result.put("AdaptFinancialAllocationInApp",0.00);
                result.put("AdaptActualFundAllocated",0.00);
                result.put("AdaptExpenditure", 0.00);
                result.put("AdaptBeneficiaries", beneficiaries);
                result.put("AdaptAchievementPercentage", 0.00);
            }
        }
        return result;
    }


//    @Override
//    public Object getComponentWiseContractAmount(Integer userId){
//        Map<String, Object> components = new HashMap<>();
//        components.put("subActivity",dashboardRepositoryImpl.getsubActivityDetails());
//        List<ComponentDto> cmp= dashboardRepositoryImpl.getcompontDetails();
//        for (ComponentDto item:cmp) {
//            item.setEstimatedAmount((dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item.getComponentId()))==null) ? 0.0:dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item.getComponentId())));
//        }
//        components.put("component",cmp);
//        List<ComponentDto> activity= dashboardRepositoryImpl.getActivityDetails();
//        for (ComponentDto item1:activity) {
//            item1.setEstimatedAmount((dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId()))==null) ? 0.0:dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getComponentId())));
//        }
//        components.put("activity",activity);
//        List<ComponentDto> subComponent= dashboardRepositoryImpl.getSubcomponentDetails();
//        for (ComponentDto item2:subComponent) {
//            item2.setEstimatedAmount((dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId()))==null) ? 0.0:dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item2.getComponentId())));
//        }
//        components.put("subComponent",subComponent);
//        return  components;
//    }


    @Override
    public PhysicalProgressValueDto getPhysicalProgressValue() {
        return dashboardRepositoryImpl.getPhysicalProgressValue();
    }




}