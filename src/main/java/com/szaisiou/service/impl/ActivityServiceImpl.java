package com.szaisiou.service.impl;

import com.szaisiou.service.ActivityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 流程核心业务处理
 *
 * @Author suli@szaisiou.com
 */
@Service("ActivityService")
public class ActivityServiceImpl implements ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    //注入activity核心组件
    @Autowired
    private RepositoryService repositoryService;//资源仓库service

    @Autowired
    private RuntimeService runtimeService;//运行时service

    @Autowired
    private TaskService taskService;


    @Override
    public ProcessInstance startProcess(String processKey, String businessKey, HashMap variables) throws Exception {
        logger.info("开始启动流程，流程的key: "+processKey);
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(processKey, businessKey,variables);//流程定义key,业务表key，流程变量
        logger.info("流程启动成功,流程实例id "+pi.getId());
        return pi;
    }

    @Override
    public List<Task> findTasks(String processKey, String userId) {
        return null;
    }

    @Override
    public void completeTask(String taskId, String userId, String result) {

    }

    @Override
    public void updateBizStatus(DelegateExecution execution, String status) {

    }

    @Override
    public List<String> findUsersForSP(DelegateExecution execution) {
        return null;
    }

    /**
     * 生成流程图片
     */
    @Override
    public void generateImage(String deploymentId, String imageName,HttpServletResponse response) {
        InputStream in = repositoryService.getResourceAsStream(deploymentId.replace("'",""), imageName);
        try {
            OutputStream out = response.getOutputStream();
            // 把图片的输入流程写入resp的输出流中
            byte[] b = new byte[1024];
            for (int len = -1; (len= in.read(b))!=-1; ) {
                out.write(b, 0, len);
            }
            // 关闭流
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getCurrentActivityCoordinates(String taskId) {
        Map<String, Object> coordinates = new HashMap<String, Object>();
        // 1. 获取到当前活动的ID
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        String currentActivitiId = pi.getActivityId();
        // 2. 获取到流程定义
        ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        // 3. 使用流程定义通过currentActivitiId得到活动对象
        ActivityImpl activity =  pd.findActivity(currentActivitiId);
        // 4. 获取活动的坐标
        coordinates.put("x", activity.getX());
        coordinates.put("y", activity.getY());
        coordinates.put("width", activity.getWidth());
        coordinates.put("height", activity.getHeight());

        //如果有多个流程活动节点（并发流程一般有多个活动节点）该方法应该返回一个list，代码应该使用下面的方法
        // 得到流程执行对象
/*        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(pi.getId()).list();
        // 得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<String>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }
        List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
        for (String id : activityIds) {
            ActivityImpl activity1 = pd.findActivity(id);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("x", activity1.getX());
            map.put("y", activity1.getY());
            map.put("width", activity1.getWidth());
            map.put("height", activity1.getHeight());
            list.add(map);
        }*/

        return coordinates;
    }
}
