package com.szaisiou.controller;

import com.szaisiou.common.utils.IdGen;
import com.szaisiou.dao.EmployMapper;
import com.szaisiou.dao.LeaveBillMapper;
import com.szaisiou.entity.ActivitiVo;
import com.szaisiou.entity.Employ;
import com.szaisiou.entity.LeaveBill;
import com.szaisiou.service.ActivityService;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *流程操作
 *
 */
@Controller
@RequestMapping("/flows")
public class FlowsController {

    private static final Logger logger = LoggerFactory.getLogger(FlowsController.class);

    @Autowired
    private RepositoryService repositoryService;

    @Resource(name = "ActivityService")
    private ActivityService activityService;

    @Autowired
    private LeaveBillMapper leaveBillMapper;

    @Autowired
    private EmployMapper employMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private HistoryService historyService;


    //*************流程模型***************************
    /**
     * 这边显示所有已经部署了的流程模型
     * 后面可以做权限控制，或者不同的用户只能查看特定的流程
     *
     * @return
     */
    @RequestMapping(value = "/flowTypeList", method = RequestMethod.GET)
    public ModelAndView getFlowTypeList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        ModelAndView modelAndView = new ModelAndView("apply");
        modelAndView.addObject("resultList", list);
        return modelAndView;
    }

    //*********************申请表单*******************
    /**
     * 保存申请表单
     *
     * @param leaveBill
     * @return
     */
    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String saveForm(@ModelAttribute LeaveBill leaveBill, HttpSession session) {
        String billId = IdGen.uuid();
        logger.info("表单对象内容: " + leaveBill.toString());
        String processKey = LeaveBill.class.getSimpleName().toLowerCase();
        logger.info("流程定义key: " + processKey);
        String businessKey = processKey + "." + billId;
        //ModelAndView modelAndView = new ModelAndView(processKey);
        HashMap<String, Object> variables = new HashMap<>();
        try {

            String userId = (String) session.getAttribute("userId");
            //验证session是否过期
            if(userId != null && !"".equals(userId)){
                //设置办理人
               // variables.put("starter",session.getAttribute("user"));//从session中取到流程发起人
                //启动流程
              //  activityService.startProcess(processKey,businessKey,variables);
                //录入表单信息
                leaveBill.setId(billId);
                leaveBill.setEmpId(userId);
                leaveBill.setStatus(0);
                leaveBill.setCreatTime(Calendar.getInstance().getTime());
                leaveBillMapper.addLeaveBillRecord(leaveBill);

               // modelAndView.setViewName("bills");

                return "redirect:/flows/bills";

//                //完成任务
//                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processKey)
//                        .latestVersion().singleResult();
//                String defId = processDefinition.getId();
//                logger.info("流程定义id"+ defId);
//                //通过流程定义id,查询正在运行任务id
//                Task task = taskService.createTaskQuery().processInstanceId().singleResult();
//                taskService.complete(task.getId());
            }else{
               // modelAndView.setViewName("login");
               // modelAndView.addObject("message","请重新登录!");
                return "redirect:/models/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 通过表单的类型定位不同的页面
     * 需要流程的key和表单的名称保持一致
     *
     * @param processdefKey
     * @return
     */
    @RequestMapping(value = "/pageComplete/{id}", method = RequestMethod.GET)
    public String completePage(@PathVariable(value = "id") String processdefKey) {
        logger.info("表单页面:" + processdefKey);
        return processdefKey;
    }

    /**
     * 模板引擎对于 实体是日期格式，传入字符串报错,允许传入空值
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

    }

    /**
     * 查询当前用户的申请单
     *
     * @return
     */
    @RequestMapping(value = "/bills", method = RequestMethod.GET)
    public ModelAndView pendingTask(ModelAndView mv,HttpSession session) {
        logger.info("表单详情...");
        LinkedList<LeaveBill> leaveBills = new LinkedList<>();
        String userId = (String)  session.getAttribute("userId");
        //session过期为判断... 应该需要全局拦截，session过期之后统一跳转到登录页面
        //获取查询表单列表数据
        List<LeaveBill> bills = leaveBillMapper.selectRelation(userId);
        //获取流程定义的key
        String processKey = LeaveBill.class.getSimpleName().toLowerCase();
        if(bills != null && bills.size()>0){
            for(LeaveBill leaveBill : bills){
                String businessKey = processKey + "." + leaveBill.getId();
                leaveBill.setKey(processKey);
                leaveBill.setBusinessKey(businessKey);
                leaveBills.add(leaveBill);
            }
        }
        mv.addObject("bills",leaveBills);
        mv.setViewName("bills");
        return mv;
    }

    //*******************流程定义*******************
    /**
     * 删除流程定义
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public @ResponseBody String deleteFlowDefinition(@PathVariable(value = "id") String id) {
        String msg = "删除失败";
        logger.info("本次需要删除的流程定义的部署id:"+id);
        try {
            repositoryService.deleteDeployment(id,true);
            msg = "删除成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }


    //************** 流程实例**********************
    /**
     * 启动流程实例
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public @ResponseBody String startFlowInstance(@ModelAttribute LeaveBill leaveBill,HttpSession session) {
        String msg = "失败";
        logger.info("启动流程实例的参数..."+leaveBill);
        try {
            //获取当前用户
            String username = (String) session.getAttribute("user");
            //流程发起人
            identityService.setAuthenticatedUserId(username);
            // 设置批注
            //设置流程变量
            HashMap<String, Object> variable = new HashMap<>();
            variable.put("starter",username);//获取session中的用户名
            //启动流程
            ProcessInstance processInstance = activityService.startProcess(leaveBill.getKey(), leaveBill.getBusinessKey(), variable);
            leaveBill.setStatus(1);
            leaveBillMapper.updateLeaveBillRecord(leaveBill);
            //完成任务 提交申请阶段
            //Authentication.setAuthenticatedUserId(username);
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            taskService.addComment(task.getId(),task.getProcessInstanceId(),leaveBill.getRemark());
            taskService.complete(task.getId(),variable);
            msg = "成功";
        }catch (Exception e){
            e.printStackTrace();
            logger.info("启动失败");
            msg = "启动异常";
        }
        return msg;
    }

    /**
     * 查询待办任务列表
     * @return
     */
    @RequestMapping(value = "/pendingTask", method = RequestMethod.GET)
    public ModelAndView taskList(HttpSession session) {
        String currentUser = (String) session.getAttribute("user");
        //查询当前用户待办任务列表
        List<Task> list = taskService.createTaskQuery().taskAssignee(currentUser).orderByTaskCreateTime().desc().list();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("pending");
        mv.addObject("tasks",list);
        return mv;
    }

    /**
     * 开始处理任务--》处理任务页面
     *
     * @return
     */
    @RequestMapping(value = "/handle/{id}", method = RequestMethod.GET)
    public ModelAndView taskHandle(@PathVariable(value = "id") String id) {
        logger.info("流程实例id :"+id);
        List<ActivitiVo> historyCommnets = new ArrayList<>();
        ModelAndView mv = new ModelAndView();
        //执行对象id97508  任务id 97515
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
        if(processInstance != null && !"".equals(processInstance)) {
            String businessKey = processInstance.getBusinessKey();
            logger.info("业务key" + businessKey);
            String bizId = "";
            if (businessKey != null && !"".equals(businessKey)) {
                bizId = businessKey.split("\\.")[1];
            }
            logger.info("业务表id" + id);
            //通过id去查询 表单表数据回显
            LeaveBill leaveBill = leaveBillMapper.selectLeaveBillRecord(bizId);
            //查询此流程实例的审批历史
//        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(id).finished()
//                .orderByHistoricActivityInstanceStartTime().asc().list();//所有活动节点
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(id).finished().orderByHistoricTaskInstanceEndTime().asc().list();
            //第二种方式
            //List<Comment> processInstanceComments = taskService.getProcessInstanceComments(id);
            //查询批注
            if (list != null && list.size() > 0) {
                for (HistoricTaskInstance hi : list) {
                    String taskId = hi.getId();
                    List<Comment> comments = taskService.getTaskComments(taskId);
                    if (comments != null && comments.size() > 0) {
                        for (Comment c : comments) {
                            //批注信息封装成前端对象
                            ActivitiVo vo = new ActivitiVo();
                            //获取批注内容的taskId
                            String taskId1 = c.getTaskId();
                            //根据taskId去查询任务节点
                            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId1).singleResult();
                            String name = historicTaskInstance.getName();
                            //设置值
                            vo.setTaskId(c.getTaskId());
                            vo.setTaskName(name);
                            vo.setCreateTime(c.getTime());
                            vo.setUserName(c.getUserId());
                            vo.setRemark(c.getFullMessage());
                            historyCommnets.add(vo);
                        }
                    }
                }
            }
            mv.addObject("bill", leaveBill);
            mv.addObject("taskHistorys", historyCommnets);
            mv.addObject("instanceId", processInstance.getId());
        }
        mv.setViewName("handle");
        return mv;
    }

    /**
     * 查询历史任务或者说个人已办
     *
     * @return
     */
    @RequestMapping(value = "/finishTask", method = RequestMethod.GET)
    public ModelAndView taskFinish(HttpSession session) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee((String) session.getAttribute("user")).finished().orderByHistoricTaskInstanceEndTime().desc().list();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("finishtask");
        mv.addObject("hisTasks",list);
        return mv;
    }

    /**
     * 审批
     *
     * @return
     */
    @RequestMapping(value = "/handlefinish", method = RequestMethod.POST)
    public @ResponseBody String  handleFinish(String instanceId,String comment,String id,HttpSession session) {
        String msg = "失败";
        logger.info("流程实例id : 批注信息 "+instanceId+":"+comment);
        try {
            //查询正在运行的task
            Task task = taskService.createTaskQuery().processInstanceId(instanceId).singleResult();
            String taskId = task.getId();
            //activity底层代码使用 认证 来设置当前的评论人
            String currentUserName = (String) session.getAttribute("user");
            Authentication.setAuthenticatedUserId(currentUserName);
            //添加批注
            taskService.addComment(taskId,instanceId,comment);
            //完成任务，可以设置流程变量
            taskService.complete(taskId);
            //完成任务之后，判断流程是否已经结束，需要更新申请单状态
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            if(pi == null){
                LeaveBill leaveBill = new LeaveBill();
                leaveBill.setStatus(3);//'3' 表示已经完成
                leaveBill.setId(id);
                leaveBill.setFinishTime(Calendar.getInstance().getTime());
                leaveBillMapper.updateLeaveBillRecord(leaveBill);
            }
            msg = "成功";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "处理异常";
        }
        return msg;
    }

    /**
     * 查看流程图，并显示活动节点
     */
    @RequestMapping(value = "/viewCurrentImage",method = RequestMethod.GET)
    public ModelAndView viewCurrentImage(@RequestParam(value = "id") String taskId,ModelAndView mv){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        // 1. 获取流程部署ID
        mv.addObject("deploymentId", pd.getDeploymentId());
        // 2. 获取流程图片的名称
        mv.addObject("imageName", pd.getDiagramResourceName());

        // 3.获取当前活动的坐标
        Map<String,Object> currentActivityCoordinates =activityService.getCurrentActivityCoordinates(taskId);
        mv.addObject("acs", currentActivityCoordinates);
        mv.setViewName("flowimage");
        return mv;
    }

    /**
     * 生成图片
     */
    @RequestMapping(value = "/viewImage",method = RequestMethod.GET)
    public void viewImage(@RequestParam(value = "deploymentId") String deploymentId, @RequestParam(value = "imageName") String imageName, HttpServletResponse response){
        activityService.generateImage(deploymentId,imageName,response);
    }

    /**
     * 显示所有的流程实例
     */
    @RequestMapping(value = "/processInstance",method = RequestMethod.GET)
    public ModelAndView processInstance(ModelAndView mv){
        LinkedList<ActivitiVo> result = new LinkedList<>();
        //查询所有的申请单据
        List<LeaveBill> leaveBills = leaveBillMapper.selectLeaveBillRecordlist();
        if(leaveBills !=null && leaveBills.size()>0){
            for(LeaveBill leaveBill : leaveBills){
                ActivitiVo vo = new ActivitiVo();
                Employ employ = new Employ();
                employ.setId(leaveBill.getEmpId());
                String userName = employMapper.findEmploy(employ).getName();
                vo.setId(leaveBill.getId());
                vo.setUserName(userName);
                vo.setRemark(leaveBill.getRemark());
                vo.setReason(leaveBill.getReason());
                vo.setCreateTime(leaveBill.getCreatTime());
                Date finishTime = leaveBill.getFinishTime();
                String status = "进行中";
                if(finishTime != null){
                    status = "结束";
                }
                vo.setStatus(status);
                result.add(vo);
            }
        }
        //返回给前端
        mv.addObject("result",result);
        mv.setViewName("processInstance");
        return mv;
    }

}
