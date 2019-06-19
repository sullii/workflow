package com.szaisiou;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2019/6/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowApplicationTest.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FormService formService;

    @Test
    public  void test(){
        System.out.println("hello test...");
    }

    /**
     *
     * 部署流程模型
     */
    @Test
    public void deploy() throws Exception{
        //方法一：配置文件在项目中
//        Deployment deployment = repositoryService.createDeployment() // 创建一个部署对象
//                .name("流程定义文件2")   // 添加部署的名称
//                .addClasspathResource("process/leave2.bpmn") // 从classpath的资源文件中加载，一次只能加载一个文件
//                .deploy();// 完成部署，完成之后返回一个部署对象

        String id = "12507";
        //方式二：模型文件在模型表中
        //获取模型
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            logger.info("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }

        JsonNode modelNode = new ObjectMapper().readTree(bytes);

        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if(model.getProcesses().size()==0){
            logger.info("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy();
        System.out.println("流程部署id："+deployment.getId());
        System.out.println("部署名称："+deployment.getName());
    }

    /**
     *
     * 启动流程定义-->流程实例
     */
    @Test
    public void start(){
        String key = "test";
        //流程变量
        String userId = "张三";
        identityService.setAuthenticatedUserId(userId);
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("starter",userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);
        System.out.println("流程部署的ID："+processInstance.getDeploymentId());
        System.out.println("流程定义的ID："+processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID："+processInstance.getProcessInstanceId());
    }

    /**
     *
     * 完成任务
     */
    @Test
    public void complete(){
        String taskid = "25006";
        taskService.complete(taskid);
        System.out.println("启动成功...");
    }

    /**
     *
     * 连线
     */
    @Test
    public void line(){
        LinkedList<String> strings = new LinkedList<>();
        String processDefinitionId ="test001:1:15004";
        String processInstanceId ="17501";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String activityId = processInstance.getActivityId();
        ProcessDefinitionEntity entity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
        ActivityImpl activity = entity.findActivity(activityId);
        List<PvmTransition> l = activity.getOutgoingTransitions();
        if(l !=null && l.size()>0){
            for(PvmTransition pvmTransition:l){
                String name = (String)pvmTransition.getProperty("name");
                if(name != null && !"".equals(name)){
                    strings.add(name);
                }else {
                    strings.add("默认提交");
                }
            }
        }
        System.out.println("流程连线:"+strings);

    }

    /**
     *
     * 通过流程定义获取动态表单
     */
    @Test
    public void getDynamicForm(){
        String processDefinitionId = "test:1:22507";
        StartFormData startFormData = formService.getStartFormData(processDefinitionId);
        List<FormProperty> formProperties = startFormData.getFormProperties();
        if(!formProperties.isEmpty()){
            for(FormProperty f : formProperties){
                System.out.println("动态表单属性:"+f.getId()+"---"+f.getName()+"-----"+f.getValue()+"----"+f.getType());
            }
        }
        System.out.println("流程定义:"+startFormData.getProcessDefinition());


    }

}
