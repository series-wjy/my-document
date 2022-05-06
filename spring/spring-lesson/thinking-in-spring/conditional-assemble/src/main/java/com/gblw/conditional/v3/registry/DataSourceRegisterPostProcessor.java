package com.gblw.conditional.v3.registry;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.sql.Driver;
import java.util.List;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 15:02:00
 */
public class DataSourceRegisterPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
        builder.addPropertyValue("url", environment.getProperty("jdbc.url"));
        builder.addPropertyValue("username", environment.getProperty("jdbc.username"));
        builder.addPropertyValue("password", environment.getProperty("jdbc.password"));

        List<String> driverClasses = SpringFactoriesLoader.loadFactoryNames(Driver.class, this.getClass().getClassLoader());
        for(String name : driverClasses) {
            try {
                Object driver = Class.forName(name).getDeclaredConstructor().newInstance();
//                builder.addPropertyValue("driver", driver);
                builder.addPropertyValue("driverClassName", name);
                break;
            } catch (Exception e) {
            }
        }
        registry.registerBeanDefinition("dataSource", builder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
