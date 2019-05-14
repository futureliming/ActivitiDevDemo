package com.activiti6.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.activiti6.util.JWUtil;
/**
 * t05案例- t5:表单例子
 * @author Bruce
 *
 */
@RestController
@RequestMapping("/t05")
public class Test05Controller {
	Logger logger = LoggerFactory.getLogger(getClass());
	

	@Autowired
	private  FormService formService ;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;

	/**
	 * 	//https://blog.csdn.net/whatlookingfor/article/details/52998861
	// test: http://localhost:8004/t05/startForm/test05:1:25004
	 * 获取当前执行步骤form表单的内容以及结构
	 * @param procDefId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "startForm/{procDefId}")
	public String startForm(@PathVariable(value = "procDefId") String procDefId, Model model) {

	    Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
	    Map<String,String> datePatterns = new HashMap<String,String>();
	    StartFormData startFormData = formService.getStartFormData(procDefId);
	    List<FormProperty> formProperties = startFormData.getFormProperties();
	    for (FormProperty formProperty : formProperties) {
	        if("enum".equals(formProperty.getType().getName())){
	            Map<String, String> values;
	            values = (Map<String, String>) formProperty.getType().getInformation("values");
	            if (values != null) {
	                for (Map.Entry<String, String> enumEntry : values.entrySet())
	                    logger.debug("enum, key: {}, value: {}", enumEntry.getKey(), enumEntry.getValue());
	                result.put("enum_" + formProperty.getId(), values);
	            }

	        }else if("date".equals(formProperty.getType().getName())){
	            datePatterns.put("pattern_"+formProperty.getId(), (String)formProperty.getType().getInformation("datePattern"));
	            logger.debug("date,key:{},pattern:{}",formProperty.getId(),formProperty.getType().getInformation("datePattern"));
	        }

	    }
	    model.addAttribute("datePatterns",datePatterns);
	    model.addAttribute("result", result);
	    model.addAttribute("list", formProperties);
	    model.addAttribute("formData", startFormData);

	    return "modules/act/dynamicStartForm";
	}
	/**
	 * 启动一个流程实例
	 * @param request
	 * @return
	 */
	@RequestMapping("/startProcessIns")
	private boolean startProcessIns(HttpServletRequest request){
//		假设业务表信息已经插入，返回业务id
		final String businessKey = request.getParameter("businessKey");//业务key
		final String processDefinitionKey = request.getParameter("processDefinitionKey");//流程key
		runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
		logger.info("启动一个流程实例, {}行",JWUtil.getLineNumber());
		logger.info("businessKey, {}",businessKey);
		logger.info("processDefinitionKey, {}",processDefinitionKey);
		return true;
	}
	
	/**
	 * 发布流程
	 */
	@RequestMapping("/runProcessT5")
	private boolean runProcessT3(){
		String bpmnPath = "processes/test05.bpmn";
		logger.info("部署配置流程图  {}","行数"+JWUtil.getLineNumber());
		logger.info("addClasspathResource通过本地文件添加部署文件  {}","行数"+JWUtil.getLineNumber());
		DeploymentBuilder deploymentBuilder = 	repositoryService.createDeployment()
				.name("测试部署05")
				.addClasspathResource(bpmnPath);
		logger.info("部署对象 deployment");
		Deployment deployment = deploymentBuilder.deploy();//提交部署
		logger.info("部署对象实例 信息打印, {}","行数"+JWUtil.getLineNumber());
		logger.info("deployment = {}",deployment);

		return true;
	}

}
