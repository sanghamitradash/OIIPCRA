package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.UserInfo;
import com.orsac.oiipcra.bindings.UserListRequest;
import com.orsac.oiipcra.bindings.UserSaveRequests;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.User;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.exception.RecordExistException;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.RoleRepository;
import com.orsac.oiipcra.repository.UserAreaMappingRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.repository.UserRepository;
import com.orsac.oiipcra.service.EmailService;
import com.orsac.oiipcra.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserQueryRepository userQryRepo;

    @Autowired
    private UserAreaMappingRepository userAreaMappingRepository;

    @Override
    public User saveUser(UserSaveRequests userSaveRequests) throws RecordExistException {
        if (checkEmailExists(userSaveRequests.getEmail())){
            throw new RecordExistException("User", "Email", userSaveRequests.getEmail());
        }
        if (checkMobileNumberExists(userSaveRequests.getMobileNumber())){
            throw new RecordExistException("User", "Mobile", userSaveRequests.getMobileNumber());
        }
        userSaveRequests.setPassword(encoder.encode(userSaveRequests.getPassword()));
        /*Set<Role> userRoles = new HashSet<>();
        for (Role role : userSaveRequests.getRoles()) {
            Role nextRole = roleRepo.findRoleById(role.getId());
            userRoles.add(nextRole);
        }
        userSaveRequests.setRoles(userRoles)*/;
        User user = new User();
        BeanUtils.copyProperties(userSaveRequests, user);
        return userRepo.save(user);
    }

    @Override
    public User updateUser(int id, UserSaveRequests userUpdateRequests) throws Exception {
        User existingUser = userRepo.findById(id);
        if(existingUser == null){
            throw new RecordNotFoundException("User", "id", id);
        }
        existingUser.setName(userUpdateRequests.getName());
        existingUser.setEmail(userUpdateRequests.getEmail());
        existingUser.setMobileNumber(userUpdateRequests.getMobileNumber());
        existingUser.setDesignationId(userUpdateRequests.getDesignationId());
        existingUser.setDepartmentId(userUpdateRequests.getDepartmentId());
        existingUser.setSubDeptId(userUpdateRequests.getSubDeptId());
        existingUser.setUserLevelId(userUpdateRequests.getUserLevelId());
        existingUser.setUpdatedBy(userUpdateRequests.getUpdatedBy());
        existingUser.setRoleId(userUpdateRequests.getRoleId());
        /*Set<Role> userRoles = new HashSet<>();
        for (Role role : userUpdateRequests.getRoles()) {
            Role nextRole = roleRepo.findRoleById(role.getId());
            userRoles.add(nextRole);
        }*/
        //existingUser.setRoles(userRoles);
        User save = userRepo.save(existingUser);
        return save;
    }

    //This Function will be used for deactivating the area based on the user_id before assigning new area(Update User)
    @Override
    public Boolean deactivateAreaMapping(Integer id) {
        return userQryRepo.deactivateAreaByUserId(id);
    }

    @Override
    public Boolean activateAndDeactivateUser(Integer userId) {
        return userQryRepo.activateAndDeactivateUser(userId);
    }

    @Override
    public Boolean activateAndDeactivateAreaMapping(Integer userId) {
        return userQryRepo.activateAndDeactivateUserAreaMapping(userId);
    }


    @Override
    public List<UserAreaMapping> saveUserAreaMapping(List<UserAreaMapping> userAreaMapping, int userId) {
        for(UserAreaMapping userAreaMapping1 : userAreaMapping){
            userAreaMapping1.setIsActive(true);
            userAreaMapping1.setUserId(userId);
        }
        return userAreaMappingRepository.saveAll(userAreaMapping);
    }

    @Override
    public Boolean checkEmailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public Boolean checkMobileNumberExists(Long mobileNo) {
        return userRepo.existsByMobileNumber(mobileNo);
    }

    @Override
    public Page<UserInfo> getUserList(UserListRequest userListRequest) {
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        return userList;
    }
    @Override
    public List<UserListDropDownDto> getUserListForDropDown(Integer userId) {
        List<UserListDropDownDto> userList = userQryRepo.getUserListForDropDown(userId);
        return userList;
    }


    @Override
    public Optional<User> findUserByMobileNo(Long mobileNo) {
        return userRepo.findUserByMobileNumber(mobileNo);
    }

    @Override
    public Boolean resetUserPassword(User user, String password) {
        userQryRepo.updateUserPassword(encoder.encode(password), user.getMobileNumber());
        return true;
    }

    @Override
    public User findUserByMobileAndEmail(Long mobile, String email) {
        return userRepo.findUserByMobileAndEmail(mobile,email);
    }

    @Override
    public Integer sendOtpToUser(User user) {
        MailDto mailDto = new MailDto();
        Random random = new Random();
        int otp = random.nextInt(999999);
        mailDto.setRecipient(user.getEmail());
        mailDto.setSubject("OTP for new password.");
        String message = readMailBody("FORGOT_PWD_OTP_TEMPLATE.txt", user, otp);
        mailDto.setMessage(message);
        if (emailService.sendHtmlMail(mailDto)){
            return otp;
        }
        return 0;
    }

    @Override
    public User findUserByMobile(Long mobile) {
        return userRepo.findUserByMobile(mobile);
    }

    public String readMailBody(String filename, User user, Integer otp){
        String mailBody = null;
        try {
            File resource = new ClassPathResource(filename).getFile();
            mailBody = new String(Files.readAllBytes(resource.toPath()));
            mailBody = mailBody.replace("{name}", user.getName());
            mailBody = mailBody.replace("{otp}", String.valueOf(otp));
        }catch (IOException io){
            System.out.println(io.getMessage());
        }
        return mailBody;
    }

    @Override
    public List<Integer> getUserRoleIdList(int userId) {
        List<Integer> role = userQryRepo.getUserRoleIdList(userId);
        return role;
    }
    @Override
    public List<Integer> getMenuIdList(List<Integer> roleIds) {
        List<Integer> menuId = userQryRepo.getUserMenuIdList(roleIds);
        return menuId;
    }

    @Override
    public List<MenuDto> getParentMenuListByMenuId(List<Integer> menuId) {
        List<MenuDto> parentMenuList = userQryRepo.getParentMenuListByMenuId(menuId);
        for (MenuDto menuDto : parentMenuList){
            List<HierarchyMenuDto> menuListByParentId = userQryRepo.getHierarchyMenuListById(menuDto.getMenuId(),menuId);
            if (menuListByParentId.size() > 0){
                menuDto.setChildren(menuListByParentId);
            }else{
                menuDto.setChildren(new ArrayList<>());
            }
        }
        return parentMenuList;
    }
    @Override
    public UserInfoDto getUserById(int id) {
        return userQryRepo.getUserById(id);
    }



}


