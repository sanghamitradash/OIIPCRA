package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.bindings.UserInfo;
import com.orsac.oiipcra.bindings.UserListRequest;
import com.orsac.oiipcra.dto.UserDto;
import com.orsac.oiipcra.dto.UserInfoDto;
import com.orsac.oiipcra.dto.UserListDropDownDto;
import com.orsac.oiipcra.entities.User;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.service.MasterService;
import com.orsac.oiipcra.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private MasterService masterService;

    @PostMapping("/createUser")
    public OIIPCRAResponse saveUser(
            @RequestParam(name = "data") String data,
            @RequestParam(required = false) MultipartFile profileImage) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserSaveRequests userSaveRequests = mapper.readValue(data, UserSaveRequests.class);
            User savedUser = userService.saveUser(userSaveRequests);
            List<UserAreaMapping> userAreaMappingInfo = userService.saveUserAreaMapping(userSaveRequests.getUserAreaMapping(), savedUser.getId());
            result.put("user", savedUser);
            result.put("userAreaMappingInfo", userAreaMappingInfo);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            oiipcraResponse.setMessage("User Created Successfully");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/updateUser")
    public OIIPCRAResponse updateUser(@RequestParam Integer id,
                                      @RequestParam(name = "data") String data,
                                      @RequestParam(required = false) MultipartFile profileImage) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserSaveRequests userUpdateRequest = mapper.readValue(data, UserSaveRequests.class);
            User updatedUser = userService.updateUser(id, userUpdateRequest);
            userService.deactivateAreaMapping(updatedUser.getId());
            List<UserAreaMapping> userAreaMappingInfo = userService.saveUserAreaMapping(userUpdateRequest.getUserAreaMapping(), updatedUser.getId());
            result.put("user", updatedUser);
            result.put("userAreaMappingInfo", userAreaMappingInfo);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("User Updated Successfully");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/login")
    public OIIPCRAResponse loginUser(@RequestBody UserRequest request) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        try {
            Map<String, Object> result = new HashMap<>();
            User dbUser = userService.findUserByMobile(request.getMobileNumber());
            if (dbUser != null && !dbUser.getEmail().isEmpty()) {
                boolean verifyuserpassword = encoder.matches(request.getPassword(), dbUser.getPassword());
                if (verifyuserpassword == true) {

                    UserDto userDto = new UserDto();
                    userDto.setId(dbUser.getId());
                    userDto.setName(dbUser.getName());
                    userDto.setEmail(dbUser.getEmail());
                    userDto.setMobileNumber(dbUser.getMobileNumber());
                    userDto.setDesignationId(dbUser.getDesignationId());
                    userDto.setDepartmentId(dbUser.getDepartmentId());
                    userDto.setCreatedBy(dbUser.getCreatedBy());
                    userDto.setUserLevelId(dbUser.getUserLevelId());
                    userDto.setIsActive(dbUser.getActive());
                    userDto.setRoleId(dbUser.getRoleId());
                    userDto.setCreatedOn(dbUser.getCreatedOn());
                    userDto.setUpdatedOn(dbUser.getUpdatedOn());
                    userDto.setDepartmentName(dbUser.getDepartmentMaster().getName());
                    userDto.setDesignationName(dbUser.getDesignationMaster().getName());
                    userDto.setIsSurveyor(dbUser.getSurveyor());
                    //List<Integer> roles = userService.getUserRoleIdList(dbUser.getId());
                    //List<Integer> menuIdList = userService.getMenuIdList(roles);
                    //List<MenuDto> ParentMenuList = userService.getParentMenuListByMenuId(menuIdList);
                    List<ParentMenuInfo> parentMenuList = new ArrayList<>();
                    if (dbUser.getRoleId() > 0) {
                        parentMenuList = masterService.getMenuHierarchyByRole(dbUser.getId(), dbUser.getRoleId());
                    } else {
                        parentMenuList = masterService.getMenuHierarchy(dbUser.getRoleId());
                    }
                    result.put("user", userDto);
                    result.put("menuList", parentMenuList);
                    oiipcraResponse.setData(result);
                    oiipcraResponse.setStatus(1);
                    oiipcraResponse.setMessage("Login successfully !!!");
                    oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                } else {
                    oiipcraResponse.setStatus(0);
                    oiipcraResponse.setMessage("Wrong Password");
                    oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                }
            } else {
                oiipcraResponse.setStatus(0);
                oiipcraResponse.setMessage("User Not Found !!!");
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }

        } catch (Exception e) {
            oiipcraResponse.setStatus(0);
            oiipcraResponse.setMessage("");
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return oiipcraResponse;
    }

    @PostMapping("/resetPassword")
    public OIIPCRAResponse resetUserPassword(@RequestBody(required = false) Map<String, String> param) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        Optional<User> user = userService.findUserByMobileNo(Long.parseLong(param.get("mobile")));
        if (user.isPresent()) {
            if (encoder.matches(param.get("password"), user.get().getPassword())) {
                oiipcraResponse.setStatus(0);
                oiipcraResponse.setData(result);
                oiipcraResponse.setMessage("New password shouldn't be same as old password !!!");
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.CONFLICT));
            } else {
                if (userService.resetUserPassword(user.get(), param.get("password"))) {
                    oiipcraResponse.setData(result);
                    oiipcraResponse.setStatus(1);
                    oiipcraResponse.setMessage("Password reset successfully !!!");
                    oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                } else {
                    oiipcraResponse.setStatus(0);
                    oiipcraResponse.setData(result);
                    oiipcraResponse.setMessage("Something went wrong while resetting password !!!");
                    oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                }
            }
        } else {
            oiipcraResponse.setStatus(0);
            oiipcraResponse.setData(result);
            oiipcraResponse.setMessage("User Not Found !!!");
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return oiipcraResponse;
    }

    @PostMapping("/changePassword")
    public OIIPCRAResponse changeUserPassword(@RequestBody(required = false) Map<String, String> param) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<User> user = userService.findUserByMobileNo(Long.parseLong(param.get("mobile")));
            if (user.isPresent()) {
                if (encoder.matches(param.get("oldPassword"), user.get().getPassword())) { //check if the old and current password are same
                    Boolean resetStatus = userService.resetUserPassword(user.get(), param.get("newPassword"));
                    oiipcraResponse.setStatus(1);
                    oiipcraResponse.setData(result);
                    oiipcraResponse.setMessage("Password Changed Successfully.");
                    oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                } else {
                    oiipcraResponse.setStatus(0);
                    oiipcraResponse.setData(result);
                    oiipcraResponse.setMessage("Wrong Old Password");
                    oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                }
            } else {
                oiipcraResponse.setStatus(0);
                oiipcraResponse.setData(result);
                oiipcraResponse.setMessage("User Not Found !!!");
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            oiipcraResponse.setStatus(0);
            oiipcraResponse.setData(result);
            oiipcraResponse.setMessage("Something Went Wrong !!!");
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }

        return oiipcraResponse;
    }

    @PostMapping("/sendOtpToUser")
    public OIIPCRAResponse sendOtpToUser(@RequestBody(required = false) Map<String, String> param) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        User user = userService.findUserByMobileAndEmail(Long.parseLong(param.get("mobile")), param.get("email"));
        if (user != null) {
            int otp = userService.sendOtpToUser(user);
            if (otp > 0) {
                result.put("otp", otp);
                oiipcraResponse.setData(result);
                oiipcraResponse.setStatus(1);
                oiipcraResponse.setMessage("OTP sent successfully !!!");
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                oiipcraResponse.setStatus(0);
                oiipcraResponse.setData(result);
                oiipcraResponse.setMessage("Something went wrong while sending otp !!!");
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } else {
            oiipcraResponse.setStatus(0);
            oiipcraResponse.setData(result);
            oiipcraResponse.setMessage("User Not Found !!!");
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return oiipcraResponse;
    }

    @PostMapping("/getUserList")
    public OIIPCRAResponse getUserList(@RequestBody UserListRequest userListRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<UserInfo> userListPage = userService.getUserList(userListRequest);
            List<UserInfo> userList = userListPage.getContent();
            result.put("userList", userList);
            result.put("currentPage", userListPage.getNumber());
            result.put("totalItems", userListPage.getTotalElements());
            result.put("totalPages", userListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Users.");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getUserListForDropDown")
    public OIIPCRAResponse getUserListForDropDown(@RequestParam Integer userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserListDropDownDto> userListDropDown = userService.getUserListForDropDown(userId);
            for(int i=0;i<userListDropDown.size();i++){
                userListDropDown.get(i).setUserName(userListDropDown.get(i).getUserName().replace("\n",""));
            }


            result.put("userList", userListDropDown);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Users For DropDown");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getUserInfoById")
    public OIIPCRAResponse getUserById(@RequestBody String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            UserInfoDto userInfoById = userService.getUserById(jsonObject.getInt("id"));
            List<UserAreraInfo> userAreaList = userQueryRepository.getAreaInfo(jsonObject.getInt("id"));
//            ArrayList<String> district = new ArrayList<>();
//            userAreaList.stream().forEach(obj -> {
//                district.add(obj.getDistrictName());
//                district.add(String.valueOf(obj.getDistId()));
//            });
//            ArrayList<String> block = new ArrayList<>();
//            userAreaList.stream().forEach(obj -> {
//                block.add(obj.getBlockName());
//                block.add(String.valueOf(obj.getBlockId()));
//            });
//            HashMap<String, String> dist = new HashMap<>();
            List<Map<String, String>> dist = new ArrayList<>();
            List<Integer> distId = new ArrayList<>();
            userAreaList.stream().forEach(obj -> {
                if (!distId.contains(obj.getDistId())) {
                    distId.add(obj.getDistId());
                    Map<String, String> singleDist = new HashMap<>();
                    singleDist.put("id", String.valueOf(obj.getDistId()));
                    singleDist.put("name", obj.getDistrictName());
                    dist.add(singleDist);
                }
            });

            List<Map<String, String>> block = new ArrayList<>();
            List<Integer> blockId = new ArrayList<>();
            userAreaList.stream().forEach(obj -> {
                if (!blockId.contains(obj.getBlockId())) {
                    blockId.add(obj.getBlockId());
                    Map<String, String> singleBlock = new HashMap<>();
                    singleBlock.put("id", String.valueOf(obj.getBlockId()));
                    singleBlock.put("name", obj.getBlockName());
                    block.add(singleBlock);
                }
            });



            List<Map<String, String>> gp = new ArrayList<>();
            List<Integer> gpId = new ArrayList<>();
            userAreaList.stream().forEach(obj -> {
                if(!gpId.contains(obj.getGpId())){
                    gpId.add(obj.getGpId());
                    Map<String, String> singleGp = new HashMap<>();
                    singleGp.put("id", String.valueOf(obj.getGpId()));
                    singleGp.put("name", obj.getGpName());
                    gp.add(singleGp);
                }
            });
            List<Map<String, String>> village = new ArrayList<>();
            List<Integer> villageId = new ArrayList<>();
            userAreaList.stream().forEach(obj -> {
                if(!villageId.contains(obj.getVillageId())){
                    villageId.add(obj.getVillageId());
                    Map<String, String> singleVillage = new HashMap<>();
                    singleVillage.put("id", String.valueOf(obj.getVillageId()));
                    singleVillage.put("name", obj.getVillageName());
                    village.add(singleVillage);
                }
            });

            List<Map<String, String>> division = new ArrayList<>();
            List<Integer> divisionId = new ArrayList<>();
            userAreaList.stream().forEach(obj -> {
                if(!divisionId.contains(obj.getDivisionId())){
                    divisionId.add(obj.getDivisionId());
                    Map<String, String> singleDivision = new HashMap<>();
                    singleDivision.put("id", String.valueOf(obj.getDivisionId()));
                    singleDivision.put("name", obj.getMiDivisionName());
                    division.add(singleDivision);
                }
            });

            List<Map<String, String>> subDivision = new ArrayList<>();
            List<Integer> subDivisionId = new ArrayList<>();
            userAreaList.stream().forEach(obj -> {
                if(!subDivisionId.contains(obj.getSubdivisionId())){
                    subDivisionId.add(obj.getSubdivisionId());
                    Map<String, String> singleSubDivision = new HashMap<>();
                    singleSubDivision.put("id", String.valueOf(obj.getSubdivisionId()));
                    singleSubDivision.put("name", obj.getMiSubDivisionName());
                    subDivision.add(singleSubDivision);
                }
            });
            List<Map<String, String>> section = new ArrayList<>();
            List<Integer> sectionId = new ArrayList<>();
            userAreaList.stream().forEach(obj -> {
                if(!sectionId.contains(obj.getSectionId())){
                    sectionId.add(obj.getSectionId());
                    Map<String, String> singleSection = new HashMap<>();
                    singleSection.put("id", String.valueOf(obj.getSectionId()));
                    singleSection.put("name", obj.getMiSectionName());
                    section.add(singleSection);
                }
            });

            result.put("userInfoById", userInfoById);
            result.put("userAreaData", userAreaList);
            result.put("district", dist);
            result.put("block", block);
            result.put("gp", gp);
            result.put("village", village);
            result.put("division", division);
            result.put("subDivision", subDivision);
            result.put("section", section);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Info of User By Id.");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/activateAndDeactivateUser")
    public OIIPCRAResponse activateAndDeactivateUser(@RequestParam Integer userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean res = userService.activateAndDeactivateUser(userId);
            userService.activateAndDeactivateAreaMapping(userId);
            if (res) {
                response.setData(result);
                response.setStatus(1);
                response.setMessage("User Status Changed Successfully");
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

}
