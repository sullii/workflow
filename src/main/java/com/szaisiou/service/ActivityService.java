package com.szaisiou.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流引擎核心业务类
 *
 * @author suli@szaisiou.com
 * @date 2019年6月10日11:43:49
 */
public interface ActivityService {

    /**
     * 启动流程实例
     *
     * @param processKey 流程定义key
     * @param businessKey 业务id
     * @param variables 流程变量
     */
    public ProcessInstance startProcess(String processKey , String businessKey, HashMap variables) throws  Exception;

    /**
     * 根据流程图key和用户id去查询当前用户的某个流程的任务列表
     *
     * @param processKey
     * @param userId
     * @return
     */
    public List<Task> findTasks(String processKey,String userId);

    /**
     * 完成任务&审批
     *
     * @param taskId
     * @param userId
     * @param result
     */
    public void completeTask(String taskId,String userId,String result);

    /**
     * 更改业务流程状态
     *
     * @param execution
     * @param status
     */
    public void updateBizStatus(DelegateExecution execution, String status);

    /**
     * 流程节点权限用户列表
     *
     * @param execution
     * @return
     */
    public List<String> findUsersForSP(DelegateExecution execution);

    /**
     * 生成流程图
     * @param deploymentId
     * @param imageName
     */
    public void generateImage(String deploymentId, String imageName, HttpServletResponse response) ;


    /**
     * 根据id获取流程图中活动的节点坐标
     *
     * @param taskId
     * @return
     */
    public Map<String, Object> getCurrentActivityCoordinates(String taskId);


}
