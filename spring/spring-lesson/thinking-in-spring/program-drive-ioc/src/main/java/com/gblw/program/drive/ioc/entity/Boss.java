package com.gblw.program.drive.ioc.entity;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月14日 08:56:00
 */
public class Boss {

    private String name;

    private Employee employee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Boss{");
        sb.append("name='").append(name).append('\'');
        sb.append(", employee=").append(employee);
        sb.append('}');
        return sb.toString();
    }
}
