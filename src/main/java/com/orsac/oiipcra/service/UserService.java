package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.bindings.UserInfo;
import com.orsac.oiipcra.bindings.UserListRequest;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.Role;
import com.orsac.oiipcra.bindings.UserSaveRequests;
import com.orsac.oiipcra.entities.DepartmentMaster;
import com.orsac.oiipcra.entities.User;
import com.orsac.oiipcra.entities.UserAreaMapping;
import com.orsac.oiipcra.entities.UserLevel;
import com.orsac.oiipcra.exception.RecordExistException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserByMobileNo(Long mobileNo);

    Boolean resetUserPassword(User user, String password);

    User findUserByMobileAndEmail(Long mobile, String email);

    Integer sendOtpToUser(User user);

    User findUserByMobile(Long Mobile);

    User saveUser(UserSaveRequests userSaveRequests) throws RecordExistException;

    User updateUser(int id, UserSaveRequests userUpdateRequests) throws Exception;

    List<UserAreaMapping> saveUserAreaMapping(List<UserAreaMapping> userAreaMapping, int userId);

    List<Integer> getUserRoleIdList(int userId);

    List<Integer> getMenuIdList(List<Integer> roles);

    List<MenuDto> getParentMenuListByMenuId(List<Integer> menuId);

    Boolean checkEmailExists(String email);

    Boolean checkMobileNumberExists(Long mobileNo);

    Page<UserInfo> getUserList(UserListRequest userListRequest);
    List<UserListDropDownDto> getUserListForDropDown(Integer userId);

    UserInfoDto getUserById(int id);

    Boolean deactivateAreaMapping(Integer id);

    Boolean activateAndDeactivateUser(Integer userId);

    Boolean activateAndDeactivateAreaMapping(Integer userId);

}
