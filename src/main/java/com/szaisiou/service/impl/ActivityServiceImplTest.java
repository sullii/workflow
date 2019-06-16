package com.szaisiou.service.impl;

import com.szaisiou.entity.ActivitiDto;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2019/6/9.
 */
public class ActivityServiceImplTest {

    private  static  final Logger logger = Logger.getLogger(ActivityServiceImplTest.class);

    //所运行工作流的名字
    //private static final String PROCESS_DEFINE_KEY = "leave2.bpmn";
    private static final String PROCESS_DEFINE_KEY = "leave2";
    private static final String NEXT_ASSIGNEE = "test1";


    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;

    @Resource
    private ProcessEngine processEngine;

 //   @Override
//    public boolean startActivity() {
//        logger.info("工作流程开始启动...");
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("apply", "zhangsan");
//        map.put("approve", "lisi");
//        //部署流程定义
//        repositoryService.createDeployment().name("leave2")
//                .addClasspathResource("processes/leave2.bpmn").deploy();
//        //启动流程实例 通过流程定义key，key是流程定义xml中id的值，使用key能够按照最新的流程定义启动流程
//        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("leave2", map);
//        String processId = pi1.getId();
//        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
//        logger.info("task 第一步:{}"+task);
//        taskService.complete(task.getId(), map);// 完成第一步申请
//
//        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
//        logger.info("task 第二步:{}"+task);
//        String taskId2 = task.getId();
//        map.put("pass", false);
//        taskService.complete(taskId2, map);// 驳回申请
//
//        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
//        logger.info("task 第三步:{}"+task);
//        logger.info("工作流结束....");
//        return false;
//    }

    //部署流程定义
  //  public ActivityServiceImplTest(){
  //      logger.info("开始部署流程定义...");
//        try {
//            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(ResourceLoader.CLASSPATH_URL_PREFIX + "processes/*.bpmn");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
   // logger.info(repositoryService);
     //   logger.info(processEngine);
//        Deployment deployment = repositoryService.createDeployment().addClasspathResource(ResourceLoader.CLASSPATH_URL_PREFIX + "processes/*.bpmn").deploy();
       // logger.info("流程定义已成功部署... "+deployment);
  //  }

    /**
     * 开始流程实例
     *
     * @param userName
     * @return
     */

    public Map<String,String> startActivi(String userName) {
        HashMap result = new HashMap<String,String>();
        logger.info("开始启动流程实例...");
        identityService.setAuthenticatedUserId(userName);
        //开始流程
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(PROCESS_DEFINE_KEY);
        String processId = pi.getId();
        logger.info("===============processId================="+processId);
        // 查询当前任务
        Task currentTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        String taskId = currentTask.getId();
        logger.info("===============taskId1==================="+taskId);
        // 申明任务人
        taskService.setAssignee(taskId, userName);

        result.put("processId",processId);
        result.put("taskId",taskId);
        return result;
    }

    /**
     * 查询当前用户的流程任务
     *
     * @param userName
     * @return
     */
    public List<Task> queryAssigneeTaskTest(String userName){
        List<Task> list = taskService.createTaskQuery().taskAssignee(userName).list();
        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
            }
        }
        return  list;
    }

    /**
     * 完成任务并且指定下一个任务处理人
     *
     * @param map
     * @param vac
     * @param userName
     * @return
     */

    public Boolean submitTaskForm(Map<String,String> map,ActivitiDto vac,String userName) {
        logger.info("开始流程实例...");
        String taskId = map.get("taskId");
        String processId = map.get("processId");
        Map<String, Object> vars = new HashMap<>(4);
        vars.put("applyUser", userName);
        vars.put("days", vac.getDays());
        vars.put("reason", vac.getReason());
        //办结
        taskService.complete(taskId);

        // 到了下一个任务， 应该在此处指派任务由谁来处理
        //重新获取当前任务
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        String taskId2 = task.getId();
        logger.info("===============taskId2==================="+taskId2);
        taskService.setAssignee(taskId2, NEXT_ASSIGNEE);
        return null;
    }


    public List<Task> myActiviti(String userName) {
        List<Task> list =  taskService.createTaskQuery().taskAssignee(userName).list();
        return list;
    }


    public List<HistoricProcessInstance> myActivitiRecord(String userName) {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(PROCESS_DEFINE_KEY).startedBy(userName)
                .finished().orderByProcessInstanceEndTime().desc().list();
        return historicProcessInstances;
    }
}
