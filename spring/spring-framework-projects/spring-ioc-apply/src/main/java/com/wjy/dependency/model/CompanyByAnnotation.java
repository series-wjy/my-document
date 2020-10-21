package com.wjy.dependency.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月23日 17:03:00
 */
public class Company {

    private String[] staffNames;
    private List<String> tels;
    private Set<User> staffs;
    private Map<String, Object> events;
    private Properties props;

    public String[] getStaffNames() {
        return staffNames;
    }

    public void setStaffNames(String[] staffNames) {
        this.staffNames = staffNames;
    }

    public List<String> getTels() {
        return tels;
    }

    public void setTels(List<String> tels) {
        this.tels = tels;
    }

    public Set<User> getStaffs() {
        return staffs;
    }

    public void setStaffs(Set<User> staffs) {
        this.staffs = staffs;
    }

    public Map<String, Object> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Object> events) {
        this.events = events;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    @Override
    public String toString() {
        return "Company{" +
                "staffNames=" + Arrays.toString(staffNames) +
                ", tels=" + tels +
                ", staffs=" + staffs +
                ", events=" + events +
                ", props=" + props +
                '}';
    }
}
