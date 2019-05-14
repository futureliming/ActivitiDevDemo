package com.activiti6.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
/**
 * 服务流程-测试
 * @author Bruce/佳武
 *
 */
public class MyServiceTask implements  org.activiti.engine.delegate.JavaDelegate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 流程变量
	 */
	private Expression text1;

	@Override
	public void execute(DelegateExecution execution) {

		System.out.println("serviceTask已经执行已经执行！");

		String value1 = (String) text1.getValue(execution);

		System.out.println(value1);

		execution.setVariable("var1", new StringBuffer(value1).reverse().toString());

	}

}


