package com.wjy;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.turbine.streaming.servlet.TurbineStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableTurbine
@EnableHystrixDashboard
public class SpringCloudHystrixDashboardTurbineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudHystrixDashboardTurbineApplication.class, args);
	}

//	@Bean
//	public ServletRegistrationBean getHystrixMetricsStreamServlet(){
//		HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
//		ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
//		registrationBean.setLoadOnStartup(1);
//		registrationBean.addUrlMappings("/actuator/hystrix.stream");
//		registrationBean.setName("HystrixMetricsStreamServlet");
//		return registrationBean;
//	}

	@Bean
	public ServletRegistrationBean getTurbineStreamServlet(){
		TurbineStreamServlet streamServlet = new TurbineStreamServlet();
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/turbine.stream");
		registrationBean.setName("TurbineStreamServlet");
		return registrationBean;
	}

}
