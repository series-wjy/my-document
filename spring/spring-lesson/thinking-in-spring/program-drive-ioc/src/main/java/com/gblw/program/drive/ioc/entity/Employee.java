package com.gblw.program.drive.ioc.entity;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月14日 08:56:00
 */
public class Employee {

    private String name;

    public Employee() {
        System.out.println("Employee has been created......");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
