package com.szaisiou.controller;

import com.szaisiou.dao.EmployMapper;
import com.szaisiou.entity.Employ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2019/6/12.
 */
@Controller
@RequestMapping("/user")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private EmployMapper employMapper;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String password,
                        HttpSession session, Model model) {
        logger.info("用户名:密码 " + userName + " : " + password);
        if (userName == null || "".equals(userName)) {
            model.addAttribute("message", "登录失败,请输入用户名和密码");
            logger.info("登录失败...");
            return "forward:/models/login";
        } else {
            //
            Employ employ = new Employ();
            employ.setName(userName);
            Employ em = employMapper.findEmploy(employ);
            if (em != null) {
                session.setAttribute("user", em.getName());
                session.setAttribute("userId",em.getId());
                logger.info("登录成功...");
                return "redirect:/models/index";
            } else {
                model.addAttribute("message", "登录失败,用户不存在");
                logger.info("登录失败...");
                return "forward:/models/login";
            }
        }

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session,Model model) {
        session.setMaxInactiveInterval(1000 * 60 * 60);
        System.out.println(session.getId());
        if (session.getAttribute("user") != null && session.getAttribute("user").equals(session.getId())) {
            session.removeAttribute("user");
            return "redirect:/models/login";
        }
        return "redirect:/models/login";
    }
}
