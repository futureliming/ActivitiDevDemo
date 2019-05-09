package com.activiti6.service;


import com.activiti6.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserService extends JpaRepository<User, Long>{
	public List<User> findByName(String name);
}