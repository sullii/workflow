package com.szaisiou.service.taskListener;

import com.szaisiou.config.SpringContextHolder;
import com.szaisiou.dao.EmployMapper;
import com.szaisiou.dao.LeaveBillMapper;
import com.szaisiou.entity.Employ;
import com.szaisiou.entity.LeaveBill;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 任务监听 分配任务处理人
 * 问题：本类中无法注入容器内部的对象，session和 mapper对象都无法注入
 * 解决：通过实现 implements ApplicationContextAware, DisposableBean 提供获取spring容器中的方法
 *
 * @author  suli
 */
@Component
public class TaskListenerImpl implements TaskListener{

    private static final Logger logger = LoggerFactory.getLogger(TaskListenerImpl.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        //获取session
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        //获取mapper
        EmployMapper employMapper = (EmployMapper) SpringContextHolder.getBean(EmployMapper.class);
        LeaveBillMapper leaveBillMapper = (LeaveBillMapper) SpringContextHolder.getBean(LeaveBillMapper.class);
        String id = (String) session.getAttribute("userId");//session中获取当前用户id
        //任务触发后更新表单状态
//        LeaveBill leaveBill = new LeaveBill();
//        leaveBill.setEmpId(id);
//        leaveBill.setStatus(2);
//        leaveBillMapper.updateLeaveBillRecord(leaveBill);
        //设置办理人
        Employ employ = new Employ();
        employ.setId(id);
        String managerId = employMapper.findEmploy(employ).getManagerId();
        employ.setId(managerId);
        String name = employMapper.findEmploy(employ).getName();
        logger.info("任务处理人: "+name);
        delegateTask.setAssignee(name);
    }
}
