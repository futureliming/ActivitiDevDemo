package com.activiti6.service;


import com.activiti6.entity.VacationForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationFormService extends JpaRepository<VacationForm, Integer>{
	
}