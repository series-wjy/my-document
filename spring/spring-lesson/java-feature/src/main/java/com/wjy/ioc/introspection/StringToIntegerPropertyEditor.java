package com.wjy.ioc.introspection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;

/**
 * @ClassName StringToIntegerPropertyEditor.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年06月04日 11:30:00
 */
public class StringToIntegerPropertyEditor extends PropertyEditorSupport {

    public StringToIntegerPropertyEditor(Object bean) {
        super(bean);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Integer value = Integer.valueOf(text);
        setValue(value);
    }
}
