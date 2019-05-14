package com.activiti6.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.activiti6.util.JWUtil;

@RestController
@RequestMapping("/t06")
public class Test06Controller {
	Logger logger = LoggerFactory.getLogger(getClass());


	@Autowired
	private  FormService formService ;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	/**
https://www.jianshu.com/p/66e336554a06
流程附带表单提交，
	 * @param procDefId
	 * @param model
	 */
	@RequestMapping(value = "startForm/{processDefinitionId}")
	public void startForm(@PathVariable(value = "processDefinitionId") String processDefinitionId) {
		// 获取表单key和启动节点表单数据
		String formKey = formService.getStartFormKey(processDefinitionId);
		StartFormData startFormData = formService.getStartFormData(processDefinitionId);
		List<FormProperty> formPropertyList = startFormData.getFormProperties();

		// 提交开始节点的表单
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("message","hello world");
		ProcessInstance processInstance = formService.submitStartFormData(processDefinitionId,properties);

		// 获取当前用户任务节点的表单数据
		Task task1 = taskService.createTaskQuery()
				.processInstanceId(processInstance.getId())
				.singleResult();
		TaskFormData taskFormData = formService.getTaskFormData(task1.getId());
		logger.info("taskformData={}",taskFormData);

		// 提交任务节点的表单
		Map<String,String> taskParams = new HashMap<String, String>();
		taskParams.put("yesOrNo","yes");
		formService.submitTaskFormData(task1.getId(),taskParams);
		List<HistoricProcessInstance>  df = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processDefinitionId).list();
		logger.info("df={}",df);
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
		logger.info("processDefinitionKey, {}",ToStringBuilder.reflectionToString( processDefinitionKey,ToStringStyle.DEFAULT_STYLE));
		return true;
	}

	/**
	 * 发布流程
	 */
	@RequestMapping("/runProcessT6")
	private boolean runProcessT6(){
		String bpmnPath = "processes/test06.bpmn";
		logger.info("部署配置流程图  {}","行数"+JWUtil.getLineNumber());
		logger.info("addClasspathResource通过本地文件添加部署文件  {}","行数"+JWUtil.getLineNumber());
		DeploymentBuilder deploymentBuilder = 	repositoryService.createDeployment()
				.name("测试部署06")
				.addClasspathResource(bpmnPath);
		logger.info("部署对象 deployment");
		Deployment deployment = deploymentBuilder.deploy();//提交部署
		logger.info("部署对象实例 信息打印, {}","行数"+JWUtil.getLineNumber());
		logger.info("deployment = {}",deployment);

		return true;
	}

}
