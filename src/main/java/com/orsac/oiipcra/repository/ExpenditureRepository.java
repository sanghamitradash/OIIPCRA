package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.ExpenditureInfo;
import com.orsac.oiipcra.dto.ExpenditureDto;
import com.orsac.oiipcra.dto.ExpenditureListDto;
import com.orsac.oiipcra.entities.Expenditure;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Integer> {
    Expenditure findById(int id);


    Expenditure findExpenditureById(Integer id);

}
