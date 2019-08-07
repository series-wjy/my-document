package com.wjy.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootMailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMailApplication.class, args);
	}

}
/**
 * 邮件发送失败，重发邮件实现步骤：
 * 1、接收到发送邮件请求，首先记录请求并且入库。
 * 2、调用邮件发送接口发送邮件，并且将发送结果记录入库。
 * 3、启动定时系统扫描时间段内，未发送成功并且重试次数小于3次的邮件，进行再次发送
 *
 * 异步发送：
 * 很多时候邮件发送并不是我们主业务必须关注的结果，比如通知类、提醒类的业务可以允许延时或者失败。
 * 这个时候可以采用异步的方式来发送邮件，加快主交易执行速度，在实际项目中可以采用MQ发送邮件相关参数，
 * 监听到消息队列之后启动发送邮件。
 *
 */