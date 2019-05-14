package com.activiti6.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.http.HttpServletRequest;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.el.ExpressionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.activiti6.service.ProcessService;
import com.activiti6.service.VacationFormService;

import de.odysseus.el.util.SimpleContext;

/**通用化设计
 * 获取下一个节点
 * @author Bruce
 *
 */
@RestController
@RequestMapping("/NextNode")
public class NextNodeController {
	private Logger logger = LoggerFactory.getLogger(getClass());
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
	/***
	 * 获取流程实例各节点以及下一个节点
	 * 
	 */
	@RequestMapping("/getNodeInfo")
	public void getNodeInfo(HttpServletRequest request) {
		String procInstanceId = request.getParameter("processInstanceId");

		HashMap map = new HashMap<String, Object>();
		HistoricActivityInstance hai = historyService.createHistoricActivityInstanceQuery()//
				.processInstanceId(procInstanceId)//
				.unfinished().singleResult();
		if (hai != null) {
			map.put("piState", hai.getActivityName());// 流程状态
		} else {
			map.put("piState", "完结");// 流程状态
		}
		logger.info("map = {}", map);
	}

	@RequestMapping("/getNextNode")
	public void getNextNode(HttpServletRequest request) {
		String processId = request.getParameter("processId");
		getNodes(processId);

	}
	//	https://www.jianshu.com/p/5db288d77759
	/**
	 * 整个流程模型的所有节点
	 * @param processId
	 */
	private void getNodes(String processId) {

		// TODO Auto-generated method stub
		//根据流程实例id获得流程模式id
		String processDefinitionId  = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processId).singleResult().getProcessDefinitionId();
		//获得流程模型
		BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
		//获得模式中所有节点
		Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
		logger.info("flowElements ={}", flowElements);
		for(FlowElement impl : flowElements) {
			logger.info("flowElement ={}",ToStringBuilder.reflectionToString(impl,ToStringStyle.DEFAULT_STYLE));
		}


	}

	/**
	 *
	 * @param flowElements 该模型的所有节点
	 * @param currentSequenceFlow 当前流程线
	 * @param elKV  所有变量信息
	 * @param userTasks 最终结果集
	 * @throws Exception
	 */
	private void nextNode(Collection<FlowElement> flowElements,SequenceFlow currentSequenceFlow,List<ActRuVariable> elKV,List<FlowElement> userTasks)throws Exception{
		//获得当前线在
		String  SequenceFlowId =   currentSequenceFlow.getTargetRef();
		for(FlowElement e : flowElements){
			if(e.getId().equals(SequenceFlowId)){
				//判断类型
				//如果是排他网管
				if(e instanceof ExclusiveGateway){
					//默认流程线
					String defaultFlowString = ((ExclusiveGateway) e).getDefaultFlow();
					SequenceFlow defaultFlow = null;
					List<SequenceFlow> egSequenceFlow  = ((ExclusiveGateway) e).getOutgoingFlows();
					//标识
					boolean boo = true;
					for(int i=0;i<egSequenceFlow.size();i++){
						if(egSequenceFlow.get(i).getId().equals(defaultFlowString)){
							defaultFlow = egSequenceFlow.get(i);
						}
						if(!StringUtils.isEmpty(egSequenceFlow.get(i).getConditionExpression())){
							//判断el选择路线
							if(isCondition(egSequenceFlow.get(i).getConditionExpression(),elKV)){
								boo=false;
								//如果为真说明会走这条路线  递归
								nextNode(flowElements,egSequenceFlow.get(i),elKV,userTasks);
							}
						}else{
							continue;
						}

						//如果最后一个走完没有el为true的，则查看是否有默认流程，如果没有抛出异常
						if(i==egSequenceFlow.size()-1&&boo){
							if(StringUtils.isEmpty(defaultFlowString)){
								throw  new Exception("流程异常");
							}else{
								//如果有默认流程 递归
								nextNode(flowElements,defaultFlow,elKV,userTasks);
							}
						}
					}
					//如果是user用户审批节点
				}else if(e instanceof UserTask){
					userTasks.add(e);
				}else if(e instanceof EndEvent){
					userTasks.add(e);
				}
				// .... 现在就这么多
			}
		}
	}
	//判断 el 表达式
	private boolean isCondition( String el,List<ActRuVariable> elKV) {
		ExpressionFactory factory = new ExpressionFactoryImpl();
		SimpleContext context = new SimpleContext();
		for(int i =0 ;i<elKV.size();i++){
			context.setVariable(elKV.get(i).getName(), factory.createValueExpression(elKV.get(i).getText(), String.class));
		}
		ValueExpression e = factory.createValueExpression(context, el, boolean.class);
		return (Boolean) e.getValue(context);
	}
}
