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
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;

/**
 * activiti 外部接口
 *
 * @author Bruce
 */
@RestController
@RequestMapping("/act")
public class ActivititestController {
    private Logger logger = LoggerFactory.getLogger(getClass());
//    启动流程
//    流程列表
//    新增流程
//    根据用户查询正在执行的任务
//    根据用户查询历史任务
//    挂起流程
    
	@Autowired
	private RuntimeService runtimeService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private RepositoryService repositoryService ;
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
        String path = "processes/test.bpmn";
        processService.startProcess(path);
    }

    /**
     * 流程列表
     * @param request
     */
    @RequestMapping("/ProcessList")
    public void ProcessList(HttpServletRequest request) {
        int rowSize = 10;
        int page =1 ;
        List<Model> list = repositoryService.createModelQuery().listPage(rowSize * (page - 1)
                , rowSize);
        long count = repositoryService.createModelQuery().count();

        logger.info(list.toString());
    }
    /**
     * 新增流程
     * @param request
     */
    @RequestMapping("/addProcess")
    public void addProcess(HttpServletRequest request) {

    }
    /**
     *  根据用户查询正在执行的任务
     * @param request
     */
    @RequestMapping("/getTaskByKey")
    public void getTaskByKey(HttpServletRequest request) {

    }

    /**
     * 根据用户查询历史任务
     * @param request
     */
    @RequestMapping("/getHiTaskByKey")
    public void getHiTaskByKey(HttpServletRequest request) {

    }
    /**
     * 挂起流程
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
        vacationFormService.save(form );
        logger.info("id: "+form.getId());
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
    		DeploymentBuilder deploymentBuilder = 	repositoryService.createDeployment()
    				.name("测试部署")
    				.addClasspathResource(bpmnPath);

    		logger.info("部署对象 deployment");
    		Deployment deployment = deploymentBuilder.deploy();//提交部署
    		logger.info("部署对象实例 信息打印");
    		logger.info("deployment = {}",deployment);
    		logger.info("id ={}", deployment.getId());
    		logger.info("key ={}", deployment.getKey());
    		logger.info("name ={}", deployment.getName());
    		logger.info("新建部署查询对象,可通过多种方式进行查询验证");
    		DeploymentQuery deploymentQuery1 = repositoryService.createDeploymentQuery();
    		deploymentQuery1.deploymentId(deployment.getId());
    		logger.info("deploymentQuery1 = {}",deploymentQuery1);

    		DeploymentQuery deploymentQuery3 = repositoryService.createDeploymentQuery();
    		deploymentQuery3.deploymentName(deployment.getName());
    		logger.info("deploymentQuery3 = [{}]",deploymentQuery3);

    		logger.info("通过createProcessDefinitionQuery来查询部署对象,比如:是否激活，分类等其他条件");
    		List<ProcessDefinition> DeploymentQuery4 =  repositoryService
    				.createProcessDefinitionQuery()
    				.active()
    				.listPage(0,100);//是否激活
    		logger.info("DeploymentQuery4 = {}",DeploymentQuery4);

    		logger.info("测试多次部署");
    		repositoryService.createDeployment()
    		.name("测试多次部署")
    		.addClasspathResource(bpmnPath)
    		.deploy();


    		logger.info("通过createProcessDefinitionQuery来查询部署对象,比如:是否激活，分类等其他条件");
    		List<ProcessDefinition> DeploymentQuery5 =  repositoryService
    				.createProcessDefinitionQuery()
    				.active()
    				.orderByProcessDefinitionVersion().asc()
    				.listPage(0,100);//是否激活 并按照版本倒序
    		logger.info("DeploymentQuery4 ={}",DeploymentQuery5);

    		logger.info("通过createProcessDefinitionQuery来查询部署对象,比如:是否激活，分类等其他条件");
    		List<ProcessDefinition> DeploymentQuery6 =  repositoryService
    				.createProcessDefinitionQuery()
    				.latestVersion()
    				.listPage(0,100);//是否是最后一个版本
    		logger.info("DeploymentQuery4 = {}",DeploymentQuery6);

    	}
    	/***
    	 * 获取流程实例各节点以及下一个节点
    	 */
    	@RequestMapping("/getNodes")
    	public void getNodes(HttpServletRequest request) {
    		String procInstanceId =request.getParameter("processInstanceId");
    		String processDefinitionKey =request.getParameter("processDefinitionKey");
    		//		idea1 https://lvdong5830.iteye.com/blog/1584556

    		//		//流程ID获取当前任务
    				List<Task> tasks1 = taskService.createTaskQuery().processInstanceId(procInstanceId).list();
//    				List<Task> tasks2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).list();
    		
    		
    				for(Task task: tasks1) {
    					//			当前任务获取当前流程的流程定义，然后根据流程定义获得所有的节点
    					ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(task.getProcessDefinitionId());  
//    					List<ActivityImpl> activitiList = def.getzz//rs是指RepositoryService的实例  
    		
    					//		根据任务获取当前流程执行ID，执行实例以及当前流程节点的ID
    					String excId = task.getExecutionId();  
    					ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult();  
    					String activitiId = execution.getActivityId();  
    					ActivitiActivityEventImpl a = null;
//    					ScopeImpl d = null;
    					//			然后循环activitiList 并判断出当前流程所处节点，然后得到当前节点实例，根据节点实例获取所有从当前节点出发的路径，然后根据路径获得下一个节点实例
//    					for(ActivityImpl activityImpl:activitiList){  
//    						String id = activityImpl.getId();  
//    						if(activitiId.equals(id)){  
//    							System.out.println("当前任务："+activityImpl.getProperty("name")); //输出某个节点的某种属性  
//    							List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();//获取从某个节点出来的所有线路  
//    							for(PvmTransition tr:outTransitions){  
//    								PvmActivity ac = tr.getDestination(); //获取线路的终点节点  
//    								System.out.println("下一步任务任务："+ac.getProperty("name"));  
//    							}  
//    							break;  
//    						}  
//    					}  
    				}

    		//		idea2
    		//		https://liuzidong.iteye.com/blog/2378920
    		//获取
    		HashMap map = new HashMap<String,Object>();
    		HistoricActivityInstance hai=historyService.createHistoricActivityInstanceQuery()//  
    				.processInstanceId(procInstanceId)//  
    				.unfinished()  
    				.singleResult();  
    		if(hai!=null){  
    			map.put("piState", hai.getActivityName());// 流程状态  
    		}else{  
    			map.put("piState", "完结");// 流程状态  
    		}  
    		logger.info("map = {}",map);
    	}
}
