package com.bjhy.collect.springmvc;

import com.bjhy.collect.interceptor.MonitorMethodInterceptor;
import com.bjhy.collect.kafka.KafkaClientTemplate;
import com.bjhy.collect.util.GsonUtil;
import com.bjhy.collect.util.LocalIpUtil;
import com.bjhy.collect.util.RequestAddrUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.SocketException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;


/**
 * @ClassName ControllerInterceptor.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 拦截Controller的方法
 * @Create 2019年1月3日 16:25:00
 */
public class ControllerInterceptor implements MonitorMethodInterceptor<InvocationLog> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String server_ip;

    private String application_name = null;

    public ControllerInterceptor() {
        Optional<Inet4Address> opt = null;
        try {
            opt = LocalIpUtil.getLocalIp4Address();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        server_ip = opt.get().getHostAddress();
    }

    @Override
    public InvocationLog beforeMethod(Method method, Object[] allArguments) {
        InvocationLog log = new InvocationLog();
        log.setHost(server_ip);
        log.setMethod(method.getName());
        log.setParams(Arrays.asList(allArguments).toString());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return null;
        }
        if(application_name == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletRequestAttributes.getRequest().getServletContext());
            application_name = context.getEnvironment().getProperty("spring.application.name");
        }

        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        if (httpServletRequest == null) {
            return null;
        }

        String httpUrl = httpServletRequest.getRequestURI();
        log.setType(httpServletRequest.getContentType());
        log.setServiceName(application_name);
        log.setUrl(httpUrl);
        log.setRemote(RequestAddrUtil.getIpAddress(httpServletRequest));
        log.setCallTime(LocalDateTime.now()
                .format(formatter));
        return log;
    }

    @Override
    public Object afterMethod(Method method, Object[] allArguments, Object ret, InvocationLog span) {
        LocalDateTime callTime = LocalDateTime.parse(span.getCallTime(), formatter);
        Duration duration = Duration.between(callTime, LocalDateTime.now());
        span.setDuration(String.valueOf(duration.toMillis()));
        KafkaClientTemplate.sendToKafka("invocation_log_topic", GsonUtil.objectToJson(span));
        System.out.println("========>>>>>：" + GsonUtil.objectToJson(span));
        return ret;
    }

    @Override
    public void handleMethodException(Method method, Object[] allArguments, Throwable throwable, InvocationLog span) {

    }
}
