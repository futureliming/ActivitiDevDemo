package com.activiti6.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.activiti6.util.JWUtil;
/**
 * t03案例 -t03:互斥网关案例
 * 部署配置流程图
查询个人任务
完成任务根据任务id
查询正在运行任务
 * @author Bruce
 *
 */
@RestController
@RequestMapping("/t03")
public class Test03Controller {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	/**
	 * 发布流程
	 */
	@RequestMapping("/runProcessT3")
	private boolean runProcessT3(){
		String bpmnPath = "processes/test03.bpmn";
		logger.info("部署配置流程图  {}","行数"+JWUtil.getLineNumber());
		logger.info("addClasspathResource通过本地文件添加部署文件  {}","行数"+JWUtil.getLineNumber());
		DeploymentBuilder deploymentBuilder = 	repositoryService.createDeployment()
				.name("测试部署03")
				.addClasspathResource(bpmnPath);
		logger.info("部署对象 deployment");
		Deployment deployment = deploymentBuilder.deploy();//提交部署
		logger.info("部署对象实例 信息打印, {}","行数"+JWUtil.getLineNumber());
		logger.info("deployment = {}",deployment);

		return true;
	}


	/**查询个人任务*/
	@RequestMapping("/findPersonalTaskList")
	private  void findPersonalTaskList(HttpServletRequest request){
		String userName = request.getParameter("userName");
		//任务办理人
		String assignee =userName;
		List<Task> list = taskService
				.createTaskQuery()
				.taskAssignee(assignee)//个人任务的查询
				.list();
		if(list!=null && list.size()>0){
			for(Task task:list){
				logger.info("任务ID："+task.getId());
				logger.info("任务的办理人："+task.getAssignee());
				logger.info("任务名称："+task.getName());
				logger.info("任务的创建时间："+task.getCreateTime());
				logger.info("流程实例ID："+task.getProcessInstanceId());
				logger.info("#######################################");
			}
		}
	}
	
	@RequestMapping("/startProcessIns")
	private boolean startProcessIns(HttpServletRequest request){
//		假设业务表信息已经插入，返回业务id
		final String businessKey = request.getParameter("businessKey");
		runtimeService.startProcessInstanceByKey("test03", businessKey);
		logger.info("启动一个流程实例, {}行",JWUtil.getLineNumber());
		logger.info("businessKey, {}",businessKey);
		return true;
	}
	
	/**
	 * 完成任务根据任务id,由于是网关，需要添加参数myMoney
	 */
	@RequestMapping("/completeTask")
	private  void completeTask(HttpServletRequest request){
		String taskId = request.getParameter("taskId");
		Integer myMoney = Integer.valueOf(request.getParameter("myMoney"));
		//任务ID
		//完成任务的同时，设置流程变量，让流程变量判断连线该如何执行
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("key", myMoney);
		taskService.complete(taskId,variables);
		System.out.println("完成任务："+taskId);
	}
	


	

	@RequestMapping("/taskRunInfo")
	private void taskRunInfo() {
		// 创建一个任务查询对象
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 办理人的任务列表
		List<Task> list = taskQuery.list();
		// 遍历任务列表
		if (list != null && list.size() > 0) {
			for (Task task : list) {
				logger.info("任务的办理人：" + task.getAssignee());
				logger.info("任务的id：" + task.getId());
				logger.info("任务的名称：" + task.getName());
			}
		}
		logger.info("查询正在运行任务, {}行",JWUtil.getLineNumber());
//		return list;

	}
}
