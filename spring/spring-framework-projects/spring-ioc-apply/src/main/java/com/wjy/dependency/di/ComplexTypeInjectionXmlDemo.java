package com.wjy.dependency.di;

import com.wjy.dependency.model.CompanyByAnnotation;
import com.wjy.dependency.model.CompanyByXml;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 复杂类型注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月23日 17:11:00
 */
public class ComplexTypeInjectionXmlDemo {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("basic_dl/complex-type-injection.xml");
        CompanyByXml company = context.getBean("company", CompanyByXml.class);
        System.out.println(company);

        CompanyByAnnotation companyByAnnotation = context.getBean("companyByAnnotation", CompanyByAnnotation.class);
        System.out.println(companyByAnnotation);
    }
}
