package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>{

    Boolean existsByEmail(String email);

    Boolean existsByMobileNumber(Long mobileNo);

    //This repo is used in Login for
    @Query(value = "select * from user_m where mobile_number=:mobile", nativeQuery = true)
    Optional<User> findUserByMobileNumber(Long mobile);

    @Query(value = "select * from user_m where mobile_number=:mobile and email=:email", nativeQuery = true)
    User findUserByMobileAndEmail(Long mobile,String email );

    @Query(value = "select * from user_m where mobile_number=:mobile and is_active=true", nativeQuery = true)
    User findUserByMobile(long mobile);

    User findById(int id);

}
