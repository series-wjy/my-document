package com.wjy.dependency.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Component
public class CompanyByAnnotation {

    @Value("#{new String[] {'laoma', 'laoniu', 'laoyang'}}")
    private String[] staffNames;
    @Value("#{{'13800000000', '13811111111', '13822222222'}}")
    private List<String> tels;
    @Value("#{{casualWorker, new com.wjy.dependency.model.User()}}")
    private Set<User> staffs;
    @Value("#{{'老候':2, '老猪':'老猪能吃能睡', '老狗':new com.wjy.dependency.model.User(), '老猫':casualWorker}}")
    private Map<String, Object> events;
    @Value("#{{'name':'这是一个名字', 'age':'35是个尴尬的年龄'}}")
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
