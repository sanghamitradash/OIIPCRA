package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.ActivityQryRepository;
import com.orsac.oiipcra.repository.ContractDocumentRepository;
import com.orsac.oiipcra.repositoryImpl.MasterRepositoryImpl;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.ActivityService;
import com.orsac.oiipcra.service.MasterService;
import com.orsac.oiipcra.service.TenderService;
import com.orsac.oiipcra.serviceImpl.MasterServiceImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/master")
public class MasterController {


    /**
     * @author ${Prasnajit}
     */

    @Autowired
    private MasterService masterService;
    @Value("${accessDocumentPath}")
    private String accessDocumentPath;
    @Value("${saveAgencyImagePath}")
    private String saveAgencyImagePath;
    @Value("${accessAgencyImagePath}")
    private String accessAgencyImagePath;
    @Autowired
    private AWSS3StorageService awss3StorageService;

    @Autowired
    private ActivityService activityService;
    @Autowired
    private MasterServiceImpl masterServiceImpl;

    @Autowired
    private TenderService tenderService;

    @Autowired
    private ActivityController activityController;

    @Autowired
    private ActivityQryRepository activityQryRepository;

    @Autowired
    private MasterRepositoryImpl masterRepositoryImpl;
    @Autowired
    private ContractDocumentRepository contractDocumentRepository;
    @PostMapping("/mastersync")
    public CompletableFuture<OIIPCRAResponse> masterSync(@RequestBody Map<String, String> param) {
        return masterService.masterSync(Integer.parseInt(param.get("userId")), param.get("lastUpdateDate"));
    }


    @PostMapping("/approveStatus")
    public OIIPCRAResponse getApproveStatus(@RequestBody Map<String, String> param) {
        return masterService.getApprovalStatus(Integer.parseInt(param.get("currentApprovalId")));
    }

    /**
     * end by ${Prasnajit}
     */


