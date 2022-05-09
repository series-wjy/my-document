package com.gblw.aop;

import com.gblw.bf.BeanFactory;
import com.gblw.service.DemoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@WebServlet(urlPatterns = "/demo/aop")
public class DemoAopServlet extends HttpServlet {

    private DemoService demoService;

    @Override
    public void init() throws ServletException {
        DemoService service = (DemoService) BeanFactory.getBean("demoService");
        demoService = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.getWriter().println(demoService.findAll().toString());
    }
    
}