package com.activiti6.serviceImpl;

import com.activiti6.service.ProcessService;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProcessServiceImpl  implements ProcessService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    HistoryService historyService;

    /***
     * 发布 流程
     * @param processPath
     */
    public  void startProcess(String processPath){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                .addClasspathResource(processPath)
                .deploy();
        log.info("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
    }

    /**
     *  获取task任务
     * @param processInstanceBusinessKey
     * @return
     */
    public Task getTaskByBusinessKey(String processInstanceBusinessKey){
    Task task = taskService.createTaskQuery().processInstanceBusinessKey(processInstanceBusinessKey).singleResult();
    return task;
}

    /**
     * 启动一个流程实例
     * 使用给定的键在流程定义的最新版本中启动新的流程实例
     * @param processDefinitionKey
     * @param businessKey
     * @param variables
     */
   public void  instanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables){
       runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    /**
     * 分支处 ,使流程自动流转到对应节点
     * @param processInstanceBusinessKey
     * @param variableName
     * @param value
     */
    public void branch(String processInstanceBusinessKey, String variableName, Object value ){
        //根据businessKey找到当前任务节点
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(processInstanceBusinessKey).singleResult();
        //设置输入参数，使流程自动流转到对应节点
        taskService.setVariable(task.getId(), variableName, value);//分配变量属性 从而判断分支走向哪边
        taskService.complete(task.getId());//任务成功调用
    }

    /**
     * 任务结束1
     * @param processInstanceBusinessKey
     * @param userId 用户
     * @param variables 用户组
     */
    public void close01(String processInstanceBusinessKey, String userId, Map<String, Object> variables){
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(processInstanceBusinessKey).singleResult();
        //认领任务,成为任务的受理人
        taskService.setAssignee(task.getId(), userId);
        //完成任务
        taskService.complete(task.getId(), variables);
    }
    /**
     * 任务结束2 ，不需要分配参数给用户
     * @param processInstanceBusinessKey
     * @param userId 用户
     */
    public void close02(String processInstanceBusinessKey, String userId){
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(processInstanceBusinessKey).singleResult();
        //认领任务,成为任务的受理人
        taskService.setAssignee(task.getId(), userId);
        //完成任务
        taskService.complete(task.getId());

    }

    /**
     * 根据business id 来获取其历史记录
     * @param processInstanceBusinessKey
     * @return
     */
    public List<HistoricTaskInstance> getHistory( String processInstanceBusinessKey){
        return 	 historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(processInstanceBusinessKey).list();
}

}