    //Department CRUD
    @PostMapping("/getAllDepartments")
    public OIIPCRAResponse getAllDepartments() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DepartmentMaster> departmentList = masterService.getAllDepartment();
            if (!departmentList.isEmpty() && departmentList.size() > 0) {
                result.put("departmentList", departmentList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getAllAdaptDepartments")
    public OIIPCRAResponse getAllAdaptDepartments() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DepartmentMaster> departmentList = masterService.getAllAdaptDepartment();
            if (!departmentList.isEmpty() && departmentList.size() > 0) {
                result.put("departmentList", departmentList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    /* //Sub-Department CRUD
     @PostMapping("/getSubDepartmentByDeptId")
     public OIIPCRAResponse getSubDepartmentByDeptId(@RequestParam int deptId){
         OIIPCRAResponse response = new OIIPCRAResponse();
         Map<String, Object> result = new HashMap<>();
         try {
             List<SubDepartmentDto> subDepartmentList = masterService.getSubDepartmentByDeptId(deptId);
             if(!subDepartmentList.isEmpty() && subDepartmentList.size() >0){
                 result.put("subDepartmentList", subDepartmentList);
                 response.setData(result);
                 response.setStatus(1);
                 response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
             }else{
                 response.setData(result);
                 response.setStatus(1);
                 response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                 response.setMessage("Record not found.");
             }
         } catch (Exception e){
             response = new OIIPCRAResponse(0,
                     new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                     e.getMessage(),
                     result);
         }
         return response;
     }
 */
    @PostMapping("/getAllDistrict")
    public OIIPCRAResponse getAllDistrict(@RequestParam int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DistrictBoundaryDto> districtList = masterService.getAllDistrict(userId);
            if (!districtList.isEmpty() && districtList.size() > 0) {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getAllCircle")
    public OIIPCRAResponse getAllCircle(@RequestParam int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CircleDto> circleList = masterService.getCircleList(userId);
            if (!circleList.isEmpty() && circleList.size() > 0) {
                result.put("circleList", circleList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("circleList", circleList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllDistrictGeoJson")
    public OIIPCRAResponse getAllDistrictGeoJson(@RequestParam int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DistrictBoundaryDto> districtList = masterService.getAllDistrictGeoJson(userId);
            if (!districtList.isEmpty() && districtList.size() > 0) {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getDistrictGeoJsonByDistId")
    public OIIPCRAResponse getAllDistrictGeoJsonByDistId(@RequestParam Integer districtId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DistrictBoundaryDto> districtList = masterService.getAllDistrictGeoJsonByDistId(districtId);
            if (!districtList.isEmpty() && districtList.size() > 0) {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getBlocksByDistId")
    public OIIPCRAResponse getBlocksByDistId(@RequestParam int userId,
                                             @RequestParam int distId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BlockBoundaryDto> blockList = masterService.getBlocksByDistId(userId, distId);
            if (!blockList.isEmpty() && blockList.size() > 0) {
                result.put("blockList", blockList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("blockList", blockList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getBlocksByDistIdGeoJson")
    public OIIPCRAResponse getBlocksByDistIdGeoJson(@RequestParam int userId,
                                                    @RequestParam int distId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BlockBoundaryDto> blockList = masterService.getBlocksByDistIdGeoJson(userId, distId);
            if (!blockList.isEmpty() && blockList.size() > 0) {
                result.put("blockList", blockList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("blockList", blockList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getBlocksGeoJsonByBlockId")
    public OIIPCRAResponse getBlocksByDistIdGeoJson(@RequestParam Integer blockId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BlockBoundaryDto> blockList = masterService.getBlocksGeoJsonByBlockId(blockId);
            if (!blockList.isEmpty() && blockList.size() > 0) {
                result.put("blockList", blockList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("blockList", blockList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getGpByBlockId")
    public OIIPCRAResponse getGpByBlockId(@RequestParam int userId,
                                          @RequestParam int blockId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<GpBoundaryDto> gpList = masterService.getGpByBlockId(userId, blockId);
            if (!gpList.isEmpty() && gpList.size() > 0) {
                result.put("gpList", gpList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("gpList", gpList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getGpByBlockIdGeoJson")
    public OIIPCRAResponse getGpByBlockIdGeoJson(@RequestParam int userId,
                                                 @RequestParam int blockId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<GpBoundaryDto> gpList = masterService.getGpByBlockIdGeoJson(userId, blockId);
            if (!gpList.isEmpty() && gpList.size() > 0) {
                result.put("gpList", gpList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("gpList", gpList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getGpGeoJsonByGpId")
    public OIIPCRAResponse getGpGeoJsonByGpId(@RequestParam Integer gpId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<GpBoundaryDto> gpList = masterService.getGpGeoJsonByGpId(gpId);
            if (!gpList.isEmpty() && gpList.size() > 0) {
                result.put("gpList", gpList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("gpList", gpList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getVillageByGpId")
    public OIIPCRAResponse getVillageByGpId(@RequestParam int userId,
                                            @RequestParam int gpId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<VillageBoundaryDto> villageList = masterService.getVillageByGpId(userId, gpId);
            if (!villageList.isEmpty() && villageList.size() > 0) {
                result.put("villageList", villageList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("villageList", villageList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getVillageByGpIdGeoJson")
    public OIIPCRAResponse getVillageByGpIdGeoJson(@RequestParam int userId,
                                                   @RequestParam int gpId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<VillageBoundaryDto> villageList = masterService.getVillageByGpIdGeoJson(userId, gpId);
            if (!villageList.isEmpty() && villageList.size() > 0) {
                result.put("villageList", villageList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("villageList", villageList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getVillageGeoJsonByVillageId")
    public OIIPCRAResponse getVillageGeoJsonByVillageId(@RequestParam Integer villageId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<VillageBoundaryDto> villageList = masterService.getVillageGeoJsonByGpId(villageId);
            if (!villageList.isEmpty() && villageList.size() > 0) {
                result.put("villageList", villageList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("villageList", villageList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getDivisionByDistId")
    public OIIPCRAResponse getDivisionByDistId(@RequestParam int userId,
                                               @RequestParam int distId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DivisionBoundaryDto> divisionList = masterService.getDivisionByDistId(userId, distId);
            if (!divisionList.isEmpty() && divisionList.size() > 0) {
                result.put("divisionList", divisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("divisionList", divisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getDivisionByDistIdGeoJson")
    public OIIPCRAResponse getDivisionByDistIdGeoJson(@RequestParam int userId,
                                                      @RequestParam int distId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DivisionBoundaryDto> divisionList = masterService.getDivisionByDistIdGeoJson(userId, distId);
            if (!divisionList.isEmpty() && divisionList.size() > 0) {
                result.put("divisionList", divisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("divisionList", divisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getSubDivisionByDivisionId")
    public OIIPCRAResponse getSubDivisionByDivisionId(@RequestParam int userId,
                                                      @RequestParam int divisionId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<SubDivisionBoundaryDto> subDivisionList = masterService.getSubDivisionByDivisionId(userId, divisionId);
            if (!subDivisionList.isEmpty() && subDivisionList.size() > 0) {
                result.put("subDivisionList", subDivisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("subDivisionList", subDivisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getSubDivisionByDivisionIdGeoJson")
    public OIIPCRAResponse getSubDivisionByDivisionIdGeoJson(@RequestParam int userId,
                                                             @RequestParam int divisionId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<SubDivisionBoundaryDto> subDivisionList = masterService.getSubDivisionByDivisionIdGeoJson(userId, divisionId);
            if (!subDivisionList.isEmpty() && subDivisionList.size() > 0) {
                result.put("subDivisionList", subDivisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("subDivisionList", subDivisionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getSectionBySubDivisionId")
    public OIIPCRAResponse getSectionBySubDivisionId(@RequestParam int userId,
                                                     @RequestParam int subdivisionId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<SectionBoundaryDto> sectionList = masterService.getSectionBySubDivisionId(userId, subdivisionId);
            if (!sectionList.isEmpty() && sectionList.size() > 0) {
                result.put("sectionList", sectionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("sectionList", sectionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getSectionBySubDivisionIdGeoJson")
    public OIIPCRAResponse getSectionBySubDivisionIdGeoJson(@RequestParam int userId,
                                                            @RequestParam int subdivisionId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<SectionBoundaryDto> sectionList = masterService.getSectionBySubDivisionIdGeoJson(userId, subdivisionId);
            if (!sectionList.isEmpty() && sectionList.size() > 0) {
                result.put("sectionList", sectionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("sectionList", sectionList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTankMasterGeoJson")
    public OIIPCRAResponse getSectionBySubDivisionIdGeoJson(@RequestParam(defaultValue = "0", required = false) Integer distId,
                                                            @RequestParam(defaultValue = "0", required = false) Integer blockId,
                                                            @RequestParam(defaultValue = "0", required = false) Integer gpId,
                                                            @RequestParam(defaultValue = "0", required = false) Integer villageId,
                                                            @RequestParam(defaultValue = "0", required = false) Integer divisionId,
                                                            @RequestParam(defaultValue = "0", required = false) Integer subDivisionId,
                                                            @RequestParam(defaultValue = "0", required = false) Integer sectionId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TankInfo> tankInfoGeoJson = masterService.getTankInfoJson(distId, blockId, gpId, villageId, divisionId, subDivisionId, sectionId);
            result.put("tankInfoGeoJson", tankInfoGeoJson);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }

        return response;
    }


    //Role CRUD
    @PostMapping("/saveRole")
    public OIIPCRAResponse saveRole(@RequestBody Role role) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Role savedRole = masterService.saveRole(role);

            result.put("savedRole", savedRole);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));

        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getRoleByUserId")
    public OIIPCRAResponse getRoleByUserId(@RequestParam int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<RoleDto> roleList = masterService.getRoleByUserId(userId);
            if (!roleList.isEmpty() && roleList.size() > 0) {
                result.put("roleList", roleList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("roleList", roleList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getRoleByUserLevelId")
    public OIIPCRAResponse getRoleByUserLevelId(@RequestParam Integer userLevelId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<RoleDto> roleByUserLevelId = masterService.getRoleByUserLevelId(userLevelId);
            if (!roleByUserLevelId.isEmpty() && roleByUserLevelId.size() > 0) {
                result.put("RoleByUserLevelId", roleByUserLevelId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Role By UserLevelId");
            } else {
                result.put("RoleByUserLevelId", roleByUserLevelId);
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

    @PostMapping("/getRoleByRoleId")
    public OIIPCRAResponse getRoleByRoleId(@RequestParam Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<RoleDto> roleByRoleId = masterService.getRoleByRoleId(id);
            if (!roleByRoleId.isEmpty() && roleByRoleId.size() > 0) {
                result.put("roleByRoleId", roleByRoleId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Role By RoleId");
            } else {
                result.put("roleByRoleId", roleByRoleId);
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

    @PostMapping("/updateRole")
    public OIIPCRAResponse updateRole(@RequestParam Integer id,
                                      @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Role updateRole = mapper.readValue(data, Role.class);
            Role role = masterService.updateRole(id, updateRole);
            //Role updateRole = masterService.updateRole(updateRole);
            result.put("role", role);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //Department CRUD
    @PostMapping("/createDepartment")
    public OIIPCRAResponse saveDepartment(@RequestBody DepartmentMaster departmentMaster) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            DepartmentMaster deptObj = masterService.saveDept(departmentMaster);
            result.put("department", deptObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Department Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getAllDepartmentById")
    public OIIPCRAResponse getDepartment(@RequestParam Integer userId, @RequestParam Integer id,@RequestParam Boolean flag) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DepartmentDto> department = masterService.getDepartment(userId, id,flag);
            if (!department.isEmpty() && department.size() > 0) {
                result.put("department", department);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Department.");
            } else {
                result.put("department", department);
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

    @PostMapping("/updateDepartment")
    public OIIPCRAResponse updateDepartment(@RequestParam int id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            DepartmentMaster updateDepartment = mapper.readValue(data, DepartmentMaster.class);
            DepartmentMaster department = masterService.updateDept(id, updateDepartment);
            result.put("department", department);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Department Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //Sub-Department CRUD
    @PostMapping("/createSubDepartment")
    public OIIPCRAResponse saveSubDepartment(@RequestBody SubDepartmentMaster subDepartmentMaster) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            SubDepartmentMaster subDepMObj = masterService.saveSubDepartment(subDepartmentMaster);
            result.put("SubDepartmentMaster", subDepMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New SubDepartment Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getSubDepartmentByDeptId")
    public OIIPCRAResponse getSubDepartmentByDeptId(@RequestParam Integer deptId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<SubDepartmentDto> subDepartmentList = masterService.getSubDepartmentByDeptId(deptId);
            if (!subDepartmentList.isEmpty() && subDepartmentList.size() > 0) {
                result.put("subDepartmentList", subDepartmentList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("subDepartmentList", subDepartmentList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getSubDepartmentById")
    public OIIPCRAResponse getSubDepartmentById(@RequestParam Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<SubDepartmentDto> subDepartmentList = masterService.getSubDepartmentById(id);
            if (!subDepartmentList.isEmpty() && subDepartmentList.size() > 0) {
                result.put("subDepartmentById", subDepartmentList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("subDepartmentById", subDepartmentList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/updateSubDepartment")
    public OIIPCRAResponse updateSubDepartment(@RequestParam int id,
                                               @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            SubDepartmentMaster updateSubDepartment = mapper.readValue(data, SubDepartmentMaster.class);
            SubDepartmentMaster subDeptObj = masterService.updateSubDept(id, updateSubDepartment);

            result.put("subDeptObj", subDeptObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Updated Sub Department");

        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //UserLevel Master CRUD
    @PostMapping("/createUserLevel")
    public OIIPCRAResponse saveUserLevel(@RequestBody UserLevel userLevel) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            UserLevel userLevelObj = masterService.saveUserLevel(userLevel);
            result.put("userLevel", userLevelObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New UserLevel Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllUserLevel")
    public OIIPCRAResponse getAllUserLevel(@RequestParam int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserLevel> userLevelList = masterService.getAllUserLevel(userId);
            if (!userLevelList.isEmpty() && userLevelList.size() > 0) {
                result.put("userLevelList", userLevelList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("userLevelList", userLevelList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getUserLevelById")
    public OIIPCRAResponse getUserLevelById(@RequestParam int id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserLevel> userLevelList = masterService.getUserLevelById(id);
            if (!userLevelList.isEmpty() && userLevelList.size() > 0) {
                result.put("userLevelById", userLevelList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("userLevelById", userLevelList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/updateUserLevel")
    public OIIPCRAResponse updateUserLevel(@RequestParam int id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserLevel updateUserLevel = mapper.readValue(data, UserLevel.class);
            UserLevel usrLevelObj = masterService.updateUserLevel(id, updateUserLevel);
            result.put("userLevel", usrLevelObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("User Level Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //Menu Master
    @PostMapping("/createMenu")
    public OIIPCRAResponse saveMenu(@RequestBody MenuMaster menuMaster) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            MenuMaster menuMObj = masterService.saveMenu(menuMaster);
            result.put("menuMaster", menuMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Menu Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllMenu")
    public OIIPCRAResponse getAllMenu(@RequestParam Integer userId, @RequestParam Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<MenuDto> menu = masterService.getMenu(userId, id);
            if (!menu.isEmpty() && menu.size() > 0) {
                result.put("menu", menu);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Menu.");
            } else {
                result.put("menu", menu);
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

    @PostMapping("/updateMenu")
    public OIIPCRAResponse updateMenu(@RequestParam int id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            MenuMaster updateMenu = mapper.readValue(data, MenuMaster.class);
            MenuMaster menuObj = masterService.updateMenu(id, updateMenu);
            result.put("menu", menuObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Menu Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    //Unit Master
    @PostMapping("/createUnit")
    public OIIPCRAResponse saveUnit(@RequestBody UnitMaster unitMaster) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            UnitMaster unitMObj = masterService.saveUnit(unitMaster);
            result.put("unitMaster", unitMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Unit Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllUnit")
    public OIIPCRAResponse getAllUnit(@RequestParam Integer userId, @RequestParam Integer id,@RequestParam Boolean flag) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<UnitDto> unit = masterService.getUnit(userId, id,flag);
            if (!unit.isEmpty() && unit.size() > 0) {
                result.put("unit", unit);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Unit.");
            } else {
                result.put("unit", unit);
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

    @PostMapping("/updateUnit")
    public OIIPCRAResponse updateUnit(@RequestParam int id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            UnitMaster updateUnit = mapper.readValue(data, UnitMaster.class);
            UnitMaster unitMasterObj = masterService.updateUnit(id, updateUnit);
            result.put("unitMaster", unitMasterObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Unit Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //Agency Master CRUD
    @PostMapping("/createAgency")
    public OIIPCRAResponse saveAgency(@RequestParam(name = "data") String data,
                                      @RequestParam(required = false) MultipartFile files) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            AgencyDto agency = objectMapper.readValue(data, AgencyDto.class);
//            for (MultipartFile multipart : files) {
//                agency.setImageName(multipart.getOriginalFilename());
//                agency.setImagePath(saveIssueImagePath);
//            }
//            AgencyMaster agencyMObj = masterService.saveAgency(agency);
//            if(files.length>0) {
//                for (MultipartFile mult : files) {
//                    boolean saveDocument = awss3StorageService.uploadAgencyImage(mult, String.valueOf(agencyMObj.getId()), mult.getOriginalFilename());
//                }
//            }
            agency.setImageName(files.getOriginalFilename());
            agency.setImagePath(saveAgencyImagePath);

            AgencyMaster agencyMObj = masterService.saveAgency(agency);
            boolean saveDocument = awss3StorageService.uploadAgencyImage(files, String.valueOf(agencyMObj.getId()), files.getOriginalFilename());

            result.put("agencyMaster", agencyMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Agency Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllAgency")
    public OIIPCRAResponse getAllAgency(@RequestParam Integer userId,@RequestParam(required = false) Integer agencyId,@RequestParam(required = false) String panNo) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyInfo> agency = masterService.getAgency(userId,agencyId,panNo);
            if (!agency.isEmpty() && agency.size() > 0) {
                result.put("agency", agency);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Agency");
            } else {
                result.put("agency", agency);
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

    @PostMapping("/getAllAgencyList")
    public OIIPCRAResponse getAllAgencyList(@RequestBody AgencyListDto agencyListDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<AgencyInfo> allAgency = masterService.getAllAgencyList(agencyListDto);
            List<AgencyInfo> agencyList = allAgency.getContent();
           // List<AgencyInfo> agency = masterService.getAgency(userId,agencyId,panNo,distId,licenseClassId);
            if (!agencyList.isEmpty() && agencyList.size() > 0) {
                result.put("agency", agencyList);
                result.put("currentPage", allAgency.getNumber());
                result.put("totalItems",  allAgency.getTotalElements());
                result.put("totalPages",  allAgency.getTotalPages());
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Agency");
            } else {
                result.put("agency", agencyList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
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

    @PostMapping("/getAgencyById")
    public OIIPCRAResponse getAgencyById(@RequestParam Integer agencyId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            String documentPath = accessAgencyImagePath + agencyId + "/";
            AgencyInfo agency = masterService.getAgencyById(agencyId);
            if (agency != null) {
                agency.setImagePath(documentPath + "" + agency.getImageName());
                result.put("Agency", agency);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Agency By Id");
            } else {
                result.put("Agency", agency);
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

    @PostMapping("/updateAgency")
    public OIIPCRAResponse updateAgency(@RequestParam int id, @RequestParam(name = "data") String data,
                                        @RequestParam(required = false) MultipartFile files) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            AgencyMaster updateAgency = mapper.readValue(data, AgencyMaster.class);
            if (files != null && !files.isEmpty()) {
                updateAgency.setImageName(files.getOriginalFilename());
            }
            AgencyMaster agencyMasterObj = masterService.updateAgency(id, updateAgency);
            if (files != null && files.getSize() > 0) {
                boolean saveDocument = awss3StorageService.uploadAgencyImage(files, String.valueOf(agencyMasterObj.getId()), files.getOriginalFilename());
            }
            result.put("agencyMaster", agencyMasterObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Agency Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //WorkType Master
    @PostMapping("/createWorkType")
    public OIIPCRAResponse saveWorkType(@RequestBody WorkTypeMaster workTypeMaster) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            WorkTypeMaster workTypeMObj = masterService.saveWorkType(workTypeMaster);
            result.put("WorkTypeMaster", workTypeMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New WorkType Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllWorkType")
    public OIIPCRAResponse getAllWorkType(@RequestParam Integer userId, @RequestParam Integer id,@RequestParam boolean flag ) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<WorkTypeDto> workType = masterService.getWorkType(userId, id,flag);

            if (!workType.isEmpty() && workType.size() > 0) {
                result.put("workType", workType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All WorkType");
            } else {
                result.put("workType", workType);
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

    @PostMapping("/updateWorkType")
    public OIIPCRAResponse updateWorkType(@RequestParam int id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            WorkTypeMaster updateWorkType = mapper.readValue(data, WorkTypeMaster.class);
            WorkTypeMaster updateWorkTypeObj = masterService.updateWorkType(id, updateWorkType);
            result.put("updateWorkType", updateWorkTypeObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Work Type Updated");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    //Designation Master
    @PostMapping("/createDesignation")
    public OIIPCRAResponse saveSubDesignation(@RequestBody DesignationMaster designationMaster) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            DesignationMaster desgMObj = masterService.saveDesignation(designationMaster);
            result.put("DesignationMaster", desgMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Designation Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getDesignationByUserLevelId")
    public OIIPCRAResponse getDesignationByUserLevelId(@RequestParam int userLevelId,
                                                       @RequestParam int deptId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DesignationDto> designationList = masterService.getDesignationByUserLevelId(userLevelId, deptId);
            if (!designationList.isEmpty() && designationList.size() > 0) {
                result.put("designationList", designationList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("designationList", designationList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }


    @PostMapping("/getAllDesignationByUserLevelId")
    public OIIPCRAResponse getAllDesignationByUserLevelId(@RequestParam int userLevelId,
                                                       @RequestParam int deptId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> userIdList = new ArrayList<>();
            List<DesignationDto> designationList = masterService.getAllDesignationByUserLevelId(userLevelId, deptId);
            if (!designationList.isEmpty() && designationList.size() > 0) {
                result.put("designationList", designationList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("designationList", designationList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }


    @PostMapping("/getDesignationInfoById")
    public OIIPCRAResponse getDesignationInfoById(@RequestParam int id,@RequestParam boolean flag) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DesignationDto> designationList = masterService.getDesignationInfoById(id,flag);
            if (!designationList.isEmpty() && designationList.size() > 0) {
                result.put("designation", designationList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("designation", designationList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @PostMapping("/updateDesignation")
    public OIIPCRAResponse updateDesignation(@RequestParam int id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            DesignationMaster updateDesignation = mapper.readValue(data, DesignationMaster.class);
            DesignationMaster desgMasterObj = masterService.updateDesignation(id, updateDesignation);
            result.put("desgMaster", desgMasterObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Designation Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //Role Menu Mapping Master
    @PostMapping("/createRoleMenu")
    public OIIPCRAResponse saveRoleMenu(@RequestBody RoleMenuInfo roleMenuInfo) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<RoleMenuMaster> roleMenuMObj = masterService.saveRoleMenu(roleMenuInfo);
            result.put("RoleMenu", roleMenuMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New RoleMenu Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllMenuByRoleId")
    public OIIPCRAResponse getAllMenuByRoleId(@RequestParam Integer userId, @RequestParam Integer roleId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<RoleMenuDto> roleMenuType = masterService.getAllMenuByRoleId(userId, roleId);
            if (!roleMenuType.isEmpty() && roleMenuType.size() > 0) {
                result.put("RoleMenu", roleMenuType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All RoleMenu");
            } else {
                result.put("RoleMenu", roleMenuType);
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

    @PostMapping("/getMenuHierarchy")
    public OIIPCRAResponse getMenuHierarchy(@RequestParam Integer userId,
                                            @RequestParam(name = "roleId", defaultValue = "0", required = false) Integer roleId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        List<ParentMenuInfo> parentMenuList = new ArrayList<>();
        try {
            if (roleId > 0) {
                parentMenuList = masterService.getMenuHierarchyByRole(userId, roleId);
            } else {
                parentMenuList = masterService.getMenuHierarchyWithoutRoleId(userId);
            }
            result.put("menuList", parentMenuList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Menu Information");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/updateRoleMenu")
    public OIIPCRAResponse updateRoleMenu(@RequestParam Integer roleId, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            RoleMenuInfo roleMenuInfo = mapper.readValue(data, RoleMenuInfo.class);
            List<RoleMenuDto> roleMenuType = masterService.getAllMenuByRoleIds(0, roleId);
            for (RoleMenuDto item : roleMenuType) {
                if (!roleMenuInfo.getMenuId().contains(item.getMenuId())) {
                    // Deactivate the menu
                    masterService.deactivateMenu(roleId, item.getMenuId(), false);
                }
            }
            for (Integer item : roleMenuInfo.getMenuId()) {
                boolean hasAvailable = false;
                for (RoleMenuDto itemRM : roleMenuType) {
                    if (itemRM.getMenuId() == item) {
                        hasAvailable = true;
                        //Set Menu Active
                        masterService.deactivateMenu(roleId, item,true);
                    }
                }
                if (!hasAvailable) {
                    //save
                    roleMenuInfo.setRoleId(roleId);
                    RoleMenuMaster roleMenuMObj = masterService.updateRoleMenu(roleMenuInfo,item);
                }
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Role Menu Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    //Contract Master
    @PostMapping("/createContract")
    public OIIPCRAResponse saveContract(@RequestParam(name = "data") String data,
                                        @RequestParam(name = "doc", required = false) MultipartFile[] files) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ContractDto contract = objectMapper.readValue(data, ContractDto.class);
            ContractMaster contractMObj = masterService.saveContract(contract);
            Integer size=contract.getContractMappingDto().size();
            if(size==1){
                contract.getContractMappingDto().get(0).setTankWiseContractAmount(contractMObj.getContractAmount());
            }
            if(size>1){
                for(int i=0;i<contract.getContractMappingDto().size();i++){
                    contract.getContractMappingDto().get(i).setTankWiseContractAmount(contractMObj.getContractAmount()/size);
                }
            }
            List<ContractMappingModel> contractMappingObj = masterService.saveContractMapping(contract.getContractMappingDto(), contractMObj.getId(),contractMObj);

            if (files!=null) {
                List<ContractDocumentModel> contractDocumentObj = masterService.saveContractDocument(contract.getContractDocumentDto(), contractMObj.getId(), files);
                for (MultipartFile mult : files) {
                    boolean saveDocument = awss3StorageService.uploadDocument(mult, String.valueOf(contractMObj.getId()), mult.getOriginalFilename());
                }
            }
            if( contract.getPhysicalProgressWork()!=null && contract.getPhysicalProgressWork().size()>0){
                List<PhysicalProgressUpdateDto> savePhysicalProgressForWork=masterServiceImpl.savePhysicalProgressForWork(contract.getPhysicalProgressWork(),contractMObj.getId());
                result.put("physicalProgressWork", savePhysicalProgressForWork);
            }

            if( contract.getPhysicalProgressConsultancy()!=null && contract.getPhysicalProgressConsultancy().size()>0){
                List<PhysicalProgressConsultancy> savePhysicalProgressForConsultancy=masterServiceImpl.savePhysicalProgressForConsultancy(contract.getPhysicalProgressConsultancy(),contractMObj.getId());
                result.put("PhysicalProgressConsultancy", savePhysicalProgressForConsultancy);
            }
         /*   if(contractMObj.getProcurementTypeId()!=null && contractMObj.getProcurementTypeId()>0){
               Object procurement=masterServiceImpl.saveProcurement(contract,contractMObj);
            }*/

            result.put("ContractMaster", contractMObj);
            result.put("ContractMapping", contractMappingObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Contract Created");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/createShlcMeeting")
    public OIIPCRAResponse saveShlcMeeting(@RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ShlcMeetingDto shlcMeetingDto = objectMapper.readValue(data, ShlcMeetingDto.class);

            ShlcMeetingEntity saveShlcMeeting = masterService.saveShlcMeeting(shlcMeetingDto);

            List<ShlcMeetingMembersEntity> saveShlcMeetingMembersEntity = masterService.saveShlcMeetingMembersEntity(shlcMeetingDto.getShlcMeetingMembersDto(), saveShlcMeeting);

            List<ShlcMeetingProceedingsEntity> saveShlcMeetingProceedingsEntity = masterService.saveShlcMeetingProceedings(shlcMeetingDto.getShlcMeetingProceedingsDto(), saveShlcMeeting);

            result.put("saveShlcMeeting", saveShlcMeeting);
            result.put("saveShlcMeetingMembers", saveShlcMeetingMembersEntity);
            result.put("saveShlcMeetingProceedings", saveShlcMeetingProceedingsEntity);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Shlc Meeting Created!!!");

        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getContractById")
    public OIIPCRAResponse getAllContract(@RequestParam Integer contractId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            String documentPath = accessDocumentPath + contractId + "/";
            List<ContractDocumentDto> contractDocument=new ArrayList<>();
            ContractInfo contract = masterService.getContract(contractId);
            //ActivityEstimateResponseDto activityEstimateById = activityService.getActivityEstimateByID(contract.getEstimateId());
            //contract.setProcurementTypeName(activityEstimateById.getProcurement());
            if(contract.getWorkTypeId()==2) {
                if (contract.getProcurementTypeId() != null && contract.getProcurementTypeId() > 0) {
                  //  if (contract.getProcurementTypeName().toLowerCase().contains("cds")  ) {
                    if (contract.getProcurementTypeId()==4 ) {
                        CdsDto cdsData = masterRepositoryImpl.getCdsDataByContractIdAndEstimateId(contract.getEstimateId(), contractId);
                        CdsDifferenceDto cdsDifference=masterRepositoryImpl.getCdsDifference(contract.getEstimateId(), contractId);
                        result.put("dataCds", cdsData);
                        result.put("cdsDifference",cdsDifference);
                    } //else if (contract.getProcurementTypeName().toLowerCase().contains("qcbs")) {
                    else if (contract.getProcurementTypeId()==3) {
                        QcbsDto qcbsData = masterRepositoryImpl.getQcbcDataByContractIdAndEstimateId(contract.getEstimateId(), contractId);
                        List<QcbsDifferenceDto> qcbsDifference=masterRepositoryImpl.getQcbsDifference(contract.getEstimateId(), contractId);
                        result.put("dataQcbs", qcbsData);
                        result.put("qcbsDifference",qcbsDifference);
                    }
                }
            }
           else if(contract.getWorkTypeId()==1) {
                if (contract.getProcurementTypeId() != null && contract.getProcurementTypeId() > 0) {
                    if (contract.getProcurementTypeId()==2) {
                        DirDto dirData = masterRepositoryImpl.getDirDataByContractIdAndEstimateId(contract.getEstimateId(), contractId);
                        result.put("dataDir", dirData);
                    }
                }
            }
            else if(contract.getWorkTypeId()==3) {
                if (contract.getProcurementTypeId() != null && contract.getProcurementTypeId() > 0) {

                    if (contract.getProcurementTypeId()==5) {
                        RfqDto rfqData = masterRepositoryImpl.getRfqDataByContractIdAndEstimateId(contract.getEstimateId(), contractId);
                        result.put("dataRfq", rfqData);
                    }
                }
            }
            List<ContractMappingDto> contractMapping = masterService.getContractMapping(contractId);
            List<Map<String, String>> dist = new ArrayList<>();
            List<Integer> distId = new ArrayList<>();
            contractMapping.stream().forEach(obj -> {
                obj.setContractNumber(contract.getContractNumber());
                if(obj.getDistId()!=null) {
                    if (!distId.contains(obj.getDistId())) {
                        distId.add(obj.getDistId());
                        Map<String, String> singleDist = new HashMap<>();
                        singleDist.put("id", String.valueOf(obj.getDistId()));
                        singleDist.put("name", obj.getDistName());
                        dist.add(singleDist);
                    }
                }
            });
            List<Map<String, String>> block = new ArrayList<>();
            List<Integer> blockId = new ArrayList<>();
            contractMapping.stream().forEach(obj -> {
                if(obj.getBlockId()!=null) {
                    if (!blockId.contains(obj.getBlockId())) {
                        blockId.add(obj.getBlockId());
                        Map<String, String> singleBlock = new HashMap<>();
                        singleBlock.put("id", String.valueOf(obj.getBlockId()));
                        singleBlock.put("name", obj.getBlockName());
                        block.add(singleBlock);
                    }
                }
            });
            List<Map<String, String>> tank = new ArrayList<>();

            List<Integer> tankId = new ArrayList<>();
            contractMapping.stream().forEach(obj -> {
                if(obj.getTankId()!=null) {
                    if (!tankId.contains(obj.getTankId())) {
                        tankId.add(obj.getTankId());
                        Map<String, String> singleTank = new HashMap<>();
                        if (obj.getTankId() != null) {
                            singleTank.put("id", String.valueOf(obj.getTankId()));
                        }
                        singleTank.put("name", obj.getTankName());
                        singleTank.put("projectId",String.valueOf(obj.getProjectId()));
                        tank.add(singleTank);
                    }
                }
            });
            List<Map<String, String>> division = new ArrayList<>();
            List<Integer> divisionId = new ArrayList<>();
            contractMapping.stream().forEach(obj -> {
                if(obj.getDivisionId()!=null) {
                    if (!divisionId.contains(obj.getDivisionId())) {
                        divisionId.add(obj.getDivisionId());
                        Map<String, String> singleDivision = new HashMap<>();
                        singleDivision.put("id", String.valueOf(obj.getDivisionId()));
                        singleDivision.put("name", obj.getDivisionName());
                        division.add(singleDivision);
                    }
                }
            });
            Integer activityId=masterServiceImpl.getDistinctActivityId(contractId);
            List<ActivityUpperHierarchyInfo>   upperLevel= activityQryRepository.getUpperHierarchyInfoById(activityId);
            result.put("upperHierarchy",upperLevel);
            for(int i=0;i<upperLevel.size();i++){
                if(upperLevel.get(i).getMasterHeadId()==1){
                    contract.setComponentName(upperLevel.get(i).getDescription());
                }
                if(upperLevel.get(i).getMasterHeadId()==2){
                    contract.setSubComponentName(upperLevel.get(i).getDescription());
                }
                if(upperLevel.get(i).getMasterHeadId()==3){
                    contract.setActivityName(upperLevel.get(i).getName());
                }
                if(upperLevel.get(i).getMasterHeadId()==4){
                    contract.setSubActivityName(upperLevel.get(i).getName());
                }
            }
            Boolean document=contractDocumentRepository.existsByContractId(contractId);
            if(document==true) {
                 contractDocument = masterService.getContractDocument(contractId);
                 for(int i=0;i<contractDocument.size(); i++) {
                     contractDocument.get(i).setDocName(documentPath + "" + contractDocument.get(i).getDocName());
                     result.put("ContractDocument", contractDocument);
                 }
            }
            if (contract!=null) {

                result.put("Contract", contract);
                result.put("ContractMapping", contractMapping);
                result.put("ContractDocument", contractDocument);
                result.put("district", dist);
                result.put("block", block);
                result.put("tank", tank);
                result.put("division", division);

                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Contract By Id");
            } else {
                result.put("Contract", contract);
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

    @PostMapping("/updateContract")
    public OIIPCRAResponse updateContract(@RequestParam int id, @RequestParam(name = "data") String data,
                                          @RequestParam(name = "doc", required = false) MultipartFile[] files) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ContractDto contract = mapper.readValue(data, ContractDto.class);
            List<ContractDocumentModel> contractDocumentObj =new ArrayList<>();
            ContractMaster contractMasterObj = masterService.updateContractById(id, contract);
            if(contract.getContractMappingDto()!=null && contract.getContractMappingDto().size()>0) {
                masterService.deactivateContractMapping(contractMasterObj.getId());
                List<ContractMappingModel> contractMappingObj = masterService.saveContractMapping(contract.getContractMappingDto(), contractMasterObj.getId(),contractMasterObj);
                result.put("ContractMapping", contractMappingObj);
            }
            else if( contract.getContractMappingDto().size()==0) {
                List<ContractMappingModel> update= masterService.updateContractMapping(contractMasterObj);
                result.put("ContractMapping", update);
            }

            if (files!=null && files.length > 0 ) {
                masterService.deactivateContractDocument(contractMasterObj.getId());
                 contractDocumentObj = masterService.saveContractDocument(contract.getContractDocumentDto(), contractMasterObj.getId(), files);
                for (MultipartFile mult : files) {
                    boolean saveDocument = awss3StorageService.uploadDocument(mult, String.valueOf(contractMasterObj.getId()), mult.getOriginalFilename());
                }
            }
            result.put("ContractMaster", contractMasterObj);
            result.put("ContractDocument", contractDocumentObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Contract Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/updateContractMapping")
    public OIIPCRAResponse updateContractMapping(@RequestParam int id, @RequestParam(name = "data")String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ContractMappingModel mapping = mapper.readValue(data, ContractMappingModel.class);

          ContractMappingModel contractMapping=masterService.updateContractMappingValue(id,mapping);
          result.put("mapping",contractMapping);
          if(contractMapping!=null) {
              response.setData(result);
              response.setStatus(1);
              response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
              response.setMessage("Contract Mapping Updated  Successfully");
          }
          else{
              response.setData(null);
              response.setStatus(0);
              response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
              response.setMessage("Contract Mapping Updated  Unsuccessfully");

          }
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/updateEstimateMapping")
    public OIIPCRAResponse updateEstimateMapping(@RequestParam int id, @RequestParam(name = "data")String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ActivityEstimateTankMappingEntity mapping = mapper.readValue(data, ActivityEstimateTankMappingEntity.class);

            ActivityEstimateTankMappingEntity estimateMapping=masterService.updateEstimateMappingValue(id,mapping);
            result.put("mapping",estimateMapping);
            if(estimateMapping!=null) {
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Estimate Mapping Updated  Successfully");
            }
            else{
                response.setData(null);
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Estimate Mapping Updated  Unsuccessfully");

            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getAllContractStatus")
    public OIIPCRAResponse getAllContractStatus() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractStatusDto> status = masterService.getAllContractStatus();
            if (!status.isEmpty() && status.size() > 0) {
                result.put("Status", status);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ContractStatus.");
            } else {
                result.put("Status", status);
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

    @PostMapping("/getAllContractNumber")
    public OIIPCRAResponse getAllContractNumber() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractStatusDto> number = masterService.getAllContractNumber();
            if (!number.isEmpty() && number.size() > 0) {
                result.put("ContractNumber", number);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ContractNumbers.");
            } else {
                result.put("ContractNumber", number);
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

    @PostMapping("/getAllContractType")
    public OIIPCRAResponse getAllContractType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractStatusDto> type = masterService.getAllContractType();
            if (!type.isEmpty() && type.size() > 0) {
                result.put("ContractType", type);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ContractType.");
            } else {
                result.put("ContractType", type);
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

    @PostMapping("/getAllTenderCode")
    public OIIPCRAResponse getAllTenderCode(@RequestParam int userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderCodeResponse> code = masterService.getAllTenderCode(userId);
            if (!code.isEmpty() && code.size() > 0) {
                result.put("TenderCode", code);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All TenderCode.");
            } else {
                result.put("TenderCode", code);
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

    @GetMapping("/getProgressStatus")
    public OIIPCRAResponse getAllProgressStatus() {
        return masterService.getProgressStatusMaster();
    }

    @PostMapping("/getDistrictByDivisionId")
    public OIIPCRAResponse getDistrictByDivisionId(@RequestBody Map<String, String> param) {
        return masterService.getDistrictByDivisionId(Integer.parseInt(param.get("divisionId")));
    }

    @PostMapping("/activateAndDeactivateMasterDataById")
    public OIIPCRAResponse activateAndDeactivateMaster(@RequestParam Integer masterId, @RequestParam Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean res = masterService.activateAndDeactivateMasterDataById(masterId, id);

            if (res) {
                response.setData(result);
                response.setStatus(1);
                response.setMessage("Master Status Changed Successfully");
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setStatus(0);
                response.setMessage("Something went wrong");
                response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(), result);
        }
        return response;
    }

    //License Crud
    @PostMapping("/createLicense")
    public OIIPCRAResponse saveLicense(@RequestBody LicenseMaster licenseMaster) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            LicenseMaster licenseMObj = masterService.saveLicense(licenseMaster);
            result.put("LicenceMaster", licenseMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("LicenseMaster Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllLicense")
    public OIIPCRAResponse getAllLicense() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<LicenseDto> license = masterService.getAllLicense();
            if (!license.isEmpty() && license.size() > 0) {
                result.put("License", license);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All License.");
            } else {
                result.put("License", license);
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

    @PostMapping("/updateLicense")
    public OIIPCRAResponse updateLicense(@RequestParam int id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            LicenseMaster updateLicense = mapper.readValue(data, LicenseMaster.class);
            LicenseMaster updateLicenseMObj = masterService.updateLicense(id, updateLicense);
            result.put("LicenseMaster", updateLicenseMObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("License Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllAgencyExempt")
    public OIIPCRAResponse getAllAgencyExempt() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyExemptDto> agencyExempt = masterService.getAgencyExempt();
            if (!agencyExempt.isEmpty() && agencyExempt.size() > 0) {
                result.put("agencyExempt", agencyExempt);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Agency Exempt.");
            } else {
                result.put("agencyExempt", agencyExempt);
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
    @PostMapping("/getAllActivityStatus")
    public OIIPCRAResponse getAllActivityStatus() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityStatusDto> activityStatus = masterService.getAllActivityStatus();
            if (!activityStatus.isEmpty() && activityStatus.size() > 0) {
                result.put("activityStatus", activityStatus);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ActivityStatus.");
            } else {
                result.put("activityStatus", activityStatus);
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
    @PostMapping("/getAllApprovedStatus")
    public OIIPCRAResponse getAllApprovedStatus() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityStatusDto> approvedStatus = masterService.getAllApprovedStatus();
            if (!approvedStatus.isEmpty() && approvedStatus.size() > 0) {
                result.put("approvedStatus", approvedStatus);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ApprovedStatus.");
            } else {
                result.put("approvedStatus", approvedStatus);
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
    @PostMapping("/getAllActivityEstimateLevel")
    public OIIPCRAResponse getAllActivityEstimateLevel() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityStatusDto> activityLevel = masterService.getAllActivityEstimateLevel();
            if (!activityLevel.isEmpty() && activityLevel.size() > 0) {
                result.put("activityLevel", activityLevel);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ActivityEstimateLevel.");
            } else {
                result.put("activityLevel", activityLevel);
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
    @PostMapping("/getAllFinancialYear")
    public OIIPCRAResponse getAllFinancialYear() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinYrDto> year = masterService.getAllFinancialYear();
            if (!year.isEmpty() && year.size() > 0) {
                result.put("year", year);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Year.");
            } else {
                result.put("year", year);
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

    @PostMapping("/getAllMonth")
    public OIIPCRAResponse getAllMonth() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<MonthDto> month = masterService.getAllMonth();
            if (!month.isEmpty() && month.size() > 0) {
                result.put("month", month);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Month.");
            } else {
                result.put("month", month);
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
    @PostMapping("/getAllProcurementType")
    public OIIPCRAResponse getAllProcurementType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProcurementTypeDto> procurement = masterService.getAllProcurementType();
            if (!procurement.isEmpty() && procurement.size() > 0) {
                result.put("procurement", procurement);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Procurement");
            } else {
                result.put("procurement", procurement);
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
    @PostMapping("/getAllEstimateType")
    public OIIPCRAResponse getAllEstimateType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityStatusDto> estimateType = masterService.getAllEstimateType();
            if (!estimateType.isEmpty() && estimateType.size() > 0) {
                result.put("EstimateType", estimateType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Estimate Type");
            } else {
                result.put("EstimateType", estimateType);
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
    @PostMapping("/getAllTank")
    public OIIPCRAResponse getAllTank(@RequestParam Integer gpId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TankInfo> tank = masterService.getAllTank(gpId);
            if (!tank.isEmpty() && tank.size() > 0) {
                result.put("tank", tank);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Tank");
            } else {
                result.put("tank", tank);
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


    //Get District By Estimate Id
    @PostMapping("/getDistrictByEstimateId")
    public OIIPCRAResponse getDistrictByEstimateId(@RequestParam int estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DistrictBoundaryDto> districtListByEstimateId = masterService.getDistrictListByEstimateId(estimateId);
            if (!districtListByEstimateId.isEmpty() && districtListByEstimateId.size() > 0) {
                result.put("districtListByEstimateId", districtListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("districtListByEstimateId", districtListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getBlockByEstimateId")
    public OIIPCRAResponse getBlockByEstimateId(@RequestParam Integer estimateId,
                                                @RequestParam(required = false, defaultValue = "0") Integer distId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BlockBoundaryDto> blockListByEstimateId = masterService.getBlockListByEstimateAndDistId(estimateId,distId);
            if (!blockListByEstimateId.isEmpty() && blockListByEstimateId.size() > 0) {
                result.put("blockListByEstimateId", blockListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("blockListByEstimateId", blockListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTankByEstimateId")
    public OIIPCRAResponse getBlockByEstimateId(@RequestParam int estimateId,
                                                @RequestParam(required = false) int distId,
                                                @RequestParam(required = false) int blockId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TankInfo> tankListByEstimateId = masterService.getTankListByEstimateAndDistId(estimateId,distId, blockId);
            if (!tankListByEstimateId.isEmpty() && tankListByEstimateId.size() > 0) {
                result.put("tankListByEstimateId", tankListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("tankListByEstimateId", tankListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getDistrictByEstimateIdForContract")
    public OIIPCRAResponse getDistrictByEstimateId(@RequestParam Integer estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        List<DistrictBoundaryDto> districtInfo=new ArrayList<>();
        try {
            districtInfo = masterService.getDistrictByEstimateId(estimateId);
            if (!districtInfo.isEmpty() && districtInfo.size() > 0) {
                result.put("districtInfo", districtInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("districtInfo", districtInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTenderByEstimateIdForContract")
    public OIIPCRAResponse getTenderByEstimateIdForContract(@RequestParam Integer estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        List<TenderDto> tenderInfo=new ArrayList<>();
        try {
            tenderInfo = masterService.getTenderByEstimateId(estimateId);
            if (!tenderInfo.isEmpty() && tenderInfo.size() > 0) {
                result.put("tenderInfo", tenderInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("tenderInfo", tenderInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getTanksByBlockIdForContract")
    public OIIPCRAResponse getTankByBlockId(@RequestParam Integer blockId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        List<TankContractDto> tankInfo=new ArrayList<>();
        try {
            tankInfo = masterService.getTankByBlockId(blockId);
            if (!tankInfo.isEmpty() && tankInfo.size() > 0) {
                result.put("tankInfo", tankInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("tankInfo", tankInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getTankByEstimateIdForContract")
    public OIIPCRAResponse getBlockByEstimateId(@RequestParam int estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        List<TankContractDto> tankListByEstimateId=new ArrayList<>();
        try {
             tankListByEstimateId = masterService.getTankByEstimateIdForContract(estimateId);
            if (!tankListByEstimateId.isEmpty() && tankListByEstimateId.size() > 0) {
                result.put("tankListByEstimateId", tankListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("tankListByEstimateId", tankListByEstimateId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/panNoExistOrNot")
    public OIIPCRAResponse panNoExistOrNot(@RequestParam String panNo){
        return masterService.panNoExistOrNot(panNo);
    }
    @PostMapping("/workIdentificationCodeExistOrNot")
    public OIIPCRAResponse workIdentificationCodeExistOrNot(@RequestParam Integer tenderId,@RequestParam String workIdentificationCode){
        return masterService.workIdentificationCodeExistOrNot(tenderId,workIdentificationCode);
    }
    @PostMapping("/getPanNoAndAgencyName")
    public OIIPCRAResponse getPanNoAndAgencyName(@RequestParam(required = false) String panNo,@RequestParam(required = false) Integer agencyId){
        return masterService.getPanNoAndAgencyName(panNo,agencyId);
    }


    @PostMapping("/getAllPanNo")
    public OIIPCRAResponse getAllPanNo(@RequestParam Integer userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyInfo> panNo = masterService.getAllPanNo();
            if (!panNo.isEmpty() && panNo.size() > 0) {
                result.put("PanNo", panNo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Pan No List.");
            } else {
                result.put("PanNo", panNo);
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
    @PostMapping("/getWorkTypeForTenderOrEstimate")
    public OIIPCRAResponse getWorkTypeForTenderOrEstimate(@RequestParam Integer id,@RequestParam Integer typeId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            WorkTypeDto workType = masterRepositoryImpl.getWorkTypeForTenderOrEstimate(id,typeId);
            if (workType!=null) {
                result.put("workType", workType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("WorkType Data");
            } else {
                result.put("workType", workType);
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
    @PostMapping("/updatePhysicalProgressConsultancy")
    public OIIPCRAResponse updatePhysicalProgressConsultancy(@RequestParam int contractId,@RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            PhysicalProgressConsultancyUpdateDto update = mapper.readValue(data, PhysicalProgressConsultancyUpdateDto.class);
            List<PhysicalProgressConsultancy> physicalProgressConsultancy=masterServiceImpl.updatePhysicalProgressByContractId(update.getPhysicalProgressConsultancy(),contractId);
            result.put("PhysicalProgress", physicalProgressConsultancy);

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("PhysicalProgressConsultancy Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getPhysicalProgressDetailsForConsultancyByContractId")
    public OIIPCRAResponse getPhysicalProgressDetails(@RequestParam (name="contractId")Integer contractId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<PhysicalProgressConsultancyDto> physicalData = masterServiceImpl.getPhysicalProgressDetailsForConsultancyById(contractId);
           if(physicalData!=null & physicalData.size()>0) {
               result.put("physicalProgressConsultancy", physicalData);
           }
           else{
               result.put("physicalProgressConsultancy", null);
           }

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("PhysicalProgressDetails for consultancy");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getAllPhysicalProgressUnit")
    public OIIPCRAResponse getPhysicalProgressUnit(){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<UnitDto> physicalProgressUnit = masterServiceImpl.getPhysicalProgressUnit();

            if(physicalProgressUnit!=null & physicalProgressUnit.size()>0) {
                result.put("physicalProgressAllUnit", physicalProgressUnit);
            }
            else{
                result.put("physicalProgressAllUnit", null);
            }

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("PhysicalProgressUnits for consultancy");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getAllPhysicalProgressStatus")
    public OIIPCRAResponse getAllPhysicalProgressStatus(){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<UnitDto> physicalProgressStatus = masterServiceImpl.getPhysicalProgressStatus();

            if(physicalProgressStatus!=null & physicalProgressStatus.size()>0) {
                result.put("physicalProgressStatus", physicalProgressStatus);
            }
            else{
                result.put("physicalProgressStatus", null);
            }

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("PhysicalProgressUnits for consultancy");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/savePhysicalProgressWork")
    public OIIPCRAResponse savePhysicalProgressWork(@RequestParam int contractId,@RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            PhysicalProgressConsultancyUpdateDto update = mapper.readValue(data, PhysicalProgressConsultancyUpdateDto.class);
            List<PhysicalProgressUpdateDto> savePhysicalProgressForWork=masterServiceImpl.savePhysicalProgressForWork(update.getPhysicalProgressWork(),contractId);
          //  List<PhysicalProgressConsultancy> physicalProgressConsultancy=masterServiceImpl.updatePhysicalProgressByContractId(update.getPhysicalProgressConsultancy(),contractId);
            result.put("PhysicalProgressWork", savePhysicalProgressForWork);

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("PhysicalProgressWork created Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTankByContractId")
    public OIIPCRAResponse getTankByContractId(@RequestParam Integer contractId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractMappingDto> tankData = masterServiceImpl.getTankByContractId(contractId);

            if(tankData!=null & tankData.size()>0) {
                result.put("tankData", tankData);
            }
            else{
                result.put("tankData", null);
            }

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("PhysicalProgressUnits for consultancy");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getTankByWorkId")
    public OIIPCRAResponse getTankByWorkId(@RequestParam Integer workId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TankInfo> tankData = masterServiceImpl.getTankByWorkId(workId);

            if(tankData!=null & tankData.size()>0) {
                result.put("tankData", tankData);
            }
            else{
                result.put("tankData", null);
            }

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Tank By workId");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getAllActivityByDepartment")
    public OIIPCRAResponse getAllActivityByDepartment(@RequestParam Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityInformationDto> activityList = masterService.getAllActivityByDepartment(id);
            if (!activityList.isEmpty() && activityList.size() > 0) {
                result.put("activityList", activityList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAgencyByContractType")
    public OIIPCRAResponse getAgencyByContractType(@RequestParam Integer typeId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyDto> agencyList = masterService.getAgencyByContractType(typeId);
            if (!agencyList.isEmpty() && agencyList.size() > 0) {
                result.put("agencyList", agencyList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Record not found.");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }



}