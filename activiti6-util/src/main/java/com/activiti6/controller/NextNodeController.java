package com.activiti6.controller;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.http.HttpServletRequest;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.el.ExpressionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import de.odysseus.el.util.SimpleContext;

/**通用化设计
 * 获取下一个节点(暂时不研究)(思路：自己解析所有节点，通过已经知道的当前节点获取下一个，如果下一个是网关，通过el解析来分析下一个)
 * 获取流程所有节点
 * el表达式判断
 * @author Bruce
 *
 */
@RestController
@RequestMapping("/NextNode")
public class NextNodeController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private HistoryService historyService;
	/***
	 * 获取流程当前节点状况
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
		logger.info("==================");

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
