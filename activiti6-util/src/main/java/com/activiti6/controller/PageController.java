package com.activiti6.controller;


import com.activiti6.entity.VacationForm;
import com.activiti6.service.MiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 前端控制器-请假实例-t08股权案例
 * @author  Bruce
 */
@Controller
public class PageController {
	@Autowired
	private MiaoService miaoService;
	@GetMapping( "/login")
	public String login2(){
		return "login";
	}
	@GetMapping( "/t8")
	public String t8(){
		return "t8";
	}
	//申请者的首页
	@GetMapping( "/home")
	public String index(ModelMap model, HttpServletRequest request){
		List<VacationForm> forms = miaoService.formList();
		Cookie[] cookies = request.getCookies();
		String user = "";
		//从cookie中获取当前用户
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userInfo")) {
					user = cookie.getValue();
					break;
				}
			}
		}
		List<HashMap<String, Object>> formsMap = new ArrayList<HashMap<String, Object>>();
		for(VacationForm form : forms) {
			//申请者只能看到自己申请的请假单信息
			if(user.equals(form.getApplicant())) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", form.getId());
				map.put("title", form.getTitle());
				map.put("content", form.getContent());
				map.put("applicant", form.getApplicant());
				map.put("state", form.getState());
				formsMap.add(map);
			}
		}
		//将forms参数返回
		model.addAttribute("forms",formsMap);
		return "index2";
	}
	
	//审核者的首页
	@GetMapping( "/homeApprover")
	public String indexApprover(ModelMap model){
		List<VacationForm> forms = miaoService.formList();
		List<HashMap<String, Object>> formsMap = new ArrayList<HashMap<String, Object>>();
		for(VacationForm form : forms) {
			//审核者只能看到待审核状态的请假单
			if("领导审核".equals(form.getState())) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", form.getId());
				map.put("title", form.getTitle());
				map.put("content", form.getContent());
				map.put("applicant", form.getApplicant());
				map.put("state", form.getState());
				formsMap.add(map);
			}
		}
		model.addAttribute("forms",formsMap);
		return "indexApprover";
	}
	
	//请假单页面
	@GetMapping( "/form")
	public String form(){
		return "form";
	}
}
