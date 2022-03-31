package com.wjy.spring.ioc.overview.domain;

import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName User.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 用户类
 * @Create 2020年06月02日 20:59:00
 */
public class User {
    private Long id;
    private String name;
    private City city;
    private City[] workCities;
    private List<City> lifCities;
    private Resource resource;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Resource getResource() {
        return resource;
    }

    public City[] getWorkCities() {
        return workCities;
    }

    public void setWorkCities(City[] workCities) {
        this.workCities = workCities;
    }

    public List<City> getLifCities() {
        return lifCities;
    }

    public void setLifCities(List<City> lifCities) {
        this.lifCities = lifCities;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city=" + city +
                ", workCities=" + Arrays.toString(workCities) +
                ", lifCities=" + lifCities +
                ", resource=" + resource +
                '}';
    }

    /**
     * 静态工厂方法
     * @return
     */
    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("隔壁老王");
        return user;
    }
}
