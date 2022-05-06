package com.wjy.servlet.web;

import com.wjy.dao.UserDAO;
import com.wjy.ioc.BeanFactory;
import com.wjy.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月07日 13:56:00
 */
@WebServlet(urlPatterns = "/webmvcDemo")
public class ServletMvcDemo extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = (UserService) BeanFactory.getBean("userServiceImpl");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(userService);
        response.getWriter().println(userService.getUsers());
    }
}
