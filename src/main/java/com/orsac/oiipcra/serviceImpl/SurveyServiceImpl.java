package com.orsac.oiipcra.serviceImpl;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.*;
import com.orsac.oiipcra.repository.TankSurveyDataRepository;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.ActivityService;
import com.orsac.oiipcra.service.DashboardService;
import com.orsac.oiipcra.service.SurveyService;
import com.orsac.oiipcra.utility.Constant;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class SurveyServiceImpl implements SurveyService {

    @Value("${accessImagePath}")
    private String accessImagePath;
    @Value("${accessWorkProgressImagePath}")
    private String accessWorkProgressImagePath;

    @Value("${accessFeederImagePath}")
    private String accessFeederImagePath;

    @Value("${accessDepthImagePath}")
    private String accessDepthImagePath;

    @Value("${accessCadImagePath}")
    private String accessCadImagePath;

    @Autowired
    private RevenueVillageRepository revenueVillageRepository;
    @Autowired
    private AWSS3StorageService awss3StorageService;

    @Autowired
    private TankSurveyDataRepository tankSurveyDataRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private TankSurveyImageRepository tankSurveyImageRepository;

    @Autowired
    private AWSS3StorageServiceImpl aWSS3StorageServiceImpl;

    @Autowired
    private RecgargeShaftImageRepository recgargeShaftImageRepository;

    @Autowired
    private TankSurveyLocationRepository tankSurveyLocationRepository;

    @Autowired
    private ActivitySurveyRepository activitySurveyRepository;

    @Autowired
    private FeederLocationRepository feederLocationRepository;

    @Autowired
    private CadLocationRepository cadLocationRepository;

    @Autowired
    private ActivitySurveyImageRepository activitySurveyImageRepository;

    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;

    @Autowired
    private FeederRepository feederRepository;

    @Autowired
    private FeederImageRepository feederImageRepository;

    @Autowired
    private DepthImageRepository depthImageRepository;

    @Autowired
    private CadImageRepository cadImageRepository;

    @Autowired
    private DepthRepository depthRepository;

    @Autowired
    private CadRepository cadRepository;

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private ExpenditureQueryRepo expenditureQueryRepo;

    @Autowired
    private ConfigurableEnvironment env;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    private UserQueryRepository userQryRepo;



    /**
     * insert survey tank data
     * */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OIIPCRAResponse insertTankSurveyData(String data, MultipartFile trainingImage, MultipartFile surveyorImage, MultipartFile[] tankImages, MultipartFile[] rechargeShaftImages) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        OIIPCRAResponse response = new OIIPCRAResponse();
        TankSurveyInfo tankInfo = mapper.readValue(data, TankSurveyInfo.class);
        List<TankImages> tankimage = tankInfo.getTankImages();
        List<RechargeShaftImages> shaftImages = tankInfo.getShaftImages();
        List<TankLocations> tankLocations = tankInfo.getTankLocation();

        int surveyId = 0;
        TankSurveyData savetank = null;
        List<TankSurveyImage> tankSurveyImages = null;
        List<RecgargeShaftImage> recgargeShaftImages = null;
        Date savetime = null;

        try {
            if (tankInfo != null && tankInfo.getProjectId() > 0) {
                //get the data in json format in a file
                File file=new File("TankSurveyData1.txt");
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] strToBytes = data.getBytes();
                outputStream.write(strToBytes);
                FileInputStream input = new FileInputStream(file);
                MultipartFile multipartFile = new MockMultipartFile("file",
                        file.getName(), "application/txt",IOUtils.toByteArray(input));
                boolean saveDocument = awss3StorageService.uploadSurveyData(multipartFile, String.valueOf(tankInfo.getTankId()),"TankSurveyData1.txt");
                outputStream.close();
                System.out.println("Done");

                TankSurveyData details = new TankSurveyData();
                BeanUtils.copyProperties(tankInfo, details);
                details.setProgressStatusId(Constant.submitted_id);//1-->submit the work surveyed data
                    TankSurveyInfo tankSurveyInfo = locationInfo(tankInfo, tankLocations);
                    details.setStateId(21);
                    details.setDistrictId(tankSurveyInfo.getDistrictId());
                    details.setBlockId(tankSurveyInfo.getBlockId());
                    details.setGpId(tankSurveyInfo.getGpId());
                    details.setVillageId(tankSurveyInfo.getVillageId());
                    details.setDivisionId(tankSurveyInfo.getDivisionId());
                    details.setSubDivisionId(tankSurveyInfo.getSubDivisionId());
                    details.setSectionId(tankSurveyInfo.getSectionId());
                    details.setGeom(tankSurveyInfo.getGeom());
                    details.setMobileTagged(true);
                    details.setActive(true);
                savetank = tankSurveyDataRepository.save(details);
                surveyId = savetank.getId();

                if(tankLocations!=null && !tankLocations.isEmpty()){
                    List<TankSurveyLocation> locations = new ArrayList<>();
                    for(TankLocations locations1 : tankLocations){
                        TankSurveyLocation locationList = new TankSurveyLocation();
                        BeanUtils.copyProperties(locations1, locationList);
                        locationList.setSurveyId(surveyId);
                        locationList.setCreatedBy(tankInfo.getCreatedBy());
                        locationList.setUpdatedBy(tankInfo.getUpdatedBy());
                        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if ( locations1.getSavetime()!=null) {
                            savetime = dtFormat.parse(locations1.getSavetime());
                        }
                        locationList.setSavetime(savetime);
                        locationList.setActive(true);
                        locations.add(locationList);
                    }
                    tankSurveyLocationRepository.saveAll(locations);
                }
                if (tankimage != null && !tankimage.isEmpty()) {
                    List<TankSurveyImage> imageList = new ArrayList<>();
                for (TankImages tankImages1 : tankimage) {
                    TankSurveyImage tankSurveyImage = new TankSurveyImage();
                    BeanUtils.copyProperties(tankImages1, tankSurveyImage);
                    tankSurveyImage.setSurveyId(surveyId);
                    tankSurveyImage.setCreatedBy(tankInfo.getCreatedBy());
                    tankSurveyImage.setUpdatedBy(tankInfo.getUpdatedBy());
                    SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if ( tankImages1.getSavetime()!=null) {
                        savetime = dtFormat.parse(tankImages1.getSavetime());
                    }
                    tankSurveyImage.setSavetime(savetime);
                    tankSurveyImage.setActive(true);
                    imageList.add(tankSurveyImage);
                }
                tankSurveyImages = tankSurveyImageRepository.saveAll(imageList);

              } if (shaftImages != null && !shaftImages.isEmpty()) {
                List<RecgargeShaftImage> shaftList = new ArrayList<>();
                for (RechargeShaftImages shaftImages1 : shaftImages) {
                    RecgargeShaftImage recgargeShaftImage = new RecgargeShaftImage();
                    BeanUtils.copyProperties(shaftImages1, recgargeShaftImage);
                    recgargeShaftImage.setSurveyId(surveyId);
                    recgargeShaftImage.setCreatedBy(tankInfo.getCreatedBy());
                    recgargeShaftImage.setUpdatedBy(tankInfo.getUpdatedBy());

                    SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (shaftImages1.getSavetime()!=null) {
                        savetime = dtFormat.parse(shaftImages1.getSavetime());
                    }
                    recgargeShaftImage.setSavetime(savetime);
                    recgargeShaftImage.setActive(true);
                    shaftList.add(recgargeShaftImage);
                }
                recgargeShaftImages = recgargeShaftImageRepository.saveAll(shaftList);
            }
            if(tankSurveyImages!=null && !tankSurveyImages.isEmpty()) {
                uploadTankImages(Arrays.asList(tankImages), surveyId);
            }
            if(recgargeShaftImages!=null && !recgargeShaftImages.isEmpty()) {
                uploadShaftImages(Arrays.asList(rechargeShaftImages), surveyId);
            }
            if(surveyorImage!=null){
                uploadImageFiles(surveyorImage,surveyId);
            }if(trainingImage!=null){
                uploadImageFiles(trainingImage,surveyId);
            }
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Inserted Successfully.");
            } else {
                response.setData(Collections.emptyList());
                response.setStatus(0);
                response.setMessage("Record not inserted.");
            }
            //to cheeck the json data in log
         /*   File file=new File("TankSurveyData"+tankInfo.getCreatedBy()+".txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos=new FileOutputStream(file);
            JSONObject json = new JSONObject(data);
            fos.write(data.getBytes());

            log.info("file",fos);*/


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


    /**
     * upload image to aws server
     * */
    public boolean uploadTankImages(List<MultipartFile> tankImages, Integer surveyId) throws IOException {
        int count = 0;
        for (MultipartFile imgfile : tankImages) {
            boolean noOfImages = aWSS3StorageServiceImpl.uploadFileToS3(imgfile, String.valueOf(surveyId), imgfile.getOriginalFilename());
            if (noOfImages) {
                count++;
            }
        }
        return tankImages.size() == count;
    }

    public boolean uploadShaftImages(List<MultipartFile> rechargeShaftImages, int surveyId) throws IOException {
        int count = 0;
        for (MultipartFile shaftImage : rechargeShaftImages) {
            boolean noOfImages = aWSS3StorageServiceImpl.uploadFileToS3(shaftImage, String.valueOf(surveyId), shaftImage.getOriginalFilename());
            if (noOfImages) {
                count++;
            }
        }
        return rechargeShaftImages.size() == count;
    }

    public void uploadImageFiles(MultipartFile imageFile, int surveyId) throws IOException {
        boolean originalUpload = aWSS3StorageServiceImpl.uploadFileToS3(imageFile, String.valueOf(surveyId), imageFile.getOriginalFilename());
    }

    /**
     * get the boundary info using intersects with village boundary table
     * */
    @Transactional
    public TankSurveyInfo locationInfo(TankSurveyInfo tankInfo,List<TankLocations> tankLocations) throws ParseException {

        Polygon poly = null;
        Point point = null;
        LineString line = null;
        Geometry geom = null;
        double lat = 0;
        double lon=0;
        double alt=0;
        double acc=0;

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        ArrayList<Coordinate> pointArray = new ArrayList<>();
        if (tankLocations!=null && tankLocations.size() > 1) {
            for (int i = 0; i < tankLocations.size(); i++) {
                pointArray.add(new Coordinate(tankLocations.get(i).getLon(),tankLocations.get(i).getLat()));
            }
            poly = geometryFactory.createPolygon(pointArray.toArray(new Coordinate[]{}));
            geom=poly;
        }else{
            point = geometryFactory.createPoint(new Coordinate(tankInfo.getLongitudeSurveyed(),tankInfo.getLatitudeSurveyed()));
            geom=point;
        }
        tankInfo.setGeom(geom);

     /*
        if (tankInfo.getTankAreaType().equals("A") && pointArray.size()>1) {
            poly = geometryFactory.createPolygon(pointArray.toArray(new Coordinate[]{}));
            geom=poly;
            tankInfo.setGeom(geom);
        } else if (tankInfo.getTankAreaType().equals("P")) {
            point = geometryFactory.createPoint(new Coordinate(tankLocations.get(0).getLon(),tankLocations.get(0).getLat()));
            geom=point;
            tankInfo.setGeom(geom);
        } else if (tankInfo.getTankAreaType().equals("L")) {
            line = geometryFactory.createLineString(pointArray.toArray(new Coordinate[pointArray.size()]));
            geom=line;
            tankInfo.setGeom(geom);
        }*/
        if(geom!=null) {
            List<RevenueVillageBoundary> revenueVillageBoundaryModel = revenueVillageRepository.getVillageByIdByLngLat(geom.toText());
            List<BlockMappingInfo> blockSectionList = masterRepository.getSectionByBlockId(revenueVillageBoundaryModel.get(0).getBlockId());
            if(revenueVillageBoundaryModel!=null && revenueVillageBoundaryModel.size()>0) {
                tankInfo.setDistrictId(revenueVillageBoundaryModel.get(0).getDistrictId());
                tankInfo.setBlockId(revenueVillageBoundaryModel.get(0).getBlockId());
                tankInfo.setGpId(revenueVillageBoundaryModel.get(0).getGpId());
                tankInfo.setVillageId(revenueVillageBoundaryModel.get(0).getVillageId());
            }
            if(blockSectionList!=null && blockSectionList.size()>0){
                tankInfo.setDivisionId(blockSectionList.get(0).getDivisionId());
                if(blockSectionList.get(0).getSubDivisionId()!=null) {
                    tankInfo.setSubDivisionId(blockSectionList.get(0).getSubDivisionId());
                }
                if(blockSectionList.get(0).getSectionId()!=null) {
                    tankInfo.setSectionId(blockSectionList.get(0).getSectionId());
                }
            }
            if(tankLocations!=null && tankLocations.size()>0) {

                for (int i = 0; i < tankLocations.size(); i++) {
                    lat += tankLocations.get(i).getLat();
                    lon += tankLocations.get(i).getLon();
                    alt += tankLocations.get(i).getAlt();
                    acc += tankLocations.get(i).getAcc();
                }
                double latitude = lat / tankLocations.size();
                double longitude = lon / tankLocations.size();
                double altitude = alt / tankLocations.size();
                double accuracy = acc / tankLocations.size();

                tankInfo.setLatitudeSurveyed(latitude);
                tankInfo.setLongitudeSurveyed(longitude);
                tankInfo.setAltitude(altitude);
                tankInfo.setAccuracy(accuracy);
            }
        }
        return tankInfo;
    }



    /**
     * view survey tank info
     * */
    @Override
    public OIIPCRAResponse getTankSurveyInfoById(int id,int flagId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        TankSurveyData tankData=null;
        TankSurveyInfoResponse surveyData=null;
        List<TankSurveyImage> imageData=null;
        List<TankSurveyLocation> locationData=null;
        List<RecgargeShaftImage> shaftImages =null;
        String imagePath = accessImagePath + id + "/";
//        tankData = tankSurveyDataRepository.findByTankId(id);
        tankData = tankSurveyDataRepository.findById(id);

        List imgList=new ArrayList();
        List locList = new ArrayList();
        List shaftList =new ArrayList();
        //0--->view survey data ,1--->geojson data
        if(flagId==0 && tankData!=null) {
            surveyData = surveyRepositoy.getSurveyInfoById(tankData.getId());
            imageData = tankSurveyImageRepository.findBySurveyId(tankData.getId());
            locationData = tankSurveyLocationRepository.findBySurveyId(tankData.getId());
            shaftImages = recgargeShaftImageRepository.findBySurveyId(tankData.getId());

            Map<String, Object> result = new HashMap<>();
            if(surveyData!=null) {
                result.put("surveyData", surveyData);
            }if(imageData!=null) {
                imageData.forEach(img->{
                   img.setImageName(imagePath + "" + img.getImageName());
                   imgList.add(img);
                });
                result.put("imageData", imgList);

            }if(locationData!=null) {
                result.put("locationData", locationData);
            }if(shaftImages!=null){
                shaftImages.forEach(img->{
                    img.setImageName(imagePath + "" + img.getImageName());
                    shaftList.add(img);
                });
                result.put("shaftImage", shaftList);
            }
            response.setStatus(1);
            response.setData(result);
            response.setMessage("Success");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
         }
          else if(flagId==1 && tankData!=null){
            TankSurveyInfo surveyGeoJson= surveyRepositoy.getSurveyInfoGeoJsonById(tankData.getId());
            imageData = tankSurveyImageRepository.findBySurveyId(tankData.getId());
            locationData = tankSurveyLocationRepository.findBySurveyId(tankData.getId());

            Map<String, Object> result = new HashMap<>();
            if(surveyGeoJson!=null) {
                result.put("surveyData", surveyGeoJson);
            }if(imageData!=null) {
                imageData.forEach(img->{
                    img.setImageName(imagePath + "" + img.getImageName());
                    imgList.add(img);
                });
                result.put("imageData", imgList);

            }if(locationData!=null) {
                result.put("locationData", locationData);
            }
            response.setStatus(1);
            response.setData(result);
            response.setMessage("Success");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        }
          else{
            response.setStatus(0);
            response.setData(null);
            response.setMessage("Record not found");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return response;
    }
    @Override
    public OIIPCRAResponse getTankImageById(Integer tankId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        List<TankSurveyImage> imageData=null;
        List imgList=new ArrayList();
        Map<String, Object> result = new HashMap<>();
        Integer id=surveyRepositoy.getLatestSurveyData(tankId);
        String imagePath = accessImagePath + id + "/";
        if(id!=null && id>0){
            imageData = tankSurveyImageRepository.findBySurveyId(id);
            if(imageData!=null) {
                imageData.forEach(img->{
                    img.setImageName(imagePath + "" + img.getImageName());
                    imgList.add(img);
                });
                result.put("imageData", imgList);
            }
            response.setStatus(1);
            response.setData(result);
            response.setMessage("Success");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        }
        else{
            response.setStatus(0);
            response.setData(result);
            response.setMessage("No record found");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        }

        return response;
    }


    /**
     * update survey tank info
     * */
  /*  @Override
    public OIIPCRAResponse UpdateTankById(String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        OIIPCRAResponse response = new OIIPCRAResponse();
        TankSurveyInfo tankInfo = mapper.readValue(data, TankSurveyInfo.class);

        try {
            TankSurveyData tankData = tankSurveyDataRepository.findById(tankInfo.getId());
            System.out.println(tankData);
            if (data != null && tankData.getId()>0) {
               // BeanUtils.copyProperties(tankInfo, tankData);
                System.out.println(tankInfo);

                tankData.setUpdatedBy(tankInfo.getUpdatedBy());
                tankData.setCatchmentArea(tankInfo.getCatchmentArea());
                tankData.setCcaKharif(tankInfo.getCcaKharif());
                tankData.setCcaRabi(tankInfo.getCcaRabi());
                tankData.setWaterSpreadArea(tankInfo.getWaterSpreadArea());
                tankData.setTankWaterLevelMax(tankInfo.getTankWaterLevelMax());
                tankData.setTankWaterLevelMin(tankInfo.getTankWaterLevelMin());
                tankData.setGroundWaterLevel(tankInfo.getGroundWaterLevel());
                tankData.setTurbidity(tankInfo.getTurbidity());
                tankData.setSolarPumpInstalled(tankInfo.isSolarPumpInstalled());
                tankData.setAquaticVegetationCover(tankInfo.getAquaticVegetationCover());
                tankData.setStatusOfTank(tankInfo.getStatusOfTank());
                tankData.setNoOfBeneficiary(tankInfo.getNoOfBeneficiary());
                tankData.setRechargeStaffInstallation(tankInfo.isRechargeStaffInstallation());
                tankData.setNoOfRechargeShaftInstalled(tankInfo.getNoOfRechargeShaftInstalled());
                tankData.setUsage(tankInfo.getUsage());
                tankData.setTrainingConducted(tankInfo.isTrainingConducted());
                tankData.setNoOfTrainee(tankInfo.getNoOfTrainee());
                tankData.setProgressStatusId(tankInfo.getProgressStatusId());
                tankData.setApprovedBy(tankInfo.getUpdatedBy());
                tankData.setUpdatedBy(tankInfo.getUpdatedBy());
                tankData.setUpdatedOn(tankInfo.getUpdatedOn());
                tankSurveyDataRepository.save(tankData);
                System.out.println(tankData);
                response.setData(Collections.emptyList());
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tank Info Updated Successfully");
                log.info("Tank Info Updated Successfully");

            }else{
                response.setData(Collections.emptyList());
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tank Info Not Found...please survey the tank");
                log.info("Tank Info Not Found");
            }

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
*/

    /*@Override
    public OIIPCRAResponse updateTankById(TankSurveyData tankSurveyData) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            TankSurveyData tankData = tankSurveyDataRepository.findById(tankSurveyData.getId());
            if (tankData != null && tankData.getId()>0) {
                tankSurveyDataRepository.save(tankSurveyData);
                response.setData(Collections.emptyList());
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tank Info Updated Successfully");
                log.info("Tank Info Updated Successfully");

            }else{
                response.setData(Collections.emptyList());
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tank Info Not Found...please survey the tank");
                log.info("Tank Info Not Found");
            }

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
*/

    @Override
    public OIIPCRAResponse updateTankById(TankSurveyInfo tankSurveyInfo) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        int id=0;
        try {
            UserInfoDto userInfoById = null;
            userInfoById = userQryRepo.getUserById(tankSurveyInfo.getUserId());

            List<UserLevelDto> userLevel = null;
            if (userInfoById != null) {
                userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
            }

            TankSurveyData tankData = tankSurveyDataRepository.findById(tankSurveyInfo.getId());

            if (tankData != null && tankData.getId()>0 && tankData.getApprovalLevel()==0) {
                int approvalLevel = userQryRepo.getUserLevelIdByUserId(tankSurveyInfo.getUserId());
                tankSurveyInfo.setApprovalLevel(approvalLevel);
                id = surveyRepositoy.updateSurveyTankData(tankSurveyInfo, tankSurveyInfo.getId());
                response.setData(Collections.emptyList());
                if(id>0) {
                    response.setStatus(1);
                    response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                    response.setMessage("Tank Info Updated Successfully");
                    log.info("Tank Info Updated Successfully");
                }else {
                    response.setStatus(0);
                    response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    response.setMessage("Tank Info not updated");
                    log.info("Tank Info not updated");
                }

            }
            else {
                if (tankData.getApprovalLevel() != 0 && tankData.getApprovalLevel() <= userLevel.get(0).getId()) {
                    int approvalLevel = userQryRepo.getUserLevelIdByUserId(tankSurveyInfo.getUserId());
                    tankSurveyInfo.setApprovalLevel(approvalLevel);
                    id = surveyRepositoy.updateSurveyTankData(tankSurveyInfo, tankSurveyInfo.getId());
                    response.setData(Collections.emptyList());
                    if (id > 0) {
                        response.setStatus(1);
                        response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                        response.setMessage("Tank Info Updated Successfully");
                        log.info("Tank Info Updated Successfully");
                    } else {
                        response.setStatus(0);
                        response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                        response.setMessage("Tank Info not updated");
                        log.info("Tank Info not updated");
                    }
                } else {
                    response.setData(Collections.emptyList());
                    response.setStatus(1);
                    response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                    response.setMessage("User not authorised to update the tank survey info");
                    log.info("User not authorised to update the tank survey info");
                }
            }
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


    /**
     * TankSurvey Search list
     * */
    @Transactional
    @Override
    public OIIPCRAResponse searchSurveyTankList(SurveyListRequest surveyListRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> tankIdsByIssueId = new ArrayList<>();
            List<Integer> tankIdsByContractId = new ArrayList<>();
            List<Integer> tankIdsByEstimateId = new ArrayList<>();

            if( surveyListRequest.getIssueId()!=null && surveyListRequest.getIssueId()>0 ){
                    tankIdsByIssueId = surveyRepositoy.getTankIdsByIssueId(surveyListRequest.getIssueId());
                }

            if( surveyListRequest.getContractId()!=null && surveyListRequest.getContractId()>0 ){
                tankIdsByContractId = surveyRepositoy.getTankIdsByContractId(surveyListRequest.getContractId());
            }

            if( surveyListRequest.getEstimateId()!=null && surveyListRequest.getEstimateId()>0 ){
                tankIdsByEstimateId = surveyRepositoy.getTankIdsByEstimateId(surveyListRequest.getEstimateId());
            }
            Page<TankSurveyInfoResponse> tankListPage = surveyRepositoy.getTankSurveySearchList(surveyListRequest,tankIdsByIssueId,tankIdsByContractId,tankIdsByEstimateId);
            List<TankSurveyInfoResponse> tankList = tankListPage.getContent();
            result.put("tankList", tankList);
            result.put("currentPage", tankListPage.getNumber());
            result.put("totalItems", tankListPage.getTotalElements());
            result.put("totalPages", tankListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks.");
        }catch (Exception e){
            log.info("Tank List Exception : {}", e.getMessage());
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }


    /**
     * insert Activity info
     * */
    //workProgress
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OIIPCRAResponse insertActivitySurveyData(String data, MultipartFile surveyorImage, MultipartFile[] activityImages) throws JsonProcessingException {
        /*ObjectMapper mapper = new ObjectMapper();
        OIIPCRAResponse response = new OIIPCRAResponse();
        ActivitySurveyInfo activityInfo = mapper.readValue(data, ActivitySurveyInfo.class);
        List<TankImages> activityImage = activityInfo.getActivityImages();
        //  List<TankLocations> tankLocations = tankInfo.getTank_location();

        int activityId = 0;
        ActivitySurvey saveActivity = null;
        List<ActivitySurveyImage> activitySurveyImages = null;
        Date savetime = null;
        try {
            if (activityInfo != null) {
                ActivitySurvey details = new ActivitySurvey();
                BeanUtils.copyProperties(activityInfo, details);
                details.setActive(true);
                saveActivity = activitySurveyRepository.save(details);
                activityId = saveActivity.getId();

                if (activityImage != null && !activityImage.isEmpty()) {
                    List<ActivitySurveyImage> imageList = new ArrayList<>();
                    for (TankImages activityImages1 : activityImage) {
                        ActivitySurveyImage activitySurveyImage = new ActivitySurveyImage();
                        BeanUtils.copyProperties(activityImages1, activitySurveyImage);
                        activitySurveyImage.setSurveyId(activityInfo.getTankId());
                        activitySurveyImage.setCreatedBy(activityInfo.getCreatedBy());
                        activitySurveyImage.setUpdatedBy(activityInfo.getUpdatedBy());
                        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (activityImages1.getSavetime() != null) {
                            savetime = dtFormat.parse(activityImages1.getSavetime());
                        }
                        activitySurveyImage.setSavetime(savetime);
                        activitySurveyImage.setActive(true);
                        imageList.add(activitySurveyImage);
                    }
                    activitySurveyImages = activitySurveyImageRepository.saveAll(imageList);

                }
                if (activitySurveyImages != null && !activitySurveyImages.isEmpty()) {
                    uploadTankImages(Arrays.asList(activityImages), activityInfo.getActivityId());
                }
                if (surveyorImage != null) {
                    uploadImageFiles(surveyorImage, activityInfo.getActivityId());
                }

                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Inserted Successfully.");
            } else {
                response.setData(Collections.emptyList());
                response.setStatus(0);
                response.setMessage("Record not inserted.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(0);
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setMessage(e.getMessage());
            response.setData(Collections.emptyList());
            log.error(e.getMessage());
        }*/
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ActivitySurveyInfo activityInfo = objectMapper.readValue(data, ActivitySurveyInfo.class);
            ActivitySurvey details = new ActivitySurvey();
            BeanUtils.copyProperties(activityInfo, details);
            details.setActive(true);
            details.setSurveyorImage(surveyorImage.getOriginalFilename());
            details.setImagePath(accessWorkProgressImagePath);
            ActivitySurvey saveActivity = activitySurveyRepository.save(details);
            boolean saveSurveyorImage = awss3StorageService.uploadSurveyorImage(surveyorImage, String.valueOf(saveActivity.getId()), surveyorImage.getOriginalFilename());
//            List<ActivitySurveyImage> image = new ActivitySurveyImage();
            List<ActivitySurveyImage> image = new ArrayList<>();
            BeanUtils.copyProperties(activityInfo.getActivitySurveyImageRequest(), image);
//            image.setSurveyId(saveActivity.getId());
            List<ActivitySurveyImage> activityImageObj = null;

            if (activityImages.length > 0) {
                for (MultipartFile mult : activityImages) {
                    ActivitySurveyImage img = new ActivitySurveyImage();
                    img.setActive(true);
                    img.setImageLocation(accessWorkProgressImagePath);
                    img.setImageName(mult.getOriginalFilename());
                    //img.setLatitude(image.get(0).getLatitude());
                    img.setSurveyId(saveActivity.getId());
                    awss3StorageService.uploadWorkProgressImage(mult, String.valueOf(saveActivity.getId()), mult.getOriginalFilename());
                    image.add(img);
                }
                activitySurveyImageRepository.saveAll(image);
            }
            result.put("Activity", saveActivity);
            result.put("SurveyorImage", saveSurveyorImage);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New WorkProgress Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    /**
     * Update Activity info
     */
    @Override
    public OIIPCRAResponse UpdateActivitySurveyById(String data) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        OIIPCRAResponse response = new OIIPCRAResponse();
        ActivitySurveyInfo activityInfo = mapper.readValue(data, ActivitySurveyInfo.class);

        try {
            Optional<ActivitySurvey> activityData = activitySurveyRepository.findById(activityInfo.getId());
            if (activityData == null) {
                throw new RecordNotFoundException("activityData", "id", activityData);
            }
            if (activityData != null) {
                ActivitySurvey activity = new ActivitySurvey();
                BeanUtils.copyProperties(activityInfo, activity);
                activity.setUpdatedBy(activityInfo.getUpdatedBy());
                activitySurveyRepository.save(activity);
                response.setData(Collections.emptyList());
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Activity Info Updated Successfully");
                log.info("Activity Info Updated Successfully");

            } else {
                response.setData(Collections.emptyList());
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Activity Info Not Found...please survey the Activity");
                log.info("Activity Info Not Found");
            }

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

    /**
     * Activity search List
     */
    @Transactional
    @Override
    public OIIPCRAResponse searchActivityList(ActivitySearchRequest activitySearchRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<ActivitySurveyListInfo> activityListPage = surveyRepositoy.searchActivityList(activitySearchRequest);
            List<ActivitySurveyListInfo> activityList = activityListPage.getContent();
            result.put("activityList", activityList);
            result.put("currentPage", activityListPage.getNumber());
            result.put("totalItems", activityListPage.getTotalElements());
            result.put("totalPages", activityListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of WorkProgress.");
        } catch (Exception e) {
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @Override
    public OIIPCRAResponse getActivityById(Integer activityId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            String imagePath = accessWorkProgressImagePath + activityId ;
            ActivityInfo activityInfo = masterQryRepository.getActivityById(activityId);
            activityInfo.setImagePath(imagePath + "/" + activityInfo.getSurveyorImage());
            List<ActivityImageInfo> imageInfo = masterQryRepository.getActivityImagesById(activityId);
            for (int i = 0; i < imageInfo.size(); i++) {
                imageInfo.get(i).setImageLocation(imagePath + "/" + imageInfo.get(i).getImageName());
            }
            if (activityInfo != null) {
                result.put("Activity", activityInfo);
                result.put("ActivityImage", imageInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Activity By Id");
            } else {
                result.put("Activity", activityInfo);
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


    @Override
     @Transactional
    public Page<ContractInfoListing> getContractList(ContractListRequestDto contractInfo) {
//        Integer parentId=0;
//        if(contractInfo.getComponentId() >0){
//            parentId=contractInfo.getComponentId();
//        }if(contractInfo.getSubComponentId() >0){
//             parentId=contractInfo.getSubComponentId();
//         }
//         if(contractInfo.getActivityId() >0){
//             parentId=contractInfo.getActivityId();
//         }
//         if(contractInfo.getSubActivityId() >0){
//             parentId=contractInfo.getSubActivityId();
//         }
//         List<Integer> terminalList=activityService.getTerminalIds(parentId);
        List<Integer> activityIds=new ArrayList<>();
        List<Integer> estimateIds=new ArrayList<>();
        List<Integer> tenderIds=new ArrayList<>();
        List<Integer> tenderNoticeIds=new ArrayList<>();
        List<Integer> contractIdsByExpenditureId = new ArrayList<>();
        List<Integer> contractIdsByInvoiceId = new ArrayList<>();

        if(contractInfo.getTankId()!=null && contractInfo.getTankId()>0 ) {
            if (contractInfo.getExpenditureId() != null && contractInfo.getExpenditureId() > 0) {
                contractIdsByExpenditureId = expenditureQueryRepo.getContractIdsByExpenditureId(contractInfo.getExpenditureId());
            }

            if (contractInfo.getInvoiceId() != null && contractInfo.getInvoiceId() > 0) {
                contractIdsByInvoiceId = expenditureQueryRepo.getContractIdsByInvoiceId(contractInfo.getInvoiceId());
            }

            if (contractInfo.getActivityId() != null && contractInfo.getActivityId() > 0) {
                activityIds = masterQryRepository.getActivityIdsByTankId(contractInfo);
            }


            if (contractInfo.getEstimateId() != null && contractInfo.getEstimateId() > 0) {
                estimateIds = masterQryRepository.getEstimateIdsByTankId(contractInfo.getTankId(), contractInfo.getActivityId());
            }

            if (contractInfo.getTenderId() != null && contractInfo.getTenderId() > 0) {
                tenderIds = masterQryRepository.getTenderIdsByTankId(contractInfo);
            }

            if (contractInfo.getTenderNoticeId() != null && contractInfo.getTenderNoticeId() > 0) {
                tenderNoticeIds = masterQryRepository.getTenderNoticeIdsByTankId(contractInfo);
            }
        }

//        if(contractInfo.getTankId()!=null && contractInfo.getTankId()>0 ){
//            if(contractInfo.getActivityId()!=null && contractInfo.getActivityId()>0){
//                activityIds.add(contractInfo.getActivityId());
//            }
//            else{
//                activityIds=masterQryRepository.getActivityIdsByTankId(contractInfo);
//            }
//
//            estimateIds=masterQryRepository.getEstimateIdsByTankId(contractInfo.getTankId(),contractInfo.getActivityId());
//            tenderIds=masterQryRepository.getTenderIdsByTankId(contractInfo);
//            tenderNoticeIds=masterQryRepository.getTenderNoticeIdsByTankId(contractInfo);
//
//        }

        else {
            if(contractInfo.getActivityId() != null && contractInfo.getActivityId()>0) {
                activityIds.add(contractInfo.getActivityId());
            }

            if (contractInfo.getExpenditureId() != null && contractInfo.getExpenditureId() > 0) {
                contractIdsByExpenditureId = expenditureQueryRepo.getContractIdsByExpenditureId(contractInfo.getExpenditureId());
            }

            if(contractInfo.getEstimateId() != null && contractInfo.getEstimateId()>0) {
                estimateIds.add(contractInfo.getEstimateId());
            }
            if(contractInfo.getTenderId() != null && contractInfo.getTenderId()>0) {
                tenderIds.add(contractInfo.getTenderId());
            }
            if(contractInfo.getTenderNoticeId() != null && contractInfo.getTenderNoticeId()>0) {
                tenderNoticeIds.add(contractInfo.getTenderNoticeId());
            }
        }

         Page<ContractInfoListing> contractList=  surveyRepositoy.getContractList(contractInfo,activityIds,estimateIds,
                 tenderIds,tenderNoticeIds,contractIdsByExpenditureId,contractIdsByInvoiceId);
         return contractList;
    }

    @Override
    public List<ContractMappingDto> getContractMapping(Integer contractId) {
        return surveyRepositoy.getContractMapping(contractId);
    }

    public Page<invoiceListingInfo> getInvoiceList(InvoiceListRequestDto invoiceListRequestDto) {
        Integer parentId = 0;
        if (invoiceListRequestDto.getComponentId() > 0) {
            parentId = invoiceListRequestDto.getComponentId();
        }
        if (invoiceListRequestDto.getSubComponentId()!=null && invoiceListRequestDto.getSubComponentId() > 0) {
            parentId = invoiceListRequestDto.getSubComponentId();
        }
        if (invoiceListRequestDto.getActivityId() > 0) {
            parentId = invoiceListRequestDto.getActivityId();
        }
        if (invoiceListRequestDto.getSubActivityId() > 0) {
            parentId = invoiceListRequestDto.getSubActivityId();
        }

        List<Integer> terminalList = activityService.getTerminalIds(parentId);
        Page<invoiceListingInfo> invoiceList = surveyRepositoy.getInvoiceList(invoiceListRequestDto, terminalList);
        return invoiceList;
    }

    @Transactional
    @Override
    public OIIPCRAResponse searchTankList(SurveyListRequest surveyListRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        Page<TankInfo> tankListPage = getTankList(surveyListRequest);
        List<TankInfo> tankInfoList = tankListPage.getContent();
        try {
            List<TankInfo> tankList = tankListPage.getContent();
            result.put("tankList", tankList);
            result.put("currentPage", tankListPage.getNumber());
            result.put("totalItems", tankListPage.getTotalElements());
            result.put("totalPages", tankListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks.");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
//    @Transactional
//    @Override
    public Page<TankInfo> getTankList(SurveyListRequest surveyListRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> tankIdsByContractId = new ArrayList<>();
            List<Integer> tankIdsByExpenditureId = new ArrayList<>();
            List<Integer> tankIdsByInvoiceId = new ArrayList<>();
            List<Integer> tankIdsByEstimateId = new ArrayList<>();
            List<Integer> tankIdsByIssueId = new ArrayList<>();
            List<Integer> cadTankIds = new ArrayList<>();
            List<Integer> surveyTankIds = new ArrayList<>();
            List<Integer> depthTankIds = new ArrayList<>();
            List<Integer> feederTankIds = new ArrayList<>();
            List<Integer> civilTankIds=new ArrayList<>();
            List<Integer> fopTankIds=new ArrayList<>();
            List<Integer> droppedTankIds=new ArrayList<>();
            List<Integer> proposedToBeDroppedTankIds=new ArrayList<>();
            List<Integer> progressTankIds = new ArrayList<>();

            if(surveyListRequest.getContractId()!=null && surveyListRequest.getContractId()>0){
                tankIdsByContractId = surveyRepositoy.getTankIdsByContractId(surveyListRequest.getContractId());
            }
            if(surveyListRequest.getExpenditureId()!=null && surveyListRequest.getExpenditureId()>0){
                tankIdsByExpenditureId = surveyRepositoy.getTankIdsByExpenditureId(surveyListRequest.getExpenditureId());
            }
            if( surveyListRequest.getInvoiceId()!=null && surveyListRequest.getInvoiceId()>0 ){
                tankIdsByInvoiceId = surveyRepositoy.getTankIdsByInvoiceId(surveyListRequest.getInvoiceId());
            }
            if( surveyListRequest.getEstimateId()!=null && surveyListRequest.getEstimateId()>0 ){
                tankIdsByEstimateId = surveyRepositoy.getTankIdsByEstimateId(surveyListRequest.getEstimateId());
            }
            if( surveyListRequest.getIssueId()!=null && surveyListRequest.getIssueId()>0 ){
                tankIdsByIssueId = surveyRepositoy.getTankIdsByIssueId(surveyListRequest.getIssueId());
            }
            //Cad Survey or not
            if(surveyListRequest.getIsCadSurveyed()!= null && surveyListRequest.getIsCadSurveyed() >0){
                cadTankIds = surveyRepositoy.getTankIdsForCad();
            }
            if(surveyListRequest.getIsCadSurveyed()!= null && surveyListRequest.getIsCadSurveyed() <0){
                cadTankIds = surveyRepositoy.getTankIdsForNotSurveyCad();
            }
            //tank Survey Or not
            if(surveyListRequest.getIsTankSurveyed() != null && surveyListRequest.getIsTankSurveyed() >0){
                surveyTankIds = surveyRepositoy.getSurveyTankIds();
            }
            if(surveyListRequest.getIsTankSurveyed() != null && surveyListRequest.getIsTankSurveyed() <0){
                surveyTankIds = surveyRepositoy.getNotSurveyTankIds();
            }
            //depth Survey or not
            if(surveyListRequest.getIsDepthSurveyed() != null && surveyListRequest.getIsDepthSurveyed() >0){
                depthTankIds = surveyRepositoy.getTankIdsForDepth();
            }
            if(surveyListRequest.getIsDepthSurveyed() != null && surveyListRequest.getIsDepthSurveyed() <0){
                depthTankIds = surveyRepositoy.getTankIdsForNotSurveyDepth();
            }
            //feeder Survey or not
            if(surveyListRequest.getIsFeederSurveyed() != null && surveyListRequest.getIsFeederSurveyed() >0){
                feederTankIds = surveyRepositoy.getTankIdsForFeeder();
            }
            if(surveyListRequest.getIsFeederSurveyed() != null && surveyListRequest.getIsFeederSurveyed() <0){
                feederTankIds = surveyRepositoy.getTankIdsForNotSurveyFeeder();
            }
            //civil work completed or not
            if(surveyListRequest.getIsCivilWorkCompleted() != null && surveyListRequest.getIsCivilWorkCompleted() >0){
                civilTankIds = surveyRepositoy.getTankIdsForCivilWork();
            }
            if(surveyListRequest.getIsCivilWorkCompleted() != null && surveyListRequest.getIsCivilWorkCompleted() <0){
                civilTankIds = surveyRepositoy.getTankIdsForCivilWorkNotCompleted();
            }
            //fop added or not
            if(surveyListRequest.getIsFpoAdded() != null && surveyListRequest.getIsFpoAdded() >0){
                fopTankIds = surveyRepositoy.getTankIdsForFop();
            }
            if(surveyListRequest.getIsFpoAdded()  != null && surveyListRequest.getIsFpoAdded()  <0){
                fopTankIds = surveyRepositoy.getTankIdsForFopNotAdded();
            }
            //Is dropped or not
            if(surveyListRequest.getIsDropped() != null && surveyListRequest.getIsDropped() >0){
                droppedTankIds = surveyRepositoy.getTankIdsForDropped();
            }
            if(surveyListRequest.getIsDropped()  != null && surveyListRequest.getIsDropped()  <0){
                droppedTankIds = surveyRepositoy.getTankIdsForNotDropped();
            }
            //proposed to be dropped or not
            if(surveyListRequest.getProposedToBeDroppedTank() != null && surveyListRequest.getProposedToBeDroppedTank() >0){
                proposedToBeDroppedTankIds = surveyRepositoy.getTankIdsProposedToBeDropped();
            }
            if(surveyListRequest.getProposedToBeDroppedTank()  != null && surveyListRequest.getProposedToBeDroppedTank()  <0){
                proposedToBeDroppedTankIds = surveyRepositoy.getTankIdsProposedToBeNotDropped();
            }
            //progressStatus wise tankIds
            if(surveyListRequest.getProgressStatus() != null && surveyListRequest.getProgressStatus() >0){
                progressTankIds = surveyRepositoy.getTankIdsProgressStatusWise(surveyListRequest.getProgressStatus());
            }


            Page<TankInfo> tankListPage = surveyRepositoy.getTankSearchList(surveyListRequest,tankIdsByContractId,tankIdsByExpenditureId,tankIdsByInvoiceId,tankIdsByEstimateId,tankIdsByIssueId,cadTankIds,surveyTankIds,depthTankIds,feederTankIds,civilTankIds,fopTankIds,droppedTankIds,proposedToBeDroppedTankIds,progressTankIds);
            return tankListPage;
            //            List<TankInfo> tankList = tankListPage.getContent();
//            result.put("tankList", tankList);
//            result.put("currentPage", tankListPage.getNumber());
//            result.put("totalItems", tankListPage.getTotalElements());
//            result.put("totalPages", tankListPage.getTotalPages());
//            response.setData(result);
//            response.setStatus(1);
//            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
//            response.setMessage("List of Tanks.");
        }catch (Exception e){
            e.printStackTrace();
            Page<TankInfo> tankListPage = null;
            return tankListPage;
//            log.info("Tank List Exception : {}", e.getMessage());
//            response = new OIIPCRAResponse(0,
//                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
//                    e.getMessage(),
//                    result);
        }
//        return response;

    }
    @Transactional
    @Override
    public OIIPCRAResponse tank538masterSearchList(SurveyListRequest surveyListRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> tankIdsByContractId = new ArrayList<>();
            if(surveyListRequest.getContractId()!=null && surveyListRequest.getContractId()>0){
                tankIdsByContractId = surveyRepositoy.getTankIdsByContractId(surveyListRequest.getContractId());
            }
           /* List<Integer> tankIdsByContractId = new ArrayList<>();
            List<Integer> tankIdsByExpenditureId = new ArrayList<>();
            List<Integer> tankIdsByInvoiceId = new ArrayList<>();
            List<Integer> tankIdsByEstimateId = new ArrayList<>();

            if(surveyListRequest.getContractId()!=null && surveyListRequest.getContractId()>0){
                tankIdsByContractId = surveyRepositoy.getTankIdsByContractId(surveyListRequest.getContractId());
            }
            if(surveyListRequest.getExpenditureId()!=null && surveyListRequest.getExpenditureId()>0){
                tankIdsByExpenditureId = surveyRepositoy.getTankIdsByExpenditureId(surveyListRequest.getExpenditureId());
            }
            if( surveyListRequest.getInvoiceId()!=null && surveyListRequest.getInvoiceId()>0 ){
                tankIdsByInvoiceId = surveyRepositoy.getTankIdsByInvoiceId(surveyListRequest.getInvoiceId());
            }
            if( surveyListRequest.getEstimateId()!=null && surveyListRequest.getEstimateId()>0 ){
                tankIdsByEstimateId = surveyRepositoy.getTankIdsByEstimateId(surveyListRequest.getEstimateId());
            }
*/

            Page<MasterData538Dto> tankListPage = surveyRepositoy.tank538masterSearchList(surveyListRequest,tankIdsByContractId);
            List<MasterData538Dto> tankList = tankListPage.getContent();
            result.put("tankList", tankList);
            result.put("currentPage", tankListPage.getNumber());
            result.put("totalItems", tankListPage.getTotalElements());
            result.put("totalPages", tankListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks.");
        }catch (Exception e){
            e.printStackTrace();
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }
    @Transactional
    @Override
    public OIIPCRAResponse tankSearchListForWebsite(Integer blockId,Integer start,Integer length,Integer draw) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            Page<TankInfo> tankListPage = surveyRepositoy.tankSearchListForWebsite( blockId, start, length, draw);
            List<TankInfo> tankList = tankListPage.getContent();
            result.put("tankList", tankList);
            result.put("currentPage", tankListPage.getNumber());
            result.put("totalItems", tankListPage.getTotalElements());
            result.put("totalPages", tankListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks.");
        }catch (Exception e){
            e.printStackTrace();
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }
    @Override
    public OIIPCRAResponse tankCount(SurveyListRequest surveyListRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> tankIdsByContractId = new ArrayList<>();
            List<Integer> tankIdsByExpenditureId = new ArrayList<>();
            List<Integer> tankIdsByInvoiceId = new ArrayList<>();

            if(surveyListRequest.getContractId()!=null && surveyListRequest.getContractId()>0){
                tankIdsByContractId = surveyRepositoy.getTankIdsByContractId(surveyListRequest.getContractId());
            }
            if(surveyListRequest.getExpenditureId()!=null && surveyListRequest.getExpenditureId()>0){
                tankIdsByExpenditureId = surveyRepositoy.getTankIdsByExpenditureId(surveyListRequest.getExpenditureId());
            }
            if(surveyListRequest.getInvoiceId()!=null && surveyListRequest.getInvoiceId()>0){
                tankIdsByInvoiceId = surveyRepositoy.getTankIdsByInvoiceId(surveyListRequest.getInvoiceId());
            }


            List<TankInfo>  tankCountByDistrict= surveyRepositoy.tankCountByDistrict(surveyListRequest,tankIdsByContractId,tankIdsByExpenditureId,tankIdsByInvoiceId);
            List<TankInfo>  tankCountByBlock= surveyRepositoy.tankCountByBlock(surveyListRequest,tankIdsByContractId,tankIdsByExpenditureId,tankIdsByInvoiceId);
            List<TankInfo>  tankCountByDivision= surveyRepositoy.tankCountByDivision(surveyListRequest,tankIdsByContractId,tankIdsByExpenditureId,tankIdsByInvoiceId);
            List<TankInfo>  tankCountByCircle= surveyRepositoy.tankCountByCircle(surveyListRequest,tankIdsByContractId,tankIdsByExpenditureId,tankIdsByInvoiceId);
            result.put("tankCountByDistrict", tankCountByDistrict);
            result.put("tankCountByBlock",tankCountByBlock);
            result.put("tankCountByDivision",tankCountByDivision);
            result.put("tankCountByCircle",tankCountByCircle);
        /*    result.put("currentPage", tankListPage.getNumber());
            result.put("totalItems", tankListPage.getTotalElements());
            result.put("totalPages", tankListPage.getTotalPages());*/
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Tank Count.");
        }catch (Exception e){
            e.printStackTrace();
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }


    @Transactional
    @Override
    public OIIPCRAResponse getTankNameAndProjectId(int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            if(userId>0) {
                List<TankInfo> tankListPage = surveyRepositoy.getTankNameAndProjectId(userId);
                if (tankListPage != null) {
                    result.put("tankName", tankListPage);
                }
            }else{
                response.setData(Collections.emptyList());
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                response.setMessage("User does not exist");
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks and project id");
        }catch (Exception e){
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }
    @Transactional
    @Override
    public OIIPCRAResponse getSurveyTankNameAndProjectId(int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            if(userId>0) {
                List<TankInfo> tankListPage = surveyRepositoy.getSurveyTankNameAndProjectId(userId);
                if (tankListPage != null) {
                    result.put("tankName", tankListPage);
                }
            }else{
                response.setData(Collections.emptyList());
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                response.setMessage("User does not exist");
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks and project id");
        }catch (Exception e){
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @Override
    public OIIPCRAResponse activityInfoSearchList(String data, int activityId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //Page<TankInfo> tankListPage = surveyRepositoy.activityInfoSearchList(activityId);
            //List<TankInfo> tankList = tankListPage.getContent();
            //result.put("tankList", tankList);
            //result.put("currentPage", tankListPage.getNumber());
            //result.put("totalItems", tankListPage.getTotalElements());
            //result.put("totalPages", tankListPage.getTotalPages());
            List<Integer> allTankIds = new ArrayList<>();
            List<Integer> tankIds = new ArrayList<>();
            List<Integer> distIds = new ArrayList<>();
            List<Integer> blockIds = new ArrayList<>();
            List<Integer> estimatesByActivityId = surveyRepositoy.getEstimatesByActivityId(activityId);
            if(estimatesByActivityId != null && estimatesByActivityId.size() > 0) {
                tankIds = surveyRepositoy.getTankIds(estimatesByActivityId);
                distIds = surveyRepositoy.getDistIds(estimatesByActivityId);
                blockIds = surveyRepositoy.getBlockIds(estimatesByActivityId);

                if (distIds.size() > 0) {
                    List<Integer> tankIdsByDistIds = surveyRepositoy.getTankIdsByDistIds(distIds);
                    allTankIds.addAll(tankIdsByDistIds);
                }
                if (blockIds.size() > 0) {
                    List<Integer> tankIdsByBlockIds = surveyRepositoy.getTankIdsByBlockIds(blockIds);
                    allTankIds.addAll(tankIdsByBlockIds);
                }
                List<Integer> tenderIdsByEstimateIds = surveyRepositoy.getTendersByEstimateIds(estimatesByActivityId);
                if (tenderIdsByEstimateIds.size() > 0) {
                    List<Integer> tankIdsByTenderId = surveyRepositoy.getTankIdsByTenderId(tenderIdsByEstimateIds);
                    allTankIds.addAll(tankIdsByTenderId);
                }
                if (tankIds.size() > 0) {
                    allTankIds.addAll(tankIds);
                }
                SurveyListRequest surveyListRequest = objectMapper.readValue(data, SurveyListRequest.class);
                List<Integer> tankIdsByContractId = new ArrayList<>();
                if (surveyListRequest.getContractId() > 0) {
                    tankIdsByContractId = surveyRepositoy.getTankIdsByContractId(surveyListRequest.getContractId());
                    allTankIds.addAll(tankIdsByContractId);
                }
                Page<TankInfo> tankListPage = surveyRepositoy.getTankSearchListWithTankIds(surveyListRequest, allTankIds);
                List<TankInfo> tankList = tankListPage.getContent();
                result.put("tankList", tankList);
                result.put("currentPage", tankListPage.getNumber());
                result.put("totalItems", tankListPage.getTotalElements());
                result.put("totalPages", tankListPage.getTotalPages());
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("List of Tanks.");
            } else {
                response.setData(null);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("No data found.");
            }

        }catch (Exception e){
            e.printStackTrace();
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @Override
    public Page<WorkProgressInfo> getWorkProgressList(WorkProgressDto workProgressDto) {
        List<Integer> activityIds = new ArrayList<>();
        List<Integer> estimateIds= new ArrayList<>();
        List<Integer> tankIdsByExpenditureId = new ArrayList<>();
        List<Integer> tankIdsByInvoiceId = new ArrayList<>();
        List<Integer> tenderIds = new ArrayList<>();
        List<Integer> tenderNoticeIds= new ArrayList<>();
        List<Integer> contractIds = new ArrayList<>();


        if (workProgressDto.getTankId() != null && workProgressDto.getTankId() > 0) {
            if (workProgressDto.getActivityId() > 0) {
                activityIds.add(workProgressDto.getActivityId());
            } else {
                activityIds = surveyRepositoy.getActivityIdsByTankId(workProgressDto);
            }
                estimateIds = masterQryRepository.getEstimateIdsByTankId(workProgressDto.getTankId(), workProgressDto.getActivityId());
        }
              else {
            if (workProgressDto.getActivityId()!=null && workProgressDto.getActivityId() > 0) {
                activityIds.add(workProgressDto.getActivityId());
            }
            if (workProgressDto.getEstimateId()!=null && workProgressDto.getEstimateId() > 0) {
                estimateIds.add(workProgressDto.getEstimateId());
            }

            if(workProgressDto.getExpenditureId()!=null && workProgressDto.getExpenditureId()>0){
                tankIdsByExpenditureId = surveyRepositoy.getTankIdsByExpenditureId(workProgressDto.getExpenditureId());
            }

            if(workProgressDto.getInvoiceId()!= null && workProgressDto.getInvoiceId()>0){
                tankIdsByInvoiceId = surveyRepositoy.getTankIdsByInvoiceId(workProgressDto.getInvoiceId());
            }
            if(workProgressDto.getTenderId()!= null && workProgressDto.getTenderId() >0) {
                tenderIds = expenditureQueryRepo.getTenderIdsByTankId(workProgressDto.getTenderId());
            }
            if(workProgressDto.getContractId()!=null && workProgressDto.getContractId() > 0)
              contractIds = expenditureQueryRepo.getContractIdsByTankId(workProgressDto.getContractId());


        }

            Page<WorkProgressInfo> workProgressList = surveyRepositoy.getWorkProgressList(workProgressDto,activityIds,estimateIds,tankIdsByExpenditureId,tankIdsByInvoiceId,tenderIds,contractIds);
            return workProgressList;
        }


    @Override
    public List<Tender> getAllClosedBidId() {
        return surveyRepositoy.getAllClosedBidId();
    }

    @Override
    public WorkStatusDto getWorkStatusDetails(Integer tenderId,Integer workId) {
        return surveyRepositoy.getWorkStatusDetails(tenderId,workId);
    }

    @Override
    public List<Tender> getAllResultDeclaredBidId() {
        return surveyRepositoy.getAllResultDeclaredBidId();
    }

    @Override
    public FeederEntity saveFeeder(FeederDto feederDto) {
        FeederEntity feederEntity1= new FeederEntity();
        BeanUtils.copyProperties(feederDto,feederEntity1);
        feederEntity1.setSurveyorImage(feederDto.getSurveyorImage());
        feederEntity1.setActive(true);
        return feederRepository.save(feederEntity1);
    }

    @Override
    public List<FeederLocationEntity> createFeederLocation(List<FeederLocationEntity> feederLocation, Integer id) {
        for (FeederLocationEntity feederLocation2 : feederLocation) {
            feederLocation2.setFeederId(id);
            feederLocation2.setActive(true);
        }
        return feederLocationRepository.saveAll(feederLocation);
    }

    @Override
    public DepthEntity saveDepth(DepthDto depthDto) {
        DepthEntity depthEntity1= new DepthEntity();
        BeanUtils.copyProperties(depthDto,depthEntity1);
        depthEntity1.setActive(true);
        depthEntity1.setImage(depthEntity1.getImage());
        return depthRepository.save(depthEntity1);
    }

    public CadEntity saveCad(CadDto cadDto) {
        CadEntity CadEntity1= new CadEntity();
        BeanUtils.copyProperties(cadDto,CadEntity1);
        return cadRepository.save(CadEntity1);
    }

    @Override
    public List<CadLocationEntity> createCadLocation(List<CadLocationEntity> cadLocation, Integer id) {
        for (CadLocationEntity cadLocation1 : cadLocation) {
            cadLocation1.setCadId(id);
            cadLocation1.setIsActive(true);
        }
        return cadLocationRepository.saveAll(cadLocation);
    }


    @Override
    public List<FeederImage> saveFeederImage( Integer id, MultipartFile[] surveyImages) {
        List<FeederImage> image = new ArrayList<>();
        for(MultipartFile multipartFile : surveyImages) {

            FeederImage feederImage1 = new FeederImage();
            feederImage1.setFeederId(id);
            feederImage1.setActive(true);
            feederImage1.setImageName(accessFeederImagePath + "/" +id+"/"+ multipartFile.getOriginalFilename());
            feederImageRepository.save(feederImage1);
            image.add(feederImage1);
        }

        return image;
    }

    @Override
    public List<DepthImageEntity> saveDepthImages(Integer id, MultipartFile[] surveyImages) {
        List<DepthImageEntity> image = new ArrayList<>();
        for(MultipartFile multipartFile : surveyImages){

            DepthImageEntity depthImage = new DepthImageEntity();
            depthImage.setDepthId(id);
            depthImage.setActive(true);
            depthImage.setImageName(accessDepthImagePath + "/" +id+"/"+ multipartFile.getOriginalFilename());
            depthImageRepository.save(depthImage);
            image.add(depthImage);
        }
        return image;
    }

    @Override
    public  List<DepthDto> getDepthIdByTankId(Integer tankId) {
        return surveyRepositoy.getDepthIdByTankId(tankId);
    }

    @Override
    public List<DepthImageDto> getDepthImagesByDepthId(Integer depthId) {
        return surveyRepositoy.getDepthImagesByDepthId(depthId);
    }

    @Override
    public List<CadImageEntity> saveCadImage(Integer id, MultipartFile[] surveyImages) {
        List<CadImageEntity> image = new ArrayList<>();
        for(MultipartFile multipartFile : surveyImages){

            CadImageEntity cadImage = new CadImageEntity();
            cadImage.setCadId(id);
            cadImage.setIsActive(true);
            cadImage.setImageName(accessCadImagePath + "/" +id+"/"+ multipartFile.getOriginalFilename());
            cadImageRepository.save(cadImage);
            image.add(cadImage);
        }
        return image;
    }

    @Override
    public List<FeederDto> getFeederDetails(Integer tankId, Integer typeId) {
        List<FeederDto> feederDto = new ArrayList<>();
        if(typeId!=null  && typeId == 20){
            feederDto = surveyRepositoy.getFeederDetailsWithGeom(tankId, typeId);
        } else {
            feederDto = surveyRepositoy.getFeederDetails(tankId);
        }
        return feederDto;
    }

    @Override
    public List<FeederImageDto> getFeederImageByTankId(Integer feederId) {
        return surveyRepositoy.getFeederImageByTankId(feederId);
    }

    @Override
    public List<FeederLocationDto> getFeederLocation(Integer feederId) {
        return surveyRepositoy.getFeederLocation(feederId);
    }
    @Override
    public List<CadDto> getCadDetailsByTankId(Integer tankId) {
        List<CadDto> cadDto = new ArrayList<>();
//        if(typeId!=null ){
//            if( typeId == 19||typeId == 20) {
//                cadDto = surveyRepositoy.getCadDetailsByTankIdWithGeom(tankId, typeId);
//            }
//        } else {
            cadDto = surveyRepositoy.getCadDetailsByTankId(tankId);
//        }
        return cadDto;
    }

    @Override
    public List<CadLocationDto> getCadLocationByTankId(Integer id) {
        return surveyRepositoy.getCadLocationByTankId(id);
    }

    @Override
    public List<CadImageDto> getCadImageDetails(Integer id) {
        return surveyRepositoy.getCadImageDetails(id);
    }

    @Override
    public List<CadDto> getCadDetailsByCadId(Integer cadId) {
        return surveyRepositoy.getCadDetailsByCadId(cadId);
    }

    @Override
    public List<FeederDto> getFeederDetailsById(Integer feederId) {
        return surveyRepositoy.getFeederDetailsById(feederId);
    }

    @Override
    public List<FeederDto> getAllFeederDetails() {
        return surveyRepositoy.getAllFeederDetails();
    }

    @Override
    public List<CadDto> getAllCadDetails() {
        return surveyRepositoy.getAllCadDetails();
    }

    @Override
    public List<Integer> getCadIdsByTankId(int tankId) {
        return surveyRepositoy.getCadIdsByTankId(tankId);
    }

    @Override
    public List<Integer> getfeederIdsByTankId(Integer tankId) {
        return surveyRepositoy.getfeederIdsByTankId(tankId);
    }

    @Override
    public CadDto getAllCadByCadId(Integer cadId, Integer typeId) {
        CadDto cadDto = new CadDto();
        if(typeId != null && typeId == 21){
            cadDto = surveyRepositoy.getAllCadByCadId(cadId, typeId);
        }
        return cadDto;
    }

    @Override
    public FeederDto getAllFeederByFeederId(Integer feederId, Integer typeId) {
        FeederDto feederDto = new FeederDto();
        if(typeId != null && typeId == 22){
            feederDto = surveyRepositoy.getAllFeederByFeederId(feederId, typeId);
        }
        return feederDto;
    }


    @Override
    public TenderDto checkBidId(Integer tenderId) {
        return surveyRepositoy.checkBidId(tenderId);
    }

    @Override
    public List <ContractMappingDto>existBidId(int id) {
        return surveyRepositoy.existBidId(id);
    }

    public OIIPCRAResponse getTankListById(int id,int flagId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FpoDataDto> fpoData=new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.000");
            List<TankInfo> tankList = surveyRepositoy.getTankDetailsById(id,flagId);
            List<Integer> fpoId=surveyRepositoy.getFpoByTankId(id);
            if(fpoId!=null && fpoId.size()>0){
                fpoData=surveyRepositoy.getTankFpoData(fpoId);

            }
            result.put("fpoData", fpoData);


            for (TankInfo crop : tankList) {
                if(fpoData.size()>0){
                    tankList.get(0).setFpoAdded(true);
                }
                else{
                    tankList.get(0).setFpoAdded(false);
                }

                List<TankCropCycleMasterDto> tankCrop = surveyRepositoy.getTankCropDetailsByProjectId(crop.getProjectId());
                MasterData538Dto masterData = surveyRepositoy.getMasterDataByProjectId(crop.getProjectId());
                masterData.setTankWiseCatchmentArea(Double.valueOf(df.format(dashboardService.getTankWiseCatchmentArea(crop.getProjectId()))));
                masterData.setTankWiseAyacutArea(Double.valueOf(df.format(dashboardService.getTankWiseAyacutArea(crop.getProjectId()))));
                masterData.setTotalWaterSpreadArea(Double.valueOf(df.format(dashboardService.getTotalWaterSpreadArea(crop.getProjectId()))));
                if (tankList != null) {
                    result.put("tankList", tankList);
                    result.put("tankCrop",tankCrop);
                    result.put("masterData",masterData);
                }
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks details");
        } catch (Exception e) {
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }
    @Override
    public BigDecimal getMaxWaterSpreadData(String projectId) {
         return surveyRepositoy.getMaxWaterSpreadData(projectId);
    }

    @Override
    public BigDecimal getMinWaterSpreadData(String projectId) {
        return surveyRepositoy.getMinWaterSpreadData(projectId);
    }

    @Override
    public BigDecimal getAvgWaterSpreadData(String projectId) {
        return surveyRepositoy.getAvgWaterSpreadData(projectId);
    }

    @Override
    public BigDecimal getTotalWaterSpreadData(String projectId) {
        return surveyRepositoy.getTotalWaterSpreadData(projectId);
    }

    @Override
    public BigDecimal getLessThan50WaterSpreadData(String projectId) {
        return surveyRepositoy.getLessThan50WaterSpreadData(projectId);
    }

    @Override
    public List<ContractTypeDto> getAllContractType() {
        return surveyRepositoy.getAllContractType();
    }

    @Override
    public List<PhysicalProgressPlannedDto> getPlannedDetails(Integer contractId) {
        return surveyRepositoy.getPlannedDetails(contractId);
    }

    @Override
    public List<PhysicalProgressExecutedDto> getExecutedDetails(Integer contractId) {
        return surveyRepositoy.getExecutedDetails(contractId);
    }

    @Override
    public List<CadDto> getLatLongByCadId(Integer cadId) {
        return surveyRepositoy.getLatLongByCadId(cadId);
    }

    @Override
    public Integer updateCadGeom(List<CadDto> cadDto, Integer cadId) {
        return surveyRepositoy.updateCadGeom(cadDto, cadId);
    }

    @Override
    public List<FeederDto> getLatLongByFeederId(Integer feederId) {
        return surveyRepositoy.getLatLongByFeederId(feederId);
    }

    @Override
    public Integer updateFeederGeom(List<FeederDto> feederDto, Integer feederId) {
        return surveyRepositoy.updateFeederGeom(feederDto, feederId);
    }

    @Transactional
    @Override
    public OIIPCRAResponse getTankListByProjectId(int projectId,int flagId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TankInfo> tankList = surveyRepositoy.getTankDetailsByProjectId(projectId,flagId);
            if (tankList != null) {
                result.put("tankList", tankList);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Tanks details");
        } catch (Exception e) {
            log.info("Tank List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }
}
