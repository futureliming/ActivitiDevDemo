package com.activiti6.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.activiti6.service.MiaoService;
import com.activiti6.util.JWUtil;
import com.activiti6.util.ResultInfo;
/**
 * t08测试 - t08:股权流程图例子
 * @author Bruce
 *
 */
@RestController
@RequestMapping("/t08")
public class Test08Controller {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private MiaoService miaoService;
	@Autowired
	private HistoryService historyService;
	/**
	 * 发布流程
	 */
	@RequestMapping("/runProcessT8")
	private boolean runProcessT8(){
		String bpmnPath = "processes/t08.bpmn";
		logger.info("部署配置流程图  {}","行数"+JWUtil.getLineNumber());
		logger.info("addClasspathResource通过本地文件添加部署文件  {}","行数"+JWUtil.getLineNumber());
		DeploymentBuilder deploymentBuilder = 	repositoryService.createDeployment()
				.name("测试部署08")
				.addClasspathResource(bpmnPath);
		logger.info("部署对象 deployment");
		Deployment deployment = deploymentBuilder.deploy();//提交部署
		logger.info("部署对象实例 信息打印, {}","行数"+JWUtil.getLineNumber());
		logger.info("deployment = {}",deployment);
		return true;
	}
	/**
	 * 创建一个流程实例
	 * @param request
	 * @return
	 */
	@RequestMapping("/startProcessIns")
	private boolean startProcessIns(HttpServletRequest request){
		//		假设业务表信息已经插入，返回业务id
		final String businessKey = request.getParameter("businessKey");
		runtimeService.startProcessInstanceByKey("test08", businessKey);
		logger.info("启动一个流程实例, {}行",JWUtil.getLineNumber());
		logger.info("businessKey, {}",businessKey);
		return true;
	}

	/**查询个人任务*/
	@RequestMapping("/findPersonalTaskList")
	private  List<Map<String,String>> findPersonalTaskList(HttpServletRequest request){
		String userName = request.getParameter("userName");
		List<Map<String,String>> reList = new ArrayList<Map<String,String>>();
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
				Map<String,String> map = new HashMap<String,String>();
				map.put("id", task.getId());
				map.put("name", task.getName());
				map.put("procInstId", task.getProcessInstanceId());
				reList.add(map);
			}
		}

		return reList;
	}

	/**
	 * 完成任务 根据任务id,由于是网关，需要添加参数myMoney
	 */
	@RequestMapping("/completeTask")
	private  String completeTask(HttpServletRequest request){
		String taskId = request.getParameter("taskId");
		Integer flowVar = Integer.valueOf(request.getParameter("flowVar"));
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("flow", flowVar);

		taskService.complete(taskId,variables);

		logger.info("完成任务："+taskId);
		return  "T";
	}

	//	查询历史流程任务实例
	@GetMapping( "/historyTaskState")
	public List<HistoricTaskInstance> queryHistoricTask(HttpServletRequest request){
		String processInstanceBusinessKey = request.getParameter("processInstanceBusinessKey");
		String processInstanceId= request.getParameter("processInstanceId");
		List<HistoricTaskInstance> reList = null;
		if(processInstanceBusinessKey != null) {
			reList =  historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(processInstanceBusinessKey).list();
		}else {
			reList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
		}
		return reList;
	}

	//获取历史的流程模型列表 process
	@GetMapping( "/historyProcessState")
	public List<HistoricProcessInstance> HistoricProcessInstances(HttpServletRequest request){
		//test08:3:32510
		String processDefinitionId = request.getParameter("processDefinitionId");

		List<HistoricProcessInstance> reList =  historyService.createHistoricProcessInstanceQuery().processDefinitionId(processDefinitionId).list();

		return reList;
	}
}
