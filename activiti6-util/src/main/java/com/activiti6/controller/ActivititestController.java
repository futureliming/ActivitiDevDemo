package com.activiti6.controller;

import com.activiti6.entity.VacationForm;
import com.activiti6.service.MiaoService;
import com.activiti6.service.ProcessService;
import com.activiti6.service.VacationFormService;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.impl.ActivitiActivityEventImpl;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * activiti 通用化 设计提供外部接口
 *
 * @author Bruce
 */
@RestController
@RequestMapping("/act")
public class ActivititestController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 启动流程
	// 流程列表
	// 新增流程
	// 根据用户查询正在执行的任务
	// 根据用户查询历史任务
	// 挂起流程
	//	删除流程实例

	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private VacationFormService vacationFormService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;

	/**
	 * 测试 启动流程
	 *
	 * @param request
	 */
	@RequestMapping("/startprocess")
	public void startProcess(HttpServletRequest request) {
		String path = "processes/t09.bpmn";
		processService.startProcess(path);
	}

	/**
	 * 流程列表
	 * 
	 * @param request
	 */
	@RequestMapping("/ProcessList")
	public void ProcessList(HttpServletRequest request) {
		int rowSize = 10;
		int page = 1;
		List<Model> list = repositoryService.createModelQuery().listPage(rowSize * (page - 1), rowSize);
		long count = repositoryService.createModelQuery().count();

		logger.info(list.toString());
	}
	
    @GetMapping("/startProcessIns")
    private boolean startProcessIns(
            @RequestParam("businessKey") String businessKey,
             @RequestParam("processDefiKey") String processDefiKey
    ) {
    	   runtimeService.startProcessInstanceByKey(processDefiKey, businessKey);
           logger.info("启动一个流程实例");
           logger.info("businessKey, {}", businessKey);
           return true;
    }

    /**
	/**
	 * 新增流程
	 * 
	 * @param request
	 */
	@RequestMapping("/addProcess")
	public void addProcess(HttpServletRequest request) {

	}

	/**
	 * 根据用户查询正在执行的任务
	 * 
	 * @param request
	 */
	@RequestMapping("/getTaskByKey")
	public void getTaskByKey(HttpServletRequest request) {

	}

	/**
	 * 根据用户查询历史任务
	 * 
	 * @param request
	 */
	@RequestMapping("/getHiTaskByKey")
	public void getHiTaskByKey(HttpServletRequest request) {

	}

	/**
	 * 挂起流程
	 * 
	 * @param request
	 */
	@RequestMapping("/stopProcess")
	public void stopProcess(HttpServletRequest request) {

	}

	/**
	 * 测试jpa插入
	 */
	@RequestMapping("/testInput")
	public void testInput() {
		VacationForm form = new VacationForm();
		form.setTitle("标题1a_");
		form.setContent("内容2b#");
		form.setApprover("未知审批者$%^");
		vacationFormService.save(form);
		logger.info("id: " + form.getId());
	}

	/**
	 * RepositoryService 测试，repositoryservice启动一个流程
	 */
	@RequestMapping("/TRepositoryService")
	public void TRepositoryService() {
		String bpmnPath = "processes/test02.bpmn";

		logger.info("部署一个流程生成6条记录，编号从 1-7 ，分别为： 部署 1+流程2+流程数据流2+合法的图片1");

		logger.info("部署配置流程图");
		logger.info("addClasspathResource通过本地文件添加部署文件");
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("测试部署")
				.addClasspathResource(bpmnPath);

		logger.info("部署对象 deployment");
		Deployment deployment = deploymentBuilder.deploy();// 提交部署
		logger.info("部署对象实例 信息打印");
		logger.info("deployment = {}", deployment);
		logger.info("id ={}", deployment.getId());
		logger.info("key ={}", deployment.getKey());
		logger.info("name ={}", deployment.getName());
		logger.info("新建部署查询对象,可通过多种方式进行查询验证");
		DeploymentQuery deploymentQuery1 = repositoryService.createDeploymentQuery();
		deploymentQuery1.deploymentId(deployment.getId());
		logger.info("deploymentQuery1 = {}", deploymentQuery1);

		DeploymentQuery deploymentQuery3 = repositoryService.createDeploymentQuery();
		deploymentQuery3.deploymentName(deployment.getName());
		logger.info("deploymentQuery3 = [{}]", deploymentQuery3);

		logger.info("通过createProcessDefinitionQuery来查询部署对象,比如:是否激活，分类等其他条件");
		List<ProcessDefinition> DeploymentQuery4 = repositoryService.createProcessDefinitionQuery().active().listPage(0,
				100);// 是否激活
		logger.info("DeploymentQuery4 = {}", DeploymentQuery4);

		logger.info("测试多次部署");
		repositoryService.createDeployment().name("测试多次部署").addClasspathResource(bpmnPath).deploy();

		logger.info("通过createProcessDefinitionQuery来查询部署对象,比如:是否激活，分类等其他条件");
		List<ProcessDefinition> DeploymentQuery5 = repositoryService.createProcessDefinitionQuery().active()
				.orderByProcessDefinitionVersion().asc().listPage(0, 100);// 是否激活 并按照版本倒序
		logger.info("DeploymentQuery4 ={}", DeploymentQuery5);

		logger.info("通过createProcessDefinitionQuery来查询部署对象,比如:是否激活，分类等其他条件");
		List<ProcessDefinition> DeploymentQuery6 = repositoryService.createProcessDefinitionQuery().latestVersion()
				.listPage(0, 100);// 是否是最后一个版本
		logger.info("DeploymentQuery4 = {}", DeploymentQuery6);

		//		用来终止流程
		//		repositoryService.suspendProcessDefinitionById(null);
		//		给用户添加流程启动权限
		//		repositoryService.addCandidateStarterUser(processDefinitionId, userId);
		//		给组添加流程启动权限
		//		repositoryService.addCandidateStarterGroup(processDefinitionId, groupId);
		//		各种流程删除方法()
		//		repositoryService.delete...
		//		删除模型
		//		repositoryService.deleteModel(modelId);
		//		给用户删除流程启动权限
		//		repositoryService.deleteCandidateStarterUser(processDefinitionId, userId);
		//		给用户删除流程启动权限
		//		repositoryService.deleteCandidateStarterGroup(processDefinitionId, groupId);
	}

	//	删除流程实例(包括正在运行的实例)的所有信息
	@RequestMapping("/delProcessInstinct")
	private void delProcessInstinct(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String pricessInstId = request.getParameter("pricessInstId");
//		判断该流程实例是否结束，未结束和结束两者删除表的信息是不一样的。

		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(pricessInstId)// 使用流程实例ID查询
		.singleResult();

		if(pi==null){
			//该流程实例已经完成了
			historyService.deleteHistoricProcessInstance(pricessInstId);
		}else{
			//该流程实例未结束的
			runtimeService.deleteProcessInstance(pricessInstId, "");
			historyService.deleteHistoricProcessInstance(pricessInstId);//(顺序不能换)
		}
		List  df2= historyService.createHistoricProcessInstanceQuery().processInstanceId(pricessInstId).list();
		logger.info("流程实例 = {}", df2.size()>0?"存在":"已删除");

	}


}
