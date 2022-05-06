package com.gblw.conditional.v3.app;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.gblw.conditional.v3.annotation.EnableJdbc;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * 测试条件装配
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 09:58:00
 */
@Configuration
@EnableJdbc
@PropertySource("jdbc/jdbc.properties")
public class EnableJdbcApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EnableJdbcApplication.class);
//        context.scan("com.gblw.cond");
//        context.refresh();
        DruidDataSource bean = context.getBean(DruidDataSource .class);
        System.out.println(bean.getUrl());
        try {
            DruidPooledConnection connection = bean.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user");
            ResultSet rs = preparedStatement.executeQuery();
            while (true) {
                boolean b = rs.next();
                if ( false == b ) {
                    break;
                }
                String userName = rs.getString( "name" );
                String userPassword = rs.getString( 3 );
                System.out.println( userName + " : " + userPassword );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        Arrays.stream(beanDefinitionNames).forEach(System.out :: println);
    }
}
