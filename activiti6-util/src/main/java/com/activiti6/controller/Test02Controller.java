package com.activiti6.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.activiti6.util.JWUtil;
/**
 * 流程test02测试
发布流程
启动一个流程实例
获取当前流程实例历史签收人
任务设置签收人
任务设置受理人
任务完成通过业务id
 */
@RestController
@RequestMapping("/t02")
public class Test02Controller {
	Logger logger = LoggerFactory.getLogger(getClass());
	//	假设业务表信息已经插入，返回业务id
	final String businessKey = "3247854";
	//流程id
	final String processDefinitionKey = "test02";
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private TaskService taskService;

	/**
	 * 发布流程
	 */
	@RequestMapping("/runProcessT2")
	private boolean runProcessT2(){
		String bpmnPath = "processes/test02.bpmn";
		logger.info("部署配置流程图  {}","行数"+JWUtil.getLineNumber());
		logger.info("addClasspathResource通过本地文件添加部署文件  {}","行数"+JWUtil.getLineNumber());
		DeploymentBuilder deploymentBuilder = 	repositoryService.createDeployment()
				.name("测试部署")
				.addClasspathResource(bpmnPath);
		logger.info("部署对象 deployment");
		Deployment deployment = deploymentBuilder.deploy();//提交部署
		logger.info("部署对象实例 信息打印, {}","行数"+JWUtil.getLineNumber());
		logger.info("deployment = {}",deployment);

		return true;
	}


	@RequestMapping("/startProcessIns")
	private boolean startProcessIns(){
		//		Map<String, Object> variables = new HashMap<String, Object>();
		//		variables.put("employee", form.getApplicant());
		runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
		logger.info("启动一个流程实例, {}行",JWUtil.getLineNumber());
		return true;
	}

	@RequestMapping("/getHiInfo")
	private List<HistoricTaskInstance> getHiInfo(){
		List<HistoricTaskInstance> reList = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(businessKey).list();
		logger.info("获取当前流程实例历史签收人, {}行",JWUtil.getLineNumber());
		return reList;
	}

	//	任务设置签收人
	@RequestMapping("/taskClaim")
	private boolean  taskClaim(HttpServletRequest request){
		String userName = request.getParameter("userName");
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
		//        //认领任务,成为任务的受理人
		taskService.claim(task.getId(), userName);

		logger.info("任务设置签收人, {}行",JWUtil.getLineNumber());
		return true;
	}
	//	任务设置受理人
	@RequestMapping("/setAssignee")
	private boolean  setAssignee(HttpServletRequest request){
		String userName = request.getParameter("userName");
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
		//        //认领任务,成为任务的受理人
		taskService.setAssignee(task.getId(), userName);
		//完成任务
		logger.info("任务设置受理人, {}行",JWUtil.getLineNumber());
		return true;
	}
	//	任务完成
	@RequestMapping("/taskComplete")
	private boolean  taskComplete(){

		Task task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
		//完成任务
		taskService.complete(task.getId());
		logger.info("任务完成通过业务id, {}行",JWUtil.getLineNumber());
		return true;
	}

	//	任务完成
	@RequestMapping("/taskCompleteByTaskId")
	private boolean  taskCompleteByTaskId(HttpServletRequest request){
		String taskId = request.getParameter("taskId");
//		Task task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
		//完成任务
		taskService.complete(taskId);
		logger.info("任务完成通过业务id, {}行",JWUtil.getLineNumber());
		return true;
	}


}
