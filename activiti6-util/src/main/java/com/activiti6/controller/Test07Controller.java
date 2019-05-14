package com.activiti6.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/t07")
public class Test07Controller {
	@Autowired
	private ProcessEngine processEngine;
	
	/** 包容网关
	 * https://blog.csdn.net/weixin_42068560/article/details/80084820
	 * */
	@RequestMapping("/main")
	public  void test07() throws InterruptedException {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment()
						 .addClasspathResource("processes/t07.bpmn")
						 .deploy();
		System.out.println("流程部署成功!");// 流程定义ID
		// 流程定义的key
		String processDefinitionKey = "InclusiveGatewayTest01";// 绘制的流程图ID
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行的流程实例和执行对象相关的Service
				.startProcessInstanceByKey(processDefinitionKey);// 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值。使用key值启动好处：默认是按照最新版本的流程定义启动
		String procesInstanceId = pi.getId();
		System.out.println("启动了一个ID为" + procesInstanceId + "的流程实例" );
	
		TaskService taskService = processEngine.getTaskService();
		while(processEngine.getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceId(procesInstanceId)
				.singleResult()!=null){//如果流程实例没有结束就一直查询当前实例的用户任务
			// 查询当前到达的任务（多线）
			List<Task> tasks = taskService.createTaskQuery()
										  .processInstanceId(procesInstanceId)
										  .list();
			if(tasks!=null && tasks.size()>0){
				for (Task t : tasks) {
					// 设置变量
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put("money", 200);
//					variables.put("money", 1200);
//					variables.put("money", 5200);
					
					// 完成当前运行节点任务
					taskService.complete(t.getId(),variables);
					System.out.println("任务 " + t.getName() + " 执行完成!");
					System.out.println("===================================");
				}
			}
		}
		System.out.println("流程实例： " + procesInstanceId + "执行完成!");
	}

}
